package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// Peterson algorithm class
public class Byzantine extends UnicastRemoteObject implements Byzantine_RMI, Runnable  {
    private final int failure_mode;
    private int tid;
    private int v; // input value
    private int num_nodes;
    private int r =  1; // round
    private int f; // num faulty processes
    private boolean decided = false; // consensus decision
    private boolean faulty;

    ArrayList<Integer> N_msgs = new ArrayList<Integer>();
    ArrayList<Integer> P_msgs = new ArrayList<Integer>();


    // constructor for a component of BSS
    protected Byzantine(int tid, int v, boolean faulty, int num_nodes, int faulty_proc, int failure_mode) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // create a BSS component with a certain id and bind it to the remote
        this.tid = tid;
//        this.ntid = -1;
//        this.nntid = -1;
        this.r = 1;
        this.num_nodes = num_nodes;
        this.faulty = faulty;
        this.f = faulty_proc;
        this.failure_mode =  failure_mode;
        this.v = v;
        this.decided = false;


        // bind object
        java.rmi.Naming.bind("rmi://localhost:1098/Byzantine-" + tid, this);

    }


    @Override
    public void run() {

        do {
            // notification phase

            this.broadcast(new Message("N", this.r, this.v));
            await_messages("N");

            // proposal phase
            //  get counts of support for 0 or 1
            int count_0 = (int) this.N_msgs.stream().filter(v -> v.equals(0)).count();
            int count_1 = (int) this.N_msgs.stream().filter(v -> v.equals(1)).count();

            if (count_0 >  ((this.num_nodes + this.f)/2) ) {
                this.broadcast(new Message("P", this.r, 0));
            } else if ( count_1 >  ((this.num_nodes + this.f)/2) ){
                this.broadcast(new Message("P", this.r, 1));
            }else {
                this.broadcast(new Message("?", this.r, -1));
            }

            if(this.decided){
                System.out.println("Node " + this.tid + " is finished in round " + this.r +  " with ending value v="+ this.v  );
                break;
            } else {
                // awaits required messages
                await_messages("P");
                // decision phase
                // recount messages
                count_0 = (int) this.P_msgs.stream().filter(v -> v.equals(0)).count();
                count_1 = (int) this.P_msgs.stream().filter(v -> v.equals(1)).count();

                if (count_0 >  ((this.f)) ) {
                    this.v = 0;
                    if(count_0 > 3*this.f){
                        decide(0);
                        this.decided = true;

                    }
                } else if ( count_1 >  ((this.f)) ){

                    this.v = 1;
                    if(count_1 > 3*this.f){
                        decide(1);
                        this.decided = true;

                    }

                }else {
                    this.v = (int) (Math.random()*1); // random between 0 and 1
                }
            }

            this.r++;
        }while (true);



        // decision phase
    }

    public void decide(int v ){
        return;
    }
    public  void await_messages(String type){
        if(type.equals("N")){
            while ((this.N_msgs.size() < (this.num_nodes - this.f)) ){
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (type.equals("P")){
            while ((this.P_msgs.size() < (this.num_nodes - this.f)) ){
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void broadcast(Message message) {
        Registry registry = null;

        // failure modes
        if(this.faulty){
            if(this.failure_mode == 0){
                return; //  does not send message at all


            }else if (this.failure_mode == 1){
                int coin_flip = (int) (Math.random());
                if (coin_flip == 1){
                    coin_flip = (int) (Math.random());
                    message.v = coin_flip; // send random message v
                }else{
                    return; //  dont send message
                }

            }
        }


        try {
            // load all  neighbours / completely connected network
            registry = LocateRegistry.getRegistry(1098);

            Byzantine_RMI [] neighbours = new Byzantine_RMI[this.num_nodes];

            // broadcast all messages
            for(int i = 0; i < this.num_nodes; i++){
                neighbours[i] = (Byzantine_RMI) registry.lookup("Byzantine-"+ i);
            }
            // Byzantine_RMI neighbour = (Byzantine_RMI) registry.lookup("Byzantine-"+this.neighbour);

            //System.out.println(this.tid + "this start");
            for(int i = 0; i < this.num_nodes; i++){
                //System.out.println(i);
                /*
                if(i == this.tid){
                    continue; // skip itself
                }
                */
                delay();
                neighbours[i].receive(message);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }



    }



    @Override
    public void receive(Message message) {

        if (message.type.equals("N") ) {
          //System.out.println("type" + message.type + "round" + message.round);
          this.N_msgs.add(message.v);
          return;
        }

        if (message.type.equals("P") ) {
           // System.out.println("type" + message.type + "round" + message.round);;
            this.P_msgs.add(message.v);
            return;
        }


    }

    public void delay() {
      int delay = (int) (Math.random()*2);
//        System.out.println(delay);
        try {
            Thread.sleep(delay*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printData() {
        System.out.println("tid: " + this.tid ); //+ //", ntid: " + this.ntid + ", nntid: " + this.nntid);
    }



    //    public boolean getNextFromBuffer() {
//        System.out.println(this.buffer.stream().findAny().get());
//    }
}
