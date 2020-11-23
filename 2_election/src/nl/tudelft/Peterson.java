package nl.tudelft;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;

// Birman-Schiper-Stephenson algorithm class
public class Peterson extends UnicastRemoteObject implements Peterson_RMI, Runnable  {
    private String id;
    private int[] clock;
    private ArrayList<Message> buffer;
    private int[] schedule;

    // constructor for a component of BSS
    protected Peterson(String id) throws RemoteException, AlreadyBoundException, MalformedURLException {
        // create a BSS component with a certain id and bind it to the remote
        this.id = id;
        this.buffer = new ArrayList<>();
        this.clock = new int[3];
        java.rmi.Naming.bind("rmi://localhost:1098/BSS-" + id.toString(), this);

    }

    //
    public void set_schedule(int[] msg_times){
        this.schedule = msg_times;

    }
    @Override
    public void run() {
        // dummy amounts now
        for( int i = 0 ; i < this.schedule.length; i++){
            // if clock is not current to the schedule get it to it,
            // create message from current elem to all others
            this.incrementClock();
            Message m1 = new Message(this.clock, this.id);
            broadcast(m1);
        }

    }

    public void broadcast(Message message) {
        Registry registry = null;
        try {
            // load all objects
            registry = LocateRegistry.getRegistry(1098);
            Peterson_RMI one = (Peterson_RMI) registry.lookup("BSS-1");
            Peterson_RMI two = (Peterson_RMI) registry.lookup("BSS-2");
            Peterson_RMI three = (Peterson_RMI) registry.lookup("BSS-3");

           // this.incrementClock();
           // message.clock[Integer.parseInt(message.sender)-1]++;
            // process random delay
            delay();
            one.receive(message);
            delay();
            two.receive(message);
            delay();
            three.receive(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }



    }


    /**
     * I think we shouldd change a few things in our algorithm, for example vplusej is up to date with the message
     * sender clock. Only increment if it is not him sending it to himself..
     * If you check the example of: https://www.google.com/search?q=birman+schiper+stephenson+protocol+example&sxsrf=ALeKk03YA7SOToH5c_mp6Z5slfeg3vUuKQ:1605647563305&tbm=isch&source=iu&ictx=1&fir=Wz9WhUmMB_5CyM%252C9OLhdgKzv0apYM%252C_&vet=1&usg=AI4_-kSHrVxQMSCeATrP0v9ROlXjDoCidw&sa=X&ved=2ahUKEwibpJSlv4rtAhVS1xoKHdT6B9gQ9QF6BAgIEC8#imgrc=X3ZFXsVUs_dtiM
     * I used that example in the main, and the values seem to be the same if I use compareLT.
     * We also just need to make sure we run them in threads and that our buffer works.
     * Our compare doesn't work like it should.
     * @param message
     * @throws RemoteException
     */
    @Override
    public void receive(Message message) throws RemoteException {
        System.out.println(this.id +" received the message w/ clock" + Arrays.toString(message.clock) + " from " + message.sender);
        int [] vplusej = this.clock;
        //if (!this.id.equals(message.sender)) {
            //Adding this and changing to compLT somehow works...
            //this.clock[Integer.parseInt(message.sender)-1] = message.clock[Integer.parseInt(message.sender)-1];

            vplusej[Integer.parseInt(message.sender)-1] +=1;
           // this.incrementClock();
        //}

        //System.out.println("vplusej: "+ Arrays.toString(vplusej) +". message clock: " + Arrays.toString(message.clock) +"" +
        //        ". vplusej is larger than message.clock: " + compGET(vplusej, message.clock));

        if (compGET(vplusej, message.clock)) {
            deliver(message);

            // once we delivered we might deliver more from the buffer
            this.buffer.forEach((Vm) -> {
                int [] vplus_ek = this.clock;
                vplus_ek[Integer.parseInt(Vm.sender)-1] += 1;
                if(compGET(vplus_ek, Vm.clock)) {
                    //deliver the message or deliver Vm?
                    deliver(Vm);
                }
            });
        }
        else {
            this.buffer.add(message);
            System.out.println("Message from "+ message.sender+  " with clock: " + Arrays.toString(message.clock) + " added to the buffer of: " +  this.id );
        }
    }

    /**
     * Optional change?: instead of incrementing by 1, set it to the clock value itself,
     * so it will work if it is two ahead.
     * @param message
     */
    public void deliver(Message message) {
//        this.incrementClock();
        //if (!this.id.equals(message.sender)){
            this.clock[Integer.parseInt(message.sender)-1] += 1;
            this.buffer.remove(message);
            System.out.println("Message delivered from " + message.sender + "in process:" + this.id  + Arrays.toString(message.clock));
            System.out.println();
            System.out.println(this.id + " now has the clock: " + Arrays.toString(this.clock));

       // }

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
        System.out.println("This is the buffer for BSS" + this.id + ": ");
        this.buffer.forEach( (arr) ->
                System.out.println(Arrays.toString(arr.clock)));
    }

    public void incrementClock() {
        this.clock[Integer.parseInt(this.id)-1]++;
    }

    public boolean compGET(int[] first, int[] second) {
        //System.out.println("Compare get:");
        //System.out.println(Arrays.toString(first));
        //System.out.println(Arrays.toString(second));
        //System.out.println();
        for (int i = 0; i < 3; i++) {
            //System.out.println("Enter the for loop");
            if (first[i] < second[i]) {
               // System.out.println("first is smaller than second for element " + i);
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

    public int[] getClock() {
        return this.clock;
    }

    public ArrayList<Message> getBuffer() {
        return buffer;
    }

    //    public boolean getNextFromBuffer() {
//        System.out.println(this.buffer.stream().findAny().get());
//    }
}
