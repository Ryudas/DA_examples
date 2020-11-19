package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import nl.tudelft.Message;

public class Main {

    public static void main(String[] args) {
	// write your code here
//        System.out.print("LALA");



        // Initialize registry
        try {

            java.rmi.registry.LocateRegistry.createRegistry(1098);
            Registry registry = LocateRegistry.getRegistry(1098);

            // Create threads for execution
            Thread t_1 = new Thread(new BSS("1"));
            Thread t_2 = new Thread(new BSS("2"));
            Thread t_3 = new Thread(new BSS("3"));

            // Start all objects in parallel
            t_1.start();
            t_2.start();
            t_3.start();
            /*
            BSS obj1 = new BSS("1");
            BSS obj2 = new BSS("2");
            BSS obj3 = new BSS("3");
            BSS_RMI newobj = (BSS_RMI) registry.lookup("BSS-1");
            int[] clock = new int[3];
            Message m1 = new Message(clock, "3");
            obj3.broadcast(m1);
            obj2.broadcast(new Message(obj2.getClock(),"2"));


            //Print the data after the scenario
            obj1.printData();
            obj2.printData();
            obj3.printData();


             */
        } catch (RemoteException e ) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
