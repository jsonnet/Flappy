package utility;

public class LogHelper {

	private static boolean DEBUG = true;

	public static void log(Object object) {
		if (LogHelper.DEBUG) System.out.println(object);
	}

	// (c) 2016 Joshua Sonnet
}
