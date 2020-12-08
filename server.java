import java.io.*;
import java.net.*;
public class server {
    public server(){
        try{
            ServerSocket main=new ServerSocket(17);
            Socket s=main.accept();
            //PrintWriter out=new PrintWriter(s.getOutputStream(),true);
            //out.println("HI");
        }
        catch (IOException e)
		{
			System.out.println("Unable to listen to port.");
			e.printStackTrace();
		}//catch
    }

    public static void main(String[]args){
        new server();
    }
}