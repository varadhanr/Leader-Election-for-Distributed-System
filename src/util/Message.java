/**Message.java contains the basic information held by a message
 * 
 */
package util;

/**
 * @author Madison Pickering
 *
 */
public class Message {
	private int id;
	private int hops;
	private int direction; //an int indicating direction, 1 if going out, 0 if going in
	
	public Message(int originatorsID, int startingHopsNum, int directionOfTravel) {
		id = originatorsID;
		hops = startingHopsNum;
		direction = directionOfTravel;
	}
	
	public int getId() {
		return id;
	}
	
	public int getHops(){
		return hops;
	}
	
	public void setHops(int newNumHops){
		hops = newNumHops;
	}
	
	public int getDirection(){
		return direction;
	}
	
	public void setDirection(int newDirection){
		direction = newDirection;
	}
}
