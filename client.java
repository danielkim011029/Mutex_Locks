import java.net.*;
import java.io.*;
public class client extends Thread {
    public void run(){
        try{
            Socket s=new Socket("192.168.0.11",17);
            //BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            //System.out.println(in.readLine());
        }
        catch (IOException e)
		{
			System.out.println("Unable to listen to port.");
			e.printStackTrace();
		}//catch
    }
    public static void main(String[]args){
        client c=new client();
        c.start();
    }
}