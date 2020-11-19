package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Birman-Schiper-Stephenson algorithm class
public class BSS extends UnicastRemoteObject implements  BSS_RMI , Runnable  {
    private String id;
    private int[] clock;

    // constructor for a component of BSS
    protected BSS(String id) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // create a BSS component with a certain id and bind it to the remote
        this.id = id;


        this.clock = new int[3];
        java.rmi.Naming.bind("rmi://localhost:1098/BSS-" + id.toString(), this);

    }

    @Override
    public void run() {
        // what does each node do?
        // send message to node X
        try {

            BSS node_1 = (BSS) java.rmi.Naming.lookup("rmi://localhost/BSS_" + "1");
            BSS node_2 = (BSS) java.rmi.Naming.lookup("rmi://localhost/BSS_" + "2");


            if(this.id == "1"){
                node_2.send_message("message from" + this.id);
            }else{
                node_1.send_message("message from" + this.id);
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void message(Message message) throws RemoteException {
        int delay = (int) (Math.random()*10);
        System.out.println(delay);

        try {
            Thread.sleep(delay*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Lala");
    }

    public void printData() {
        System.out.println("This is the vector clock:");
        for (int i = 0; i < this.clock.length; i++) {
            System.out.println(this.clock[i]);
        }

    }
}
