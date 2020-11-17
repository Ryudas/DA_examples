package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// Birman-Schiper-Stephenson algorithm class
public class BSS extends UnicastRemoteObject implements  BSS_RMI , Runnable  {
    private String id;
    private int[] clock;
    private ArrayList<Message> buffer;

    // constructor for a component of BSS
    protected BSS(String id) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // create a BSS component with a certain id and bind it to the remote
        this.id = id;
        this.buffer = new ArrayList<>();
        this.clock = new int[3];
        java.rmi.Naming.bind("rmi://localhost:1098/BSS-" + id.toString(), this);
    }

    @Override
    public void run() {

    }

    public void broadcast(Message message) {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(1098);
            BSS_RMI one = (BSS_RMI) registry.lookup("BSS-1");
            BSS_RMI two = (BSS_RMI) registry.lookup("BSS-2");
            BSS_RMI three = (BSS_RMI) registry.lookup("BSS-3");
            this.incrementClock();
            one.receive(message);
            two.receive(message);
            three.receive(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void receive(Message message) throws RemoteException {
        delay();
        int [] vplusej = this.clock;
        vplusej[Integer.parseInt(message.sender)-1] += 1;

        if (compGET(vplusej, message.clock)) {
            deliver(message);
            this.buffer.forEach((Vm) -> {
                int [] vplus_ek = this.clock;
                vplus_ek[Integer.parseInt(Vm.sender)-1] += 1;
                if(compGET(vplus_ek, Vm.clock)) {
                    deliver(message);
                }
            }
            );
        }
        else {
            this.buffer.add(message);
        }
    }

    public void deliver(Message message) {
//        this.incrementClock();
        this.clock[Integer.parseInt(message.sender)-1] += 1;
        this.buffer.remove(message);
    }

    public void delay() {
        int delay = (int) (Math.random()*10);
//        System.out.println(delay);
        try {
            Thread.sleep(delay*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printData() {
        System.out.println("This is the vector clock for BSS" + this.id + ":");
        System.out.print("[ ");
        for (int i = 0; i < this.clock.length; i++) {
            System.out.print(this.clock[i] + " ");
        }
        System.out.print("]");
        System.out.println();
    }

    public void incrementClock() {
        this.clock[Integer.parseInt(this.id)-1]++;
    }

    public boolean compGET(int[] first, int[] second) {
        for (int i = 0; i < 3; i++) {
            if (first[i] < second[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean compLT(int[] first, int[] second) {
        for (int i = 0; i < 3; i++) {
            if (first[i] >= second[i]) {
                return false;
            }
        }
        return true;
    }

//    public boolean getNextFromBuffer() {
//        System.out.println(this.buffer.stream().findAny().get());
//    }
}
