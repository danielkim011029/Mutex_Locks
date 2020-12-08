import java.net.*;
import java.io.*;
import java.util.Vector;
public class SubServerThread extends Thread {
    Socket s;   //socket of current thread  
    String threadType;  //manager, employee, or customer
    String id;  //id of customers
    private boolean StoreOpen=false;    //store open or not
    private int customers;		//number of customers
	private int capacity=6;		//capacity per time 
	private int counter=0;		//counter of how many customers are in the store
	private int freeRegister=2;	//number of free registers that can be used at one time. (this is due to the fact that customers must use every other register)
	private int groupSize=1;	//counter to keep track of how many are in the group 
	private Vector queue=new Vector();	//queue of waiting customers to enter store
	private Vector waitingtoCheckout=new Vector();	//queue of waiting customers to check out
	Object lock=new Object();	//lock object of the manager
	Object lock1=new Object();	//lock object of the employee
	private boolean[] registerNumber=new boolean[4]; //number of registers in the store
	private boolean accident=true;	//accident occured
	private Object inParkinglot[]=new Object[19];	//queue of waiting customers stuck in  the parking lot	
	private int exitNumber=0;	//help customers leave in or by their id

    //constructor that accepts a socket 
    public SubServerThread(Socket s){
        this.s=s;
    }

