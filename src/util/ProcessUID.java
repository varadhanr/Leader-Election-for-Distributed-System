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
	
	public ProcessUID(int pNumber, int pUID) {
		this.processNumber = pNumber;
		this.processUID = pUID;
		
	}

	public int getProcessNumber() {
		return this.processNumber;
	}

	public int getProcessUID() {
		return this.processUID;
	}
	
}
