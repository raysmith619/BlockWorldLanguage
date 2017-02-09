package BlockWorld;

public class BwRange {
	public enum BwRangeType {
		FIRST,
		LAST,
		ALL,
		LIST,	// n to m
		SINGLE,
	}
	
	public BwRange(BwSymTable sT) {
		this.sT = sT;
	}
	
	/**
	 * Basic range type
	 * @param type
	 */
	public BwRange(BwSymTable sT, BwRangeType type) {
		this(sT);
		set(type, mkValue(), mkValue());
	}

	/**
	 * Range with limits
	 */
	public BwRange(BwSymTable sT, BwRangeType type, BwValue begin, BwValue end) {
		this(sT);
		set(type, begin, end);
	}
	
	/**
	 * Initialization tool
	 */
	public void set(BwRangeType type, BwValue begin, BwValue end) {
		this.type = type;
		this.begin = begin;
		this.end = end;
	}
	
	
	public String toString() {
		String str = "";
		switch (this.type) {
			case SINGLE:
				str += this.begin;
				break;
			case LIST:
				str += this.begin + "-" + this.end;
				break;
			case FIRST:
				str += "FIRST";
				break;
			case LAST:
				str += "LAST";
				break;
			case ALL:
				str += "ALL";
				break;
			default:
				str += "UNKNOWN";
		}
		return str;
	}

	public BwValue mkValue() {
		return new BwValue(this.sT);
	}

	
	private BwSymTable sT;		// Access to Symbol Table
	public BwRangeType type;
	public BwValue begin;
	public BwValue end;
}
