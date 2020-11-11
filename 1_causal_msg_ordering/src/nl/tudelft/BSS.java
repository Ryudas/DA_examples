package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Birman-Schiper-Stephenson algorithm class
public class BSS extends UnicastRemoteObject implements  BSS_RMI , Runnable  {
    private String id;

    // constructor for a component of BSS
    protected BSS(String id) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // create a BSS component with a certain id and bind it to the remote
        this.id = id;

        java.rmi.Naming.bind("rmi://localhost/BSS_" + id.toString(), this);
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
    public void send_message(String text) throws RemoteException {
        System.out.print( "I, " + this.id + "Have received: "+ text );
    }
}