    public void run(){
        try{
            PrintWriter out =new PrintWriter(s.getOutputStream(), true);                //send to    
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));  //read from
            threadType=in.readLine();   //read the type thread and store into threadtype
            out.println("Thread Type Received");    
            String inputline;   //string to read what the clients send
            if(threadType.equals("manager")){   //if thread type is manager
                while((inputline=in.readLine())!=null){
                    if(inputline.equals("0")){      //case 1
                        managerIshere();
                        out.println("CASE 1 FINISHED");    
                    }
                    if(inputline.equals("0")){      //case 2
                        unlockStore();
                        out.println("CASE 1 FINISHED");
                    }
                    if(inputline.equals("2")){      //case 3
                        openStore();
                        out.println("CASE 2 FINISHED");
                    }
                }
            }
            else if(threadType.equals("employee")){ //thread type is employee
                while((inputline=in.readLine())!=null){
                    if(inputline.equals("0")){
                        employeIshere();
                        out.println("CASE 0 FINISHED");
                    }
                    if(inputline.equals("1")){
                        standBy();
                        out.println("CASE 1FINISHED");
                    }
                    if(inputline.equals("2")){
                        nextinLine();
                        out.println("CASE 2 FINISHED");
                    }
                }
            }
            else if(threadType.equals("customer")){ //thread type is customer
                id=in.readLine();
                while((inputline=in.readLine())!=null){
                    if(inputline.equals("0")){
                        requestToEnter(id);
                        out.println("CASE 0 FINISHED");
                    }
                    if(inputline.equals("1")){
                        shop(id);
                        out.println("CASE 1 FINISHED");
                    }
                    if(inputline.equals("2")){
                        exitStore(id);
                        out.println("CASE 2FINISHED");
                    }
                    if(inputline.equals("3")){
                        exitParkinglot(id);
                        out.println("CASE 3 FINISHED");
                    }
                }
                
            }
        }
        catch (Exception e)
			{
				e.printStackTrace();
			}	  

    }
    public void managerIshere(){
        System.out.println("MANAGER HAS ARRIVED");
    }
    public void unlockStore(){
        StoreOpen=true;
        System.out.println("MANAGER OPENED STORE");
    }
    public void openStore() {
		//if store is empty 
		if(counter==0) {
			//if enough people are in line to enter store,6 in this case
			if(queue.size()>=capacity) {
				for(int i=0;i<capacity;i++) {
					synchronized(queue.elementAt(0)) {
						queue.elementAt(0).notify();
						queue.removeElementAt(0);
					}
				}
			}
			else {
				for(int i=0;i<queue.size();i++) {
					synchronized(queue.elementAt(0)) {
						queue.elementAt(0).notify();
						queue.removeElementAt(0);
					}
				}
			}
		}
        //if waiting line to enter store is empty, manager terminates
        
		if(queue.size()==0) {
			System.out.println("MANAGER TERMINATED");
        }
        
		//if there are still others in the line, manager waits for a signal from customers.
		else {
            System.out.println("MANAGER SIGNALING");
			synchronized(lock) {
				while(true) {
					try {lock.wait(); break;}
					catch(InterruptedException e) {continue;}
				}
			}
			//recursive method so manager dosnt deadock
			openStore(); 
		}
	}
	//customers initial enter state
	public void requestToEnter(String n) {
        System.out.println("CUSTOMER-"+n+" ARRIVED");
        Object convey=new Object();
        /*
		synchronized(convey){
			queue.addElement(convey);
			while(true) {
				try {convey.wait(); break;}
				catch(InterruptedException e) {continue;}
			}
        }
        synchronized(lock){
            lock.notify();
        }
        */
		enterStore(n);
	}
	
	
	public synchronized void enterStore(String n) {
        counter++;
        System.out.println("CUSTOMER-"+n+" ENTERED STORE");
	}
	
	public void shop(String n) {
        System.out.println("CUSTOMER-"+n+" SHOPPING");
		checkOut(n);
	}
	
	public void checkOut(String n) {
		//if there are no more free registers wait customers
		if(freeRegister==0) {
			Object convey=new Object();
            waitingtoCheckout.addElement(convey);
            /*
			synchronized(convey) {
				while(true) {
					try {convey.wait(); break;}
					catch(InterruptedException e) {continue;}
				}
            }
            */
		}
		//otherwise take a register.
		freeRegister--;
        //int myRegister=calculateRegisterNumber(); 	//calculating which register is safe to use
		System.out.println(n+" CHECKING OUT AT REGISTER NUMBER: "+2);
		endCheckout(n,2);
	}
	
	public synchronized void endCheckout(String n,int r) {
		freeRegister++;
		registerNumber[r]=false;	//sets that register free
		synchronized(lock1) {
			lock1.notify();
		}
	}
	//calculates which register number to use
	public int calculateRegisterNumber() {
		int output=0;
		for(int i=0;i<registerNumber.length;i++) {
			if(i==0) {
				if(registerNumber[i]==false&&registerNumber[i+1]==false) {
					output=i;
					registerNumber[i]=true;
				}
			}
			else if(i==1||i==2) {
				if(registerNumber[i]==false&&registerNumber[i+1]==false&&registerNumber[i-1]==false) {
					output=i;
				}
			}
			else if(i==3) {
				if(registerNumber[i]==false&&registerNumber[2]==false) {
					output=i;
				}
			}
		}
		return output;
	}
    //employee waits until people want to check out
    public void employeIshere(){
        System.out.println("EMPLOYEE ARRIVED");
    }
    public void standBy(){
        System.out.println("EMPLOYEE IS ON STANDBY");
    }
	public void nextinLine() {
        System.out.println("EMPLOYEE WAITING");
		if(waitingtoCheckout.size()>0&&freeRegister>0) {
			synchronized(waitingtoCheckout.elementAt(0)) {
				waitingtoCheckout.elementAt(0).notify();
				waitingtoCheckout.removeElementAt(0);
			}
		}
		synchronized(lock1) {
			while(true) {
				try {lock1.wait(); break;}
				catch(InterruptedException e) {continue;}
			}
		}
		//if line is empty and store is empty accident is resolved
		if(queue.size()==0&&counter==0) {
				synchronized(inParkinglot[exitNumber]) {
					inParkinglot[exitNumber].notify();
				}
			
		}
		//recursive method to avoid deadlock
		else {
			nextinLine(); 
		}
	}
	//customers leaving the store, signals manager and employee before they leave.
	public  void exitStore(String n) {
        System.out.println("CUSTOMER-"+n+" EXIT STORE");
		counter--;
		if(queue.size()>0&&counter==0) {
			synchronized(lock) {
				lock.notify();
			}
		}
		if(queue.size()==0&&counter==0) {
			synchronized(lock1) {
				lock1.notify();
			}
			accident=false;
			
		}
	}
	//if there is an accident, customers must wait until signaled
	public void exitParkinglot(String n) {
		if(accident) {
			int number=Integer.parseInt(n.substring(9));
			Object convey=new Object();
			inParkinglot[number]=convey;
			synchronized(convey) {
				while(true) {
					try {convey.wait(); break;}
					catch(InterruptedException e) {continue;}
				}
			}
		}
		//otherwise leave the parking lot by their ID
		System.out.println(n+": EXITING PARKING LOT");
		exitNumber++;
		if(exitNumber<inParkinglot.length&&inParkinglot[exitNumber]!=null) {
			synchronized(inParkinglot[exitNumber]) {
				inParkinglot[exitNumber].notify();
			}
		}
		
	}
}