package com.juliuskrah.smpp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

import com.juliuskrah.smpp.utilities.StringUtils;

/**
 * 
 * @author Julius Krah
 *
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Creates a UUID from raw string without dashes
	 * @param raw UUID without dashes
	 * @return
	 */
	public static java.util.UUID toUUID(String raw) {
		StringUtils.notBlank(raw, "'raw' cannot be empty");
		StringUtils.hasRequiredLength(raw, 32, "'raw' must be of length 32");
		return java.util.UUID.fromString( //
				raw.replaceFirst( //
						"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", //
						"$1-$2-$3-$4-$5"));
	}

	@Nullable
	public static ZonedDateTime toZonedDateTime(LocalDateTime dateTime) {
		if (dateTime == null)
			return null;
		var timeZone = LocaleContextHolder.getTimeZone();
		return dateTime.atZone(timeZone.toZoneId());
	}

	@Nullable
	public static LocalDateTime toLocalDateTime(Instant instant) {
		if (instant == null)
			return null;
		var timeZone = LocaleContextHolder.getTimeZone();
		return LocalDateTime.ofInstant(instant, timeZone.toZoneId());
	}
}
