package io.microvibe.booster.core.schedule.err;

import io.microvibe.booster.commons.err.MessageException;
import io.microvibe.booster.commons.err.ValidationException;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
public class ScheduleException extends MessageException {

	private static final long serialVersionUID = 1L;

	public ScheduleException() {
		super();
	}

	public ScheduleException(String code, Object... params) {
		super(code, params);
	}

	public ScheduleException(Throwable cause) {
		super(cause);
	}

	public ScheduleException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

	public static void shouldNotHappen(Throwable cause) throws ScheduleException {
		throw new ScheduleException(cause);
	}

}
