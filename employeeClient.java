import java.io.*;
import java.net.*;

public class employeeClient extends Thread{
    String host;
    int port;
    public employeeClient(String host,int port){
        this.host=host;
        this.port=port;
    }
    public void run(){
        try{
            Socket employee=new Socket(host,port);

            PrintWriter out =new PrintWriter(employee.getOutputStream(), true);
            BufferedReader in =new BufferedReader(new InputStreamReader(employee.getInputStream()));
            out.println("employee");
            int methodNumber=0;
            String line;
            while((line=in.readLine())!=null){
                System.out.println("employee: "+line);
                if(methodNumber<3){ //send 3 cases but only if they have received it
                    out.println(methodNumber);
                    try{sleep(1000);}
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