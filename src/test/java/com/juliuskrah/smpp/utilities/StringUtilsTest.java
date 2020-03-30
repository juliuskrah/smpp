package com.juliuskrah.smpp.utilities;

import static com.juliuskrah.smpp.utilities.StringUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {
	@Test
	@DisplayName("throws IllegalArgumentException when string is null or empty")
	public void nullOrBlankStringException() {
		assertThrows(IllegalArgumentException.class, () -> {
			notBlank(null, "Must not be null or empty");
		}, "Must not be null or empty");
		assertThrows(IllegalArgumentException.class, () -> {
			notBlank("", "Must not be null or empty");
		}, "Must not be null or empty");
		assertThrows(IllegalArgumentException.class, () -> {
			notBlank("           ", "Must not be null or empty");
		}, "Must not be null or empty");
	}

	@Test
	@DisplayName("throws IllegalArgumentException when string length is not equal to provided length")
	public void requiredLengthException() {
		assertThrows(IllegalArgumentException.class, () -> {
			hasRequiredLength(null, 32, "Length must of String must be 32");
		}, "Length must of String must be 32");

		assertThrows(IllegalArgumentException.class, () -> {
			hasRequiredLength("", 2, "Length must of String must be 2");
		}, "Length must of String must be 2");

	}

	@Test
	@DisplayName("does not execute exceptionally")
	public void requiredLengthPasses() {
		hasRequiredLength("c87147ef9892426fb5b366382471d145", 32, "Length must of String must be 32");
	}
}
