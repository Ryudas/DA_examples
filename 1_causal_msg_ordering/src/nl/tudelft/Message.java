package nl.tudelft;

import java.io.Serializable;

/**
 * This is the message being sent between nodes.
 *
 */
public class Message implements Serializable {
  public int [] clock;

  public Message(int[] clock) {
    this.clock = new int[3];
    this.clock = clock.clone();
  }

  public void print() {
    System.out.println("This is the vector clock:");
    for (int i = 0; i < this.clock.length; i++) {
      System.out.println(this.clock[i]);
    }
  }
}
