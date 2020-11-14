package nl.tudelft;

import java.rmi.Remote;

// Birman-Schiper-Stephenson
public interface BSS_RMI extends Remote {
    public void message(Message message) throws java.rmi.RemoteException;
}
