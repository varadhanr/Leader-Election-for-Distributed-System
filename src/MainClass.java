import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import impl.HSAlgoImpl;
import interfaces.HSAlgo;
import util.ProcessUID;

/**
 * 
 * Java Main class which takes the input from user
 * 
 * @author varadhan, Madison, Prameela
 *
 */
public class MainClass {

  public static void main(String[] str) {

    int input_num_of_processes;
    ProcessUID[] processUIDObject;

    if (str.length == 1) {
      // get input from a file
      Scanner fileScanner;

      try {
        fileScanner = new Scanner(new File(str[0]));
      } catch (FileNotFoundException e) {
        System.out.println("Error while opening the input file : " + str[0]);
        return;
      }

      input_num_of_processes = fileScanner.nextInt();
      processUIDObject = new ProcessUID[input_num_of_processes];

      for (int i = 0; i < input_num_of_processes; i++) {
        processUIDObject[i] = new ProcessUID(i,fileScanner.nextInt());
      }
      fileScanner.close();

    } else {
      Scanner input_scanner = new Scanner(System.in);

      System.out.println("Enter Number of processes:");
      input_num_of_processes = input_scanner.nextInt();
      processUIDObject = new ProcessUID[input_num_of_processes];
      System.out.println("Enter the processes UID:");

      for (int i = 0; i < input_num_of_processes; i++) {
        processUIDObject[i] = new ProcessUID(i, input_scanner.nextInt());
      }

      input_scanner.close();
    }

    HSAlgo algo = new HSAlgoImpl(input_num_of_processes, processUIDObject);

    ProcessUID leaderUIDObj = algo.execute();

    System.out.println("Process with id: " + leaderUIDObj.getProcessNumber() + " and UI: "
        + leaderUIDObj.getProcessUID() + " is leader");

    System.exit(0);
  }
}
