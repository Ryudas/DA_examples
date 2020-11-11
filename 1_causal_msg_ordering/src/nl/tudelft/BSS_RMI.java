package nl.tudelft;

import java.rmi.Remote;

// Birman-Schiper-Stephenson
public interface BSS_RMI extends Remote {
    public void send_message(String text) throws java.rmi.RemoteException;
}
