public class main {
    public static void main(String args[]){
        String host=args[0];
        int port=Integer.parseInt(args[1]);
        //start of all the clients
        managerClient m=new managerClient(host,port);
        m.start();

        employeeClient e=new employeeClient(host,port);
        e.start();

        for(int i=0;i<10;i++){
            customerClient c=new customerClient(host,port,i);
            c.start();
        }
    }
}