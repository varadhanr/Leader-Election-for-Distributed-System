package hs.impl;

import hs.interfaces.HSAlgo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import hs.util.ProcessUID;
import hs.util.Message;
import hs.util.MessageType;
import hs.util.Status;

public class HSAlgoImpl implements HSAlgo {

  int num_of_processes;
  ProcessUID processUID[];

  private static CyclicBarrier barrier, barrier_message_sent;
  private static CountDownLatch latch;
  
  private static ProcessUID leader;

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
	
	int leaderUID;
	Status status;	
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
      
      leaderUID = pUID;
      status = Status.UNKNOWN;
      phase = 0;
    }

    @Override
    public void run() {
      // HS Algorithm will be implemented here
    	
      messageToRNeighbor = new Message(this.processUID, 1, 1, MessageType.UNKNOWN);
      messageToLNeighbor = new Message(this.processUID, 1, 1, MessageType.UNKNOWN);
      
      while(true) { 
    	  if (messageToLNeighbor != null)
    		  this.sendMessageToLeftNeighbor(messageToLNeighbor);
    	  if (messageToRNeighbor != null)
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
          if (messageFromLNeighbor != null  && (messageFromLNeighbor.getMessageType() == MessageType.ELECTED)) {
        	  if (this.status == Status.TERMINATE) //thread already terminated
        		  break;
        	  
        	  this.leaderUID = messageFromLNeighbor.getId();
        	  this.status = Status.TERMINATE;
              latch.countDown(); 
              
        	  System.out.println(processUID + " received leader's  UID :" + this.leaderUID);
        	  messageToRNeighbor = messageFromLNeighbor;
          }
          
          //Broadcasting leader's UID to left neighbors
          if (messageFromRNeighbor != null  && (messageFromRNeighbor.getMessageType() == MessageType.ELECTED)) {
        	  if (this.status == Status.TERMINATE) //thread already terminated
        		  break;
        	  
        	  this.leaderUID = messageFromRNeighbor.getId();
        	  this.status = Status.TERMINATE;
        	  latch.countDown(); 
        	  
        	  System.out.println(processUID + " received leader's  UID :" + this.leaderUID);
        	  messageToLNeighbor = messageFromRNeighbor;
          }
	      
	      if (messageFromLNeighbor != null && messageFromLNeighbor.getDirection() == 1) {
	    	  
	    	  Message leftMessage = messageFromLNeighbor;
	    	  if ((leftMessage.getId() > this.processUID) && leftMessage.getHops() > 1) {
	    		  this.status = Status.RELAY;

	    		  Message lMessage = new Message(leftMessage.getId(), leftMessage.getHops()-1, leftMessage.getDirection(), MessageType.UNKNOWN);
	    		  messageToRNeighbor = (lMessage);
	    	  }
	    	  else if((leftMessage.getId() > this.processUID) && (leftMessage.getHops() == 1)) {
	    		  this.status = Status.RELAY;

	    		  Message lMessage = new Message(leftMessage.getId(), leftMessage.getHops(), 0, MessageType.UNKNOWN);
	    		  messageToLNeighbor = (lMessage);
	    	  }
	    	  else if (leftMessage.getId() == this.processUID) {
	    		  leader = new ProcessUID(this.processId, this.processUID);
	    		  status = Status.LEADER;
	    		  latch.countDown();
	    		  
	    		  leftMessage.setMessageType(MessageType.LEADER);
	    		  Message lMessage = new Message(this.processUID, leftMessage.getHops(), -1, MessageType.ELECTED);
	    		  messageToLNeighbor = (lMessage);
	    		  System.out.println("Leader found");	          
	          }
	      }
	      
	      if (messageFromRNeighbor != null && messageFromRNeighbor.getDirection() == 1) {
	    	  
	    	  Message rightMessage = messageFromRNeighbor;
	    	  if ((rightMessage.getId() > this.processUID) && rightMessage.getHops() > 1) {
	    		  this.status = Status.RELAY;
	    		  
	    		  System.out.println("Process with Id :" + this.processUID + " is relay and process with id :" + rightMessage.getId() + " is leader");
	    		  Message rMessage = new Message(rightMessage.getId(),rightMessage.getHops()-1,rightMessage.getDirection(), MessageType.UNKNOWN);
	    		  messageToLNeighbor = (rMessage);
	    	  }
	    	  else if((rightMessage.getId() > this.processUID) && (rightMessage.getHops() == 1)) {
	    		  this.status = Status.RELAY;
	    		  
	    		  System.out.println("Process with Id :" + this.processUID + " is relay and process with id :" + rightMessage.getId() + " is leader");
	    		  Message rMessage = new Message(rightMessage.getId(),rightMessage.getHops(),0, MessageType.UNKNOWN);
	    		  messageToRNeighbor = (rMessage);
	    	  }
	    	  else if (rightMessage.getId() == this.processUID) {
	    		  leader = new ProcessUID(this.processId, this.processUID);
	    		  this.status = Status.LEADER;
	    		  
	    		  rightMessage.setMessageType(MessageType.LEADER);
	    		  Message rMessage = new Message(this.processUID,rightMessage.getHops(),-1, MessageType.ELECTED);
	    		  messageToRNeighbor = (rMessage);
	    	  }
	      }
	      
	      //Pass INWARD message from left to right neighbor
	      if ((messageFromLNeighbor != null) && (messageFromLNeighbor.getDirection() == 0) && messageFromLNeighbor.getId() > this.processUID) {
	    	  messageToRNeighbor = (messageFromLNeighbor);
	      }
	      
          //Pass INWARD message from right to left neighbor
	      if ((messageFromRNeighbor != null) && (messageFromRNeighbor.getDirection() == 0) && (messageFromRNeighbor.getId() > this.processUID)) {
	    	  messageToLNeighbor = (messageFromRNeighbor);
	      }
	      
	      if ((messageFromRNeighbor != null) && (messageFromLNeighbor != null) && (messageFromLNeighbor.getDirection() == 0) && (messageFromRNeighbor.getDirection() == 0)) {
	    	  phase++;
	    	  System.out.println("Phase is :" + phase + " and UID is " + this.processUID);
	    	  messageFromLNeighbor.setHops((int) Math.pow(2, phase));
	    	  messageFromLNeighbor.setDirection(1);
	    	  
	    	  messageToLNeighbor = (messageFromLNeighbor);
	    	  messageToRNeighbor = (messageFromLNeighbor);
	      }
	      
          messageFromLNeighbor = null;
          messageFromRNeighbor = null;

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
	
    for (int i = 0; i < num_of_processes; i++) {
    	ProcessThreadObject thisProcess = threads[i];
    	
    	thisProcess.setRightNeighbor((i == num_of_processes -1)? threads[0] : threads[i + 1]);
    	thisProcess.setLeftNeighbor((i == 0)? threads[num_of_processes -1] : threads[i - 1]);
    }
    
    barrier_message_sent = new CyclicBarrier(num_of_processes);
    
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
    
    // The main thread waits for all the threads to terminate
    latch = new CountDownLatch(num_of_processes); 
    
    //start threads
    for (int i = 0; i < num_of_processes; i++)
        threads[i].start();
    
    try {
		latch.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}

    return leader;
  }

}
