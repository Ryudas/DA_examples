package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Birman-Schiper-Stephenson algorithm class
public class BSS extends UnicastRemoteObject implements  BSS_RMI , Runnable  {
    private String id;

    // constructor for a component of BSS
    protected BSS(String id) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // create a BSS component with a certain id and bind it to the remote
        this.id = id;

        java.rmi.Naming.bind("rmi://localhost/BSS" + id.toString(), this);
    }

    @Override
    public void run() {

    }

    @Override
    public void send_message(String text) throws RemoteException {
        System.out.print(text);
    }
}
