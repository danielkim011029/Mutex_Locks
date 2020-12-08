import java.io.*;
import java.net.*;
import java.util.Random;

public class managerClient extends Thread{
    String host;
    int port;
    public managerClient(String host,int port){
        this.host=host;
        this.port=port;
    }
    public void run(){
        try{
            Socket managerSocket=new Socket(host,port);

            PrintWriter out =new PrintWriter(managerSocket.getOutputStream(), true);
            BufferedReader in =new BufferedReader(new InputStreamReader(managerSocket.getInputStream()));
            String line;
            int methodNumber=0;
            out.println("manager"); //send that I am a manager
            while((line=in.readLine())!=null){  
                System.out.println("manager: "+line);
                if(methodNumber<3){     //send 3 cases but only if they have received it
                    out.println(methodNumber);
                    try{sleep(3000);}
                    catch(InterruptedException e){};
                }
                methodNumber++;
            }
        }
        catch (Exception e)
		{
			e.printStackTrace();
		}
    }
}