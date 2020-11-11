package nl.tudelft;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Birman-Schiper-Stephenson algorithm class
public class BSS extends UnicastRemoteObject implements  BSS_RMI , Runnable  {
    protected BSS() throws RemoteException {
    }

    @Override
    public void run() {

    }

    @Override
    public void send_message() throws RemoteException {

    }
}
