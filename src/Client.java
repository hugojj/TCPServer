import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Client
{
    public static void main( String[] args ) throws IOException
    {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        String host = "";
        int port = 5987;
        Socket socket = null;
        Scanner consoleInput = new Scanner( System.in );
        System.out.println("½Ð¿é¤JServerºÝ¦ì§}");
        host = consoleInput.nextLine();
        try
        {
            socket = new Socket( host, port );
            DataInputStream input = null;
            DataOutputStream output = null;
            
            try
            {
                input = new DataInputStream( socket.getInputStream() );
                output = new DataOutputStream( socket.getOutputStream() );


                while(true){
                	
                    File file = new File("HAHA.png");
                    Mat m = Highgui.imread(file.getAbsolutePath());
                    System.out.println(m.channels()*m.total() + " " + m.width() + " " + m.height() + " " + m.type());
                    

                    byte[] data = new byte[(int) (m.total() * m.channels())];
                    m.get(0, 0, data);
                    Highgui.imwrite(System.currentTimeMillis()+"_h.png",m);            
                	output.writeInt((int)data.length);
                    output.writeInt((int)m.size().height);
                    output.writeInt((int)m.size().width);
                    output.writeInt(m.type());
                    output.write(data);
                    

                }

                
            }
            catch ( IOException e )
            {
            }
            finally 
            {
                if ( input != null )
                    input.close();
                if ( output != null )
                    output.close();
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( socket != null )
                socket.close();
            if ( consoleInput != null )
                consoleInput.close();
        }
    }
}