package review;

public enum EvaluationType {
	FOUR_MARKS,YES_NO,ZERO_TO_HUNDRED,NO_EVAL;
	
	public boolean isFourMarks() {
		return this.equals(FOUR_MARKS);
	}
	
	public boolean isYesNo() {
		return this.equals(YES_NO);
	}
	
	public boolean isZeroToHundred() {
		return this.equals(ZERO_TO_HUNDRED);
	}
}
