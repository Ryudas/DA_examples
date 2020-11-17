package nl.tudelft;

import java.io.Serializable;

/**
 * This is the message being sent between nodes.
 *
 */
public class Message implements Serializable {
  public int [] clock;
  public String sender;


  public Message(int[] clock, String sender) {
    this.clock = new int[3];
    this.clock = clock.clone();
    this.sender = sender;
  }

  public void print() {
    System.out.println("This is the vector clock:");
    for (int i = 0; i < this.clock.length; i++) {
      System.out.println(this.clock[i]);
    }
  }
}
