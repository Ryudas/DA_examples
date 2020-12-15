package nl.tudelft;

import java.rmi.Remote;

// Birman-Schiper-Stephenson
public interface Byzantine_RMI extends Remote {
    public void receive(Message message) throws java.rmi.RemoteException;
}
