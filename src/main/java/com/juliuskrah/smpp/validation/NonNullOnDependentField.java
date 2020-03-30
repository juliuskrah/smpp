package com.juliuskrah.smpp.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates a field only if another field has certain value
 * 
 * @author Julius Krah
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Repeatable(NonNullOnDependentField.List.class)
@Retention(RUNTIME)
@Constraint(validatedBy = NonNullOnDependentFieldValidator.class)
@Documented
public @interface NonNullOnDependentField {
	String fieldName();

	String[] fieldValue();

	String dependentFieldName();

	String message() default "{NonNullOnDependentFeild.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ TYPE, ANNOTATION_TYPE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		NonNullOnDependentField[] value();
	}
}
