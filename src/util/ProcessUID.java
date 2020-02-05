package util;

/**
 * 
 * Encapsulated Object for Process id and its UID
 * 
 * @author varadhan, Madison
 *
 */

public class ProcessUID {
	int processNumber;
	int processUID;
	private ProcessUID leftNeighbor;
	private ProcessUID rightNeighbor;
	private Message messageLNeighbor; //the message received from this process's left neighbor
	private Message messageRNeighbor; //the message received from this process's right neighbor

	public ProcessUID(int pNumber, int pUID) {
		this.processNumber = pNumber;
		this.processUID = pUID;
		//initially, all messages received from neighbors are null
		messageLNeighbor = null;
		messageRNeighbor = null;
		//set right and left neighbors to be null. call setLeft/RightNeighbor() to change this
		leftNeighbor = null;
		rightNeighbor = null;
	}

	public int getProcessNumber() {
		return this.processNumber;
	}

	public int getProcessUID() {
		return this.processUID;
	}
	
	public void setLeftNeighbor(ProcessUID leftNeighbor) {
		this.leftNeighbor = leftNeighbor;
	}
	
	public ProcessUID getLeftNeighbor(){
		return leftNeighbor;
	}
	
	public void setRightNeighbor(ProcessUID rightNeighbor) {
		this.rightNeighbor = rightNeighbor;
	}
	
	public ProcessUID getRightNeighbor(){
		return rightNeighbor;
	}
	
	public Message getLNeighborMessage(){ 
		return messageLNeighbor;
	}
	
	public void setLNeighborMessage(Message message) {
		messageLNeighbor = message;
	}
	
	public void setRNeighborMessage(Message message) {
		messageRNeighbor = message;
	}
	
	public Message getRNeighborMessage(){
		return messageRNeighbor;
	}
	
	public void sendMessageToLeftNeighbor(Message message) {
		//setRNeighborMessage bc relative to the left neighbor, this process is to its right
		leftNeighbor.setRNeighborMessage(message); 
	}
	
	public void sendMessageToRightNeighbor(Message message) {
		//setRNeighborMessage bc relative to the left neighbor, this process is to its right
		rightNeighbor.setLNeighborMessage(message);
	}
	
}
