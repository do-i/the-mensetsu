package org.djd.mensetsu.global;


public final class SwipeComputer {
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public enum Movement {
		LEFT_TO_RIGHT, RIGHT_TO_LEFT, NONE
	};

	public static Movement getMovement(float startX, float startY, float endX, float endY, float velocityX) {
		Movement result = Movement.NONE;
		if (Math.abs(startY - endY) > SWIPE_MAX_OFF_PATH) {
			return Movement.NONE;
		}
		if (startX - endX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			result = Movement.RIGHT_TO_LEFT;
		} else if (endX - startX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

			result = Movement.LEFT_TO_RIGHT;
		}

		return result;
	}
}
