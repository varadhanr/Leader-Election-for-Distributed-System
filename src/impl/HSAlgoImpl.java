package impl;

import interfaces.HSAlgo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import util.Message;
import util.ProcessUID;

public class HSAlgoImpl implements HSAlgo {

  int num_of_processes;
  ProcessUID processUID[];

  private static CyclicBarrier barrier, barrier_message_sent;

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
	private Message messageFromLNeighbor; //the message received from this process's left neighbor
	private Message messageFromRNeighbor; //the message received from this process's right neighbor
	
	private Message messageToRNeighbor; //the message sent to this process's left neighbor
	private Message messageToLNeighbor; //the message sent to this process's right neighbor
	
	String status;	
	int phase;
	

    public ProcessThreadObject(int pId, int pUID) {
      this.processId = pId;
      this.processUID = pUID;
      
      //initially, all messages received from neighbors are null
      messageFromLNeighbor = null;
      messageFromRNeighbor = null;
      
      //set right and left neighbors to be null. call setLeft/RightNeighbor() to change this
      leftNeighbor = null;
      rightNeighbor = null;
      
      messageToRNeighbor = null;
      messageToLNeighbor = null;
      
      status = "UNKNOWN";
      phase = 0;
    }

    @Override
    public void run() {
      // HS Algorithm will be implemented here
    	
      messageToRNeighbor = new Message(this.processUID, 1, 1, "UNKNOWN");
      messageToLNeighbor = new Message(this.processUID, 1, 1, "UNKNOWN");
      
      int leader;
      
      while(true) { 
          this.sendMessageToLeftNeighbor(messageToLNeighbor);
    	  this.sendMessageToRightNeighbor(messageToRNeighbor);
    	  
    	  try {
	            // do not proceed, until all n threads have sent messages to it's neighbors
	          barrier_message_sent.await();
          } catch (InterruptedException ex) {
        	  return;
          } catch (BrokenBarrierException ex) {
        	  return;
          }
        
    	  //messages to be sent to the left and right neighbor in next round
    	  messageToRNeighbor = null;
          messageToLNeighbor = null;
	      
          //Broadcasting leader's UID to right neighbors
          if (messageFromLNeighbor != null  && (messageFromLNeighbor.getMessageType() == "ELECTED")) {
        	  if (this.status == "TERMINATE") //thread already terminated
        		  break;
        	  this.status = "TERMINATE";
        	  System.out
	          .println(processUID + " Received leader's  Id :" + messageFromLNeighbor.getId());
        	  messageToRNeighbor = messageFromLNeighbor;
          }
          
          //Broadcasting leader's UID to left neighbors
          if (messageFromRNeighbor != null  && (messageFromRNeighbor.getMessageType() == "ELECTED")) {
        	  if (this.status == "TERMINATE") //thread already terminated
        		  break;
        	  this.status = "TERMINATE";
        	  System.out
	          .println(processUID + " Received leader's  Id :" + messageFromRNeighbor.getId());
        	  messageToLNeighbor = messageFromRNeighbor;
          }
	      
	      if (messageFromLNeighbor != null && messageFromLNeighbor.getDirection() == 1) {
	    	  
	    	  Message leftMessage = messageFromLNeighbor;
	    	  if ((leftMessage.getId() > this.processUID) && leftMessage.getHops() > 1) {
	    		  this.status = "RELAY";
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is relay and process with id :" + leftMessage.getId() + " is leader");
	    		  Message lMessage = new Message(leftMessage.getId(), leftMessage.getHops()-1, leftMessage.getDirection(), "UNKNOWN");
	    		  messageToRNeighbor = (lMessage);
	    	  }
	    	  else if((leftMessage.getId() > this.processUID) && (leftMessage.getHops() == 1)) {
	    		  this.status = "RELAY";
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is relay and process with id :" + leftMessage.getId() + " is leader");
	    		  Message lMessage = new Message(leftMessage.getId(), leftMessage.getHops(), 0, "UNKNOWN");
	    		  messageToLNeighbor = (lMessage);
	    	  }
	    	  else if (leftMessage.getId() == this.processUID) {
	    		  leader = this.processUID;
	    		  status = "LEADER";
	    		  leftMessage.setMessageType("LEADER");
	    		  Message lMessage = new Message(leader, leftMessage.getHops(), -1, "ELECTED");
	    		  messageToLNeighbor = (lMessage);
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is leader");	          
	          }
	      }
	      
	      if (messageFromRNeighbor != null && messageFromRNeighbor.getDirection() == 1) {
	    	  
	    	  Message rightMessage = messageFromRNeighbor;
	    	  if ((rightMessage.getId() > this.processUID) && rightMessage.getHops() > 1) {
	    		  this.status = "RELAY";
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is relay and process with id :" + rightMessage.getId() + " is leader");
	    		  Message rMessage = new Message(rightMessage.getId(),rightMessage.getHops()-1,rightMessage.getDirection(), "UNKNOWN");
	    		  messageToLNeighbor = (rMessage);
	    	  }
	    	  else if((rightMessage.getId() > this.processUID) && (rightMessage.getHops() == 1)) {
	    		  this.status = "RELAY";
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is relay and process with id :" + rightMessage.getId() + " is leader");
	    		  Message rMessage = new Message(rightMessage.getId(),rightMessage.getHops(),0, "UNKNOWN");
	    		  messageToRNeighbor = (rMessage);
	    		  //System.out
		          //.println("Process with Id :" + this.processUID + " message :" + this.messageToRNeighbor);
	    	  }
	    	  else if (rightMessage.getId() == this.processUID) {
	    		  leader = this.processUID;
	    		  status = "LEADER";
	    		  rightMessage.setMessageType("LEADER");
	    		  Message rMessage = new Message(this.processUID,rightMessage.getHops(),-1, "ELECTED");
	    		  messageToRNeighbor = (rMessage);
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is leader");
	    	  }
	      }
	      
	      //Pass INWARD message from left to right neighbor
	      if ((messageFromLNeighbor != null) && (messageFromLNeighbor.getDirection() == 0) && messageFromLNeighbor.getId() > this.processUID) {
	    	  System.out
	          .println("Process with Id :" + this.processUID + " is relay and process with id :" + messageFromLNeighbor.getId() + " is leader");
	    	  messageToRNeighbor = (messageFromLNeighbor);
	      }
	      
          //Pass INWARD message from right to left neighbor
	      if ((messageFromRNeighbor != null) && (messageFromRNeighbor.getDirection() == 0) && (messageFromRNeighbor.getId() > this.processUID)) {
	    	  System.out
	          .println("Process with Id :" + this.processUID + " is relay and process with id :" + messageFromRNeighbor.getId() + " is leader");
	    	  messageToLNeighbor = (messageFromRNeighbor);
	      }
	      
	      if ((messageFromRNeighbor != null) && (messageFromLNeighbor != null) && (messageFromLNeighbor.getDirection() == 0) && (messageFromRNeighbor.getDirection() == 0)) {
	    	  phase++;
	    	  System.out
	          .println("Phase is :" + phase + " and UID is " + this.processUID);
	    	  messageFromLNeighbor.setHops((int) Math.pow(2, phase));
	    	  messageFromLNeighbor.setDirection(1);
	    	  messageToLNeighbor = (messageFromLNeighbor);
	    	  messageToRNeighbor = (messageFromLNeighbor);
	      }
	      /*
	      System.out
	          .println("Process Id is :" + this.processId + " and its UID is :" + this.processUID);
	      */
	      try {
	            // do not proceed, until all n threads have reached this position
	            barrier.await();
          } catch (InterruptedException ex) {
        	  return;
          } catch (BrokenBarrierException ex) {
        	  return;
          }
      }
      
      
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
  		return messageFromLNeighbor;
  	}
  	
  	public void setLNeighborMessage(Message message) {
  		messageFromLNeighbor = message;
  	}
  	
  	public void setRNeighborMessage(Message message) {
  		messageFromRNeighbor = message;
  	}
  	
  	public Message getRNeighborMessage(){
  		return messageFromRNeighbor;
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
    System.out.println("Message received: " + processZero.getLNeighborMessage().getDirection());
  */ 
    
    barrier_message_sent = new CyclicBarrier(num_of_processes);
    
    //start threads
    for (int i = 0; i < num_of_processes; i++)
        threads[i].start();
    
    barrier = new CyclicBarrier(num_of_processes,
	   new Runnable() {
		  int round = 1;
	      @Override
	      public void run() {
	    	  round++;
	    	  System.out.println("Executing " + round + " round ");
	      }
	    }
	 );
 
    // dummy return
    return new ProcessUID(10, 100);

  }

}
