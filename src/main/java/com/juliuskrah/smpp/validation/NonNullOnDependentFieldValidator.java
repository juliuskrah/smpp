package com.juliuskrah.smpp.validation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.juliuskrah.smpp.utilities.ReflectionUtils;

/**
 * Validates when another field has a certain value
 * 
 * @author Julius Krah
 */
public class NonNullOnDependentFieldValidator implements ConstraintValidator<NonNullOnDependentField, Object> {
	private String fieldName;
	private String[] expectedFieldValue;
	private String dependentFieldName;

	@Override
	public void initialize(NonNullOnDependentField annotation) {
		fieldName = annotation.fieldName();
		expectedFieldValue = annotation.fieldValue();
		dependentFieldName = annotation.dependentFieldName();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null)
			return true;
		List<Object> expectedValues = new ArrayList<>();
		Field expectField = ReflectionUtils.getField(value, fieldName);
		Field expectDependentField = ReflectionUtils.getField(value, dependentFieldName);

		Object expectFieldValue = ReflectionUtils.getField(value, expectField);
		Object expectDependentFieldValue = ReflectionUtils.getField(value, expectDependentField);

		for (String strValue : expectedFieldValue) {
			if (expectField.getType().isEnum()) {
				Class clazz = expectField.getType();
				Enum enums = Enum.valueOf(clazz, strValue);
				expectedValues.add(enums);
			} else
				expectedValues.add(strValue);
		}

		if (expectedValues.contains(expectFieldValue) && expectDependentFieldValue == null) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode(dependentFieldName) //
					.addConstraintViolation();
			return false;
		}

		return true;
	}

}
