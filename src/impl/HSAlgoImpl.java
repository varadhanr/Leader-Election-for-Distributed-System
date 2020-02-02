package impl;

import interfaces.HSAlgo;
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

    public ProcessThreadObject(int pId, int pUID) {
      this.processId = pId;
      this.processUID = pUID;
    }

    @Override
    public void run() {
      // HS Algorithm will be implemented here
      System.out
          .println("Process Id is :" + this.processId + " and its UID is :" + this.processUID);
    }
  }

  @Override
  public ProcessUID execute() {

    for (int i = 0; i < num_of_processes; i++) {
      ProcessThreadObject threadObj = new ProcessThreadObject(i, processUID[i].getProcessUID());
      threadObj.start();
    }

    // dummy return
    return new ProcessUID(10, 100);

  }

}
