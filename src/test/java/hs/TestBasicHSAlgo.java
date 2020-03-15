package hs;

import org.testng.Assert;
import org.testng.annotations.Test;
import hs.impl.HSAlgoImpl;
import hs.interfaces.HSAlgo;
import hs.util.ProcessUID;

public class TestBasicHSAlgo {
  @Test
  public void simpleHSAlgoTest() {
    int[] UID = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 120};
    int num_of_processes = UID.length;
    ProcessUID[] processUIDObject = new ProcessUID[num_of_processes];
    for (int i = 0; i < num_of_processes; i++) {
      processUIDObject[i] = new ProcessUID(i, UID[i]);
    }

    HSAlgo hsAlgo = new HSAlgoImpl(num_of_processes, processUIDObject);
    ProcessUID leader = hsAlgo.execute();

    Assert.assertEquals(120, leader.getProcessUID());

  }
}
