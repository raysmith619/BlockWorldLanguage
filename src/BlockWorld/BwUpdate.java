package BlockWorld;
/**
 * Supports update by unconnected parties
 */
public class BwUpdate {

	/**
	 * Default for testing
	 */
	BwUpdate() {
		if (this.bExec == null) {
			BwTrace trace = new BwTrace();
			this.bExec = new BwExec(trace);
		}
	}
	
	BwUpdate(BwExec bw_cmd) {
		this.bExec = bw_cmd; 
	}
	
	/**
	 * Pass updated indication and value
	 * @param name
	 * @param value
	 */
	public void update(String name, double value) {
		this.bExec.update(name, value);
	}

	BwExec bExec;
}
