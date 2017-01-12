package BlockWorld;

public class BwRange {
	public enum BwRangeType {
		FIRST,
		LAST,
		ALL,
		LIST,	// n to m
		SINGLE,
	}
	/**
	 * Basic range type
	 * @param type
	 */
	public BwRange(BwRangeType type) {
		this(type, new BwValue(), new BwValue());
	}

	/**
	 * Range with limits
	 */
	public BwRange(BwRangeType type, BwValue begin, BwValue end) {
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
	
	public BwRangeType type;
	public BwValue begin;
	public BwValue end;
}
