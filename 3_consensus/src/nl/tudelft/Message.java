package nl.tudelft;

import java.io.Serializable;

/**
 * This is the message being sent between nodes.
 *
 */
public class Message implements Serializable {

  public String type;
  public int round;
  public int v;


  public Message(String type, int round, int v) {
    this.type = type;
    this.round = round;
    this.v =  v;
  }

}
