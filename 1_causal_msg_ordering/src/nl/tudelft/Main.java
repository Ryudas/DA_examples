package nl.tudelft;

import java.rmi.RemoteException;

public class Main {

    public static void main(String[] args) {
	// write your code here
        System.out.print("LALA");

        // Initialize registry
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
        } catch (RemoteException e ) {
            e.printStackTrace();
        }
    }
}
