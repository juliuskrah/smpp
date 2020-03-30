package com.juliuskrah.smpp.utilities;

/**
 * Utility methods to validate strings
 * @author Julius Krah
 *
 */
public class StringUtils {
	private  StringUtils() {}
	
	public static void notBlank(String str, String message) {
		if (str == null || str.isBlank())
			throw new IllegalArgumentException(message);
	}

	public static void hasRequiredLength(String str, int expectedLength, String message) {
		if (str == null || str.length() != expectedLength)
			throw new IllegalArgumentException(message);
	}
}
