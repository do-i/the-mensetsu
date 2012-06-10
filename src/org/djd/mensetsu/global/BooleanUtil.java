package org.djd.mensetsu.global;

public final class BooleanUtil {

	public static final int TRUE = 1;
	public static final int FALSE = 0;
	

	public static final int SELECTED = TRUE;
	public static final int UNSELECTED = FALSE;

	public static final boolean get(int value) {
		switch (value) {
		case TRUE:
			return true;
		case FALSE:
			return false;
		default:
			throw new IllegalArgumentException("value must be either 0 or 1.");
		}
	}

	public static final int get(boolean value) {
		if (value) {
			return TRUE;
		} else {
			return FALSE;
		}

	}

}
