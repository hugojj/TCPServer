import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Server
{
    public static final int LISTEN_PORT = 5987;
    
    public void listenRequest()
    {
        ServerSocket serverSocket = null;
        ExecutorService threadExecutor = Executors.newCachedThreadPool();
        try
        {
            serverSocket = new ServerSocket( LISTEN_PORT );
            System.out.println("Server listening requests...");
            while ( true )
            {
                Socket socket = serverSocket.accept();
                threadExecutor.execute( new RequestThread( socket ) );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( threadExecutor != null )
                threadExecutor.shutdown();
            if ( serverSocket != null )
                try
                {
                    serverSocket.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
        }
    }
    
   
    public static void main( String[] args )
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Server server = new Server();
        server.listenRequest();
    }
    
    class RequestThread implements Runnable
    {
        private Socket clientSocket;
    	private static final int buffer_size = 1048576;
        
        public RequestThread( Socket clientSocket )
        {
            this.clientSocket = clientSocket;
        }
        
        @Override
        public void run()
        {
            System.out.printf("有%s連線進來!\n", clientSocket.getRemoteSocketAddress() );
            DataInputStream input = null;
            DataOutputStream output = null;
            
            try
            {

                output = new DataOutputStream( this.clientSocket.getOutputStream() );


                while ( true && this.clientSocket.isConnected() )
                {
                    input = new DataInputStream( this.clientSocket.getInputStream() );
                	System.out.println("receiving an image");

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    
                    int image_size = input.readInt();                    
                    int height = input.readInt();
                    int width = input.readInt();
                    int type = input.readInt();
                    byte[] imageByteArray = new byte[image_size];
                    
                    input.readFully(imageByteArray, 0, image_size);
                    out.write(imageByteArray, 0, image_size);

                                              
                    Mat mat = new Mat(height, width, type);
                    mat.put(0, 0, out.toByteArray());
                    System.out.println(height + "," + width+ "," + type + ","+image_size);
                    Highgui.imwrite(System.currentTimeMillis()+".png",mat);
                    
                    

                    

                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            finally 
            {
                try
                {
                    if ( input != null )
                        input.close();
                    if ( output != null )
                        output.close();
                    if ( this.clientSocket != null && !this.clientSocket.isClosed() )
                        this.clientSocket.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

}