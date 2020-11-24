package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    public static void main(String[] args) {

        // Initialize registry
        try {

            java.rmi.registry.LocateRegistry.createRegistry(1098);
            Registry registry = LocateRegistry.getRegistry(1098);

            Peterson obj1 = new Peterson("4", 7);
            Peterson obj2 = new Peterson("9", 4);
            Peterson obj3 = new Peterson("12", 9);
            Peterson obj4 = new Peterson("1", 12);
            Peterson obj5 = new Peterson("14", 1);
            Peterson obj6 = new Peterson("7", 14);
//            Peterson obj6 = new Peterson("8", 3);
//            Peterson obj7 = new Peterson("2", 8);
//            Peterson obj8 = new Peterson("6", 2);
//            Peterson obj9 = new Peterson("5", 6);
//            Peterson obj10 = new Peterson("7", 5);



            // Create threads for execution
            Thread t_1 = new Thread(obj1);
            Thread t_2 = new Thread(obj2);
            Thread t_3 = new Thread(obj3);
            Thread t_4 = new Thread(obj4);
            Thread t_5 = new Thread(obj5);
            Thread t_6 = new Thread(obj6);
//            Thread t_7 = new Thread(obj7);
//            Thread t_8 = new Thread(obj8);
//            Thread t_9 = new Thread(obj9);
//            Thread t_10 = new Thread(obj10);

            // Start all objects in parallel
            t_1.start();
            t_2.start();
            t_3.start();
            t_4.start();
            t_5.start();
            t_6.start();
//            t_7.start();
//            t_8.start();
//            t_9.start();
//            t_10.start();

            //Print the data after the scenario
//            obj1.printData();
//            obj2.printData();
//            obj3.printData();



        } catch (RemoteException e ) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();

        }
    }
}
