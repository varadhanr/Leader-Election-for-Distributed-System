package impl;

import interfaces.HSAlgo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import util.Message;
import util.ProcessUID;

public class HSAlgoImpl implements HSAlgo {

  int num_of_processes;
  ProcessUID processUID[];
  
  int num_of_unknown_processes;

  static boolean done = false;
  private static CyclicBarrier barrier;

  public HSAlgoImpl(int num_of_processes, ProcessUID[] processUID) {
    this.num_of_processes = num_of_processes;
    this.processUID = processUID;
    this.num_of_unknown_processes = num_of_processes;
  }

  class ProcessThreadObject extends Thread {
	  
    // Attributes of a Process

    int processId;
    int processUID;
	private ProcessThreadObject leftNeighbor;
	private ProcessThreadObject rightNeighbor;
	private Message messageLNeighbor; //the message received from this process's left neighbor
	private Message messageRNeighbor; //the message received from this process's right neighbor
	
	private Message messageToRNeighbor;
	private Message messageToLNeighbor;
	
	String status;
	int phase;
	

    public ProcessThreadObject(int pId, int pUID) {
      this.processId = pId;
      this.processUID = pUID;
      
      //initially, all messages received from neighbors are null
      this.messageLNeighbor = null;
      this.messageRNeighbor = null;
      
      //set right and left neighbors to be null. call setLeft/RightNeighbor() to change this
      this.leftNeighbor = null;
      this.rightNeighbor = null;
      
      this.messageToRNeighbor = null;
      this.messageToLNeighbor = null;
      
      status = "UNKNOWN";
      phase = 0;
    }

    @Override
    public void run() {
      // HS Algorithm will be implemented here
    	
      this.messageToRNeighbor = new Message(this.processUID, 1, 1);
      this.messageToLNeighbor = new Message(this.processUID, 1, 1);
      
      while(!done) { 
    	  if (this.messageToLNeighbor.getDirection() != -1)
    		  this.sendMessageToLeftNeighbor(this.messageToLNeighbor);
    	  if (this.messageToRNeighbor.getDirection() != -1)
    		  this.sendMessageToRightNeighbor(this.messageToRNeighbor);
    	  
	      try
	      { 
	          // thread to sleep for 1000 milliseconds 
	          Thread.sleep(1000); 
	      } 
	
	      catch (Exception e) 
	      { 
	          System.out.println(e); 
	      } 
	      
	      System.out
          .println("Process with Id :" + this.processUID + " is " + this.messageToLNeighbor.getDirection());
	      System.out
          .println("Process with Id :" + this.processUID + " is " + this.messageToRNeighbor.getDirection());
	      
	      
	      if (this.messageLNeighbor.getDirection() == 1) {
	    	  
	    	  Message leftMessage = this.messageLNeighbor;
	    	  if ((leftMessage.getId() > this.processUID) && leftMessage.getHops() > 1) {
	    		  this.status = "RELAY";
	    		  leftMessage.setHops(leftMessage.getHops()-1); 
	    		  this.messageToRNeighbor = (leftMessage);
	    	  }
	    	  else if((leftMessage.getId() > this.processUID) && (leftMessage.getHops() == 1)) {
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is relay and process with id :" + leftMessage.getId() + " is leader");
	    		  this.status = "RELAY";
	    		  leftMessage.setDirection(0);
	    		  this.messageToLNeighbor = (leftMessage);
	    		  System.out
		          .println("Process with Id :" + this.processUID + " message :" + this.messageToLNeighbor.getId() + this.messageToLNeighbor.getDirection());

	    	  }
	    	  else if (leftMessage.getId() == this.processUID) {
	    		  status = "LEADER";
	    		  done = true;
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is leader");
	    		  break;
	    	  }
	      }
	      
	      if (this.messageRNeighbor.getDirection() == 1) {
	    	  
	    	  Message rightMessage = this.messageRNeighbor;
	    	  if ((rightMessage.getId() > this.processUID) && rightMessage.getHops() > 1) {
	    		  this.status = "RELAY";
	    		  rightMessage.setHops(rightMessage.getHops()-1);
	    		  this.messageToLNeighbor = (rightMessage);
	    	  }
	    	  else if((rightMessage.getId() > this.processUID) && (rightMessage.getHops() == 1)) {
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is relay and process with id :" + rightMessage.getId() + " is leader");
	    		  this.status = "RELAY";
	    		  rightMessage.setDirection(0);
	    		  this.messageToRNeighbor = (rightMessage);
	    		  System.out
		          .println("Process with Id :" + this.processUID + " message :" + this.messageToRNeighbor);
	    	  }
	    	  else if (rightMessage.getId() == this.processUID) {
	    		  status = "LEADER";
	    		  done = true;
	    		  System.out
		          .println("Process with Id :" + this.processUID + " is leader");
	    		  break;
	    	  }
	      }
	      
	      if ((this.messageLNeighbor.getDirection() == 0) && this.messageLNeighbor.getId() > this.processUID) {
	    	  this.messageToRNeighbor = (this.messageLNeighbor);
	      }
	      
	      if ((this.messageRNeighbor.getDirection() == 0) && (this.messageRNeighbor.getId() > this.processUID)) {
	    	  this.messageToLNeighbor = (this.messageLNeighbor);
	      }
	      
	      if ((this.messageLNeighbor.getDirection() == 0) && (this.messageRNeighbor.getDirection() == 0)) {
	    	  System.out
	          .println("Phase is :" + this.phase + " and UID is " + this.processUID);
	    	  this.phase++;
	    	  
	    	  this.messageLNeighbor.setHops((int) Math.pow(2, phase));
	    	  this.messageLNeighbor.setDirection(1);
	    	  this.messageToLNeighbor = (this.messageLNeighbor);
	    	  this.messageToRNeighbor = (this.messageLNeighbor);
	      }
	    
	      System.out
	          .println("Process Id is :" + this.processId + " and its UID is :" + this.processUID);
	      
	      try {
	            // do not proceed, until all [count] threads
	            // have reached this position
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
    System.out.println("Message received: " + processZero.getLNeighborMessage().getDirection());
  */ 
    
    //start threads
    for (int i = 0; i < num_of_processes; i++)
        threads[i].start();
    
    barrier = new CyclicBarrier(num_of_processes,
	   new Runnable() {
	      @Override
	      public void run() {
	    	  System.out.println("Executing next round ");
	      }
	    }
	 );

    // dummy return
    return new ProcessUID(10, 100);

  }

}
