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
	private String direction; //in or out. Can change this to an int or something if you like
	
	public Message(int originatorsID, int startingHopsNum, String directionOfTravel) {
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
	
	public String getDirection(){
		return direction;
	}
	
	public void setDirection(String newDirection){
		direction = newDirection;
	}
}
