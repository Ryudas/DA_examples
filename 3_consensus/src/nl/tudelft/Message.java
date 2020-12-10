package nl.tudelft;

import java.io.Serializable;

/**
 * This is the message being sent between nodes.
 *
 */
public class Message implements Serializable {
  public int id;
  public String type;


  public Message(int id, String type) {
    this.id = id;
    this.type = type;
  }

}
