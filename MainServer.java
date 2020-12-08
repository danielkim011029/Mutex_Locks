import java.net.*;
import java.io.*;

public class MainServer {
	int port;
	public MainServer(int p){
		port=p;
			try{
				ServerSocket MainServer=new ServerSocket(port);
				while(true){
					Socket s=MainServer.accept();
					SubServerThread helper=new SubServerThread(s);
					helper.start();

					//PrintWriter out =new PrintWriter(s.getOutputStream(), true);                   
					//BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}	
	}
	
	public static void main (String [] args)
	{
		int port=Integer.parseInt(args[0]);
		new MainServer(port);
	}//main
	
	
}//class
