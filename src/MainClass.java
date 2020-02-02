import java.util.Scanner;

import impl.HSAlgoImpl;
import interfaces.HSAlgo;
import util.ProcessUID;

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

    input_scanner.close();

    HSAlgo algo = new HSAlgoImpl(input_num_of_processes, processUIDObject);

    ProcessUID leaderUIDObj = algo.execute();


  }
}
