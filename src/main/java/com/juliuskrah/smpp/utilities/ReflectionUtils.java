package com.juliuskrah.smpp.utilities;

import java.lang.reflect.Field;

/**
 * Provides exclusively static methods for performing reflection
 * @author Julius Krah
 */
public class ReflectionUtils {
	private ReflectionUtils() {}
	
	public static Field getField(Object object, String fieldName) {
		Class<?> clazz = object.getClass();
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Failed to retrieve field", e);
		}
	}

	public static Object getField(Object object, Field field) {
		field.setAccessible(true);
		try {
			return field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Failed to read field value", e);
		}
	}

	public static <T> T getField(Object object, Field field, Class<T> expectedType) {
		field.setAccessible(true);
		try {
			return expectedType.cast(field.get(object));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Failed to read field value", e);
		}
	}
}
