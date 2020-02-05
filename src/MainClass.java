import java.util.Scanner;

import impl.HSAlgoImpl;
import interfaces.HSAlgo;
import util.Message;
import util.ProcessUID;

/**
 * 
 * Java Main class which takes the input from user
 * 
 * @author varadhan, Madison
 *
 */
public class MainClass {

  public static void main(String[] str) {
    Scanner input_scanner = new Scanner(System.in);

    System.out.println("Enter Number of processes:");
    int input_num_of_processes = input_scanner.nextInt();
    ProcessUID[] processUIDObject = new ProcessUID[input_num_of_processes];
    System.out.println("Enter the processes UID:");

    for (int i = 0; i < input_num_of_processes; i++) {
      processUIDObject[i] = new ProcessUID(i, input_scanner.nextInt());
    }
    
    //set up right and left neighbors
    ProcessUID processZero = processUIDObject[0];
    processZero.setLeftNeighbor(processUIDObject[processUIDObject.length - 1]);
    processZero.setRightNeighbor(processUIDObject[1]);
    for (int i = 1; i < processUIDObject.length - 1; i++) {
    	ProcessUID thisProcess = processUIDObject[i];
    	thisProcess.setRightNeighbor(processUIDObject[i + 1]);
    	thisProcess.setLeftNeighbor(processUIDObject[i - 1]);
    }
    ProcessUID processLast = processUIDObject[processUIDObject.length - 1];
    processLast.setLeftNeighbor(processUIDObject[processUIDObject.length - 2]);
    processLast.setRightNeighbor(processZero);
    
    /** test that the links are set up correctly **/
  /*  for (int i = 0; i < processUIDObject.length; i++) {
    	ProcessUID thisProcess = processUIDObject[i];
    	System.out.println("this process: " + thisProcess.getProcessUID() + ", right neighbor: " + 
    			thisProcess.getRightNeighbor().getProcessUID() + ", left neighbor: " + 
    			thisProcess.getLeftNeighbor().getProcessUID());
    }
    //links are set up correctly.. pass a message from last process to process 0
    Message message = new Message(32, 1, "test message from process last");
    processLast.sendMessageToRightNeighbor(message);
    System.out.println("Message recieved: " + processZero.getLNeighborMessage().getDirection());
   */ 

    input_scanner.close();

    HSAlgo algo = new HSAlgoImpl(input_num_of_processes, processUIDObject);

    ProcessUID leaderUIDObj = algo.execute();


  }
}
