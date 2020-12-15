package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

// Peterson algorithm class
public class Byzantine extends UnicastRemoteObject implements Byzantine_RMI, Runnable  {
    private int tid;
    private int v; // input value
    private int num_nodes;
    private boolean passive;
    private boolean elected;
    private String neighbour;
    private boolean receivedNtid;
    private boolean receivedNntid;
    private Object Byzantine_RMI;


    // constructor for a component of BSS
    protected Byzantine(String neighbour, int tid) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // create a BSS component with a certain id and bind it to the remote
        this.tid = tid;
//        this.ntid = -1;
//        this.nntid = -1;
        this.passive = false;
        this.elected = false;
        this.receivedNtid = false;
        this.receivedNntid = false;
        this.neighbour = neighbour;
        java.rmi.Naming.bind("rmi://localhost:1098/Byzantine-" + tid, this);

    }


    @Override
    public void run() {
        this.send(new Message(this.tid,"ntid"));
    }

    public void send(Message message) {
        Registry registry = null;
        try {
            // load all  neighbours / completely connected network
            registry = LocateRegistry.getRegistry(1098);

            Byzantine_RMI [] neighbours = new Byzantine_RMI[this.num_nodes];

            // broadcast all messages
            for(int i = 0; i < this.num_nodes; i++){
                neighbours[i] = (Byzantine_RMI) registry.lookup("Byzantine-"+ i);
            }
            // Byzantine_RMI neighbour = (Byzantine_RMI) registry.lookup("Byzantine-"+this.neighbour);


            for(int i = 0; i < this.num_nodes; i++){
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


        if (message.type.equals("elected") || this.tid == message.id) {
          System.out.println("Finished with elected: " + message.id);
          return;
        }

        if (this.passive) {
          System.out.println("[" + this.tid + "] " + "[" + message.type + "] Relaying: " + message.id);
             this.relay(message);
         }
        else {
            System.out.println("[" + this.tid + "] " + "[" + message.type + "] Message received: " + message.id);


            if (message.type.equals("ntid")) {
              this.ntid = message.id;
              this.receivedNtid = true;
              receiveNtid(message);
            }


            if (message.type.equals("nntid")) {
              this.nntid = message.id;
              this.receivedNntid = true;
              receiveNntid(message);
            }


            if (this.ntid >= this.tid && this.ntid >= this.nntid) {
                System.out.println(this.tid + " has been changed to " + this.ntid);
                this.tid = this.ntid;
                this.passive = false;
            }
            else {
                this.passive = true;
                System.out.println("I have been idled!");
            }
            this.receivedNntid = false;
            this.receivedNtid = false;
            send(new Message(this.tid, "ntid"));
        }

    }

    public void relay(Message message) {
        if (this.tid == message.id) {
            this.elected = true;
        }
        this.send(message);
    }

    public void receiveNtid(Message message) {
      while (!receivedNtid) {
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      if (this.tid == this.ntid) {
        this.elected = true;
        send(new Message(this.tid, "elected"));
      }
      send(new Message(Math.max(this.tid, this.ntid), "nntid"));

    }

  public void receiveNntid(Message message) {
    while (!receivedNntid) {
      try {
        Thread.sleep(1000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (this.tid == this.nntid) {
      this.elected = true;
      send(new Message(this.tid, "elected"));
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
        System.out.println("tid: " + this.tid + ", ntid: " + this.ntid + ", nntid: " + this.nntid);
    }



    //    public boolean getNextFromBuffer() {
//        System.out.println(this.buffer.stream().findAny().get());
//    }
}
