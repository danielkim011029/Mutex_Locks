import java.io.*;
import java.net.*;

public class customerClient extends Thread{
    private int id;
    String host;
    int port;
    public customerClient(String host,int port,int i){
        this.host=host;
        this.port=port;
        id=i;
    }
    public void run(){
        try{
            Socket customerSocket=new Socket(host,port);

            PrintWriter out =new PrintWriter(customerSocket.getOutputStream(), true);
            BufferedReader in =new BufferedReader(new InputStreamReader(customerSocket.getInputStream()));
            out.println("customer");
            out.println(id);
            int methodNumber=0;
            String line;
            while((line=in.readLine())!=null){
                System.out.println("customer-"+id+" :"+line);
                if(methodNumber<4){ //send 4 cases but only if they have received it
                    out.println(methodNumber);
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