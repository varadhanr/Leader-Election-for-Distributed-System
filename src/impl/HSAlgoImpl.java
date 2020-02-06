package impl;

import interfaces.HSAlgo;
import util.Message;
import util.ProcessUID;

public class HSAlgoImpl implements HSAlgo {

  int num_of_processes;
  ProcessUID processUID[];

  public HSAlgoImpl(int num_of_processes, ProcessUID[] processUID) {
    this.num_of_processes = num_of_processes;
    this.processUID = processUID;
  }

  class ProcessThreadObject extends Thread {

    // Attributes of a Process

    int processId;
    int processUID;
	private ProcessThreadObject leftNeighbor;
	private ProcessThreadObject rightNeighbor;
	private Message messageLNeighbor; //the message received from this process's left neighbor
	private Message messageRNeighbor; //the message received from this process's right neighbor
	
	String status = "UNKNOWN";
	

    public ProcessThreadObject(int pId, int pUID) {
      this.processId = pId;
      this.processUID = pUID;
      //initially, all messages received from neighbors are null
      messageLNeighbor = null;
      messageRNeighbor = null;
      //set right and left neighbors to be null. call setLeft/RightNeighbor() to change this
      leftNeighbor = null;
      rightNeighbor = null;
    }

    @Override
    public void run() {
      // HS Algorithm will be implemented here
    
      Message message = new Message(this.processUID, 1, 1);
      this.sendMessageToRightNeighbor(message);
      this.sendMessageToLeftNeighbor(message);
      
      if (this.messageLNeighbor.getDirection() == 1) {
    	  
    	  Message leftMessage = this.messageLNeighbor;
    	  if ((leftMessage.getId() > this.processUID) && leftMessage.getHops() > 1) {
    		  leftMessage.setHops(leftMessage.getHops()-1);
    		  this.sendMessageToRightNeighbor(leftMessage);
    	  }
    	  else if((leftMessage.getId() > this.processUID) && (leftMessage.getHops() == 1)) {
    		  leftMessage.setDirection(0);
    		  this.sendMessageToLeftNeighbor(message);
    	  }
    	  else if (leftMessage.getId() > this.processUID) {
    		  status = "LEADER";
    	  }
      }
      
      if (this.messageRNeighbor.getDirection() == 1) {
    	  
    	  Message rightMessage = this.messageRNeighbor;
    	  if ((rightMessage.getId() > this.processUID) && rightMessage.getHops() > 1) {
    		  rightMessage.setHops(rightMessage.getHops()-1);
    		  this.sendMessageToLeftNeighbor(rightMessage);
    	  }
    	  else if((rightMessage.getId() > this.processUID) && (rightMessage.getHops() == 1)) {
    		  rightMessage.setDirection(0);
    		  this.sendMessageToRightNeighbor(rightMessage);
    	  }
    	  else if (rightMessage.getId() > this.processUID) {
    		  status = "LEADER";
    	  }
      }
      
      if ((this.messageLNeighbor.getDirection() == 0) && this.messageLNeighbor.getId() > this.processUID) {
    	  this.sendMessageToRightNeighbor(message);
      }
      
      if ((this.messageRNeighbor.getDirection() == 0) && (this.messageRNeighbor.getId() > this.processUID)) {
    	  this.sendMessageToLeftNeighbor(message);
      }
      
      if ((this.messageLNeighbor.getDirection() == 0) && (this.messageRNeighbor.getDirection() == 0)) {
    	  
    	  this.sendMessageToLeftNeighbor(message);
      }
    
      System.out
          .println("Process Id is :" + this.processId + " and its UID is :" + this.processUID);
    }
    
    /** setters & getters **/
    
    public void setLeftNeighbor(ProcessThreadObject leftNeighbor) {
  		this.leftNeighbor = leftNeighbor;
  	}
  	
  	public ProcessThreadObject getLeftNeighbor(){
  		return leftNeighbor;
  	}
  	
  	public void setRightNeighbor(ProcessThreadObject rightNeighbor) {
  		this.rightNeighbor = rightNeighbor;
  	}
  	
  	public ProcessThreadObject getRightNeighbor(){
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
  	
  	/** other methods **/
  	public void sendMessageToLeftNeighbor(Message message) {
  		//setRNeighborMessage bc relative to the left neighbor, this process is to its right
  		leftNeighbor.setRNeighborMessage(message); 
  	}
  	
  	public void sendMessageToRightNeighbor(Message message) {
  		//setRNeighborMessage bc relative to the left neighbor, this process is to its right
  		rightNeighbor.setLNeighborMessage(message);
  	}
  }

  @Override
  public ProcessUID execute() {

	ProcessThreadObject[] threads = new ProcessThreadObject[num_of_processes];
	//make n threads
	for (int i = 0; i < num_of_processes; i++) {
	      ProcessThreadObject threadObj = new ProcessThreadObject(i, processUID[i].getProcessUID());
	      threads[i] = threadObj;
	}
	
	//set up right and left neighbors
    ProcessThreadObject processZero = threads[0];
    processZero.setLeftNeighbor(threads[num_of_processes - 1]);
    processZero.setRightNeighbor(threads[1]);
    for (int i = 1; i < num_of_processes - 1; i++) {
    	ProcessThreadObject thisProcess = threads[i];
    	thisProcess.setRightNeighbor(threads[i + 1]);
    	thisProcess.setLeftNeighbor(threads[i - 1]);
    }
    ProcessThreadObject processLast = threads[num_of_processes - 1];
    processLast.setLeftNeighbor(threads[num_of_processes - 2]);
    processLast.setRightNeighbor(processZero);
    
    /** test that the links are set up correctly **/
  /*  for (int i = 0; i < threads.length; i++) {
    	ProcessThreadObject thisProcess = threads[i];
    	System.out.println("this process: " + thisProcess.processUID + ", right neighbor: " + 
    			thisProcess.getRightNeighbor().processUID + ", left neighbor: " + 
    			thisProcess.getLeftNeighbor().processUID);
    }
    //links are set up correctly.. pass a message from last process to process 0
    Message message = new Message(32, 1, "test message from process last");
    processLast.sendMessageToRightNeighbor(message);
    System.out.println("Message recieved: " + processZero.getLNeighborMessage().getDirection());
  */ 
    
    //start threads
    for (int i = 0; i < num_of_processes; i++)
        threads[i].start();

    // dummy return
    return new ProcessUID(10, 100);

  }

}
