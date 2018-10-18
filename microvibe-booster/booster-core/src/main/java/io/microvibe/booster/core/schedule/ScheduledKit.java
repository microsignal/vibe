package io.microvibe.booster.core.schedule;

import org.quartz.CronExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
public class ScheduledKit {


	/**
	 * 获取定时任务下次的执行时间（精确到毫秒）
	 *
	 * @param scheduled
	 * @param currentTimeMillis
	 * @param embeddedValueResolver
	 * @return
	 */
	public static long getNextTimeMillis(Scheduled scheduled, long currentTimeMillis, StringValueResolver embeddedValueResolver) {
		if (StringUtils.hasText(scheduled.cron())) {
			try {
				CronExpression exp = new CronExpression(scheduled.cron());
				String zone = scheduled.zone();
				TimeZone timeZone;
				if (StringUtils.hasText(zone)) {
					timeZone = StringUtils.parseTimeZoneString(zone);
					exp.setTimeZone(timeZone);
				}
				Date nextTime = exp.getNextValidTimeAfter(new Date(currentTimeMillis));
				return nextTime.getTime();
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		}
		if (scheduled.fixedDelay() > 0 || StringUtils.hasText(scheduled.fixedDelayString())) {
			if (scheduled.fixedDelay() > 0) {
				return currentTimeMillis + scheduled.fixedDelay();
			}

			String fixedDelayString = scheduled.fixedDelayString();
			fixedDelayString = embeddedValueResolver.resolveStringValue(fixedDelayString);
			long fixedDelay = Long.parseLong(fixedDelayString);

			return currentTimeMillis + fixedDelay;
		}
		if (scheduled.fixedRate() > 0 || StringUtils.hasText(scheduled.fixedRateString())) {
			if (scheduled.fixedRate() > 0) {
				return currentTimeMillis + scheduled.fixedRate();
			}

			String fixedRateString = scheduled.fixedRateString();
			fixedRateString = embeddedValueResolver.resolveStringValue(fixedRateString);
			long fixedRate = Long.parseLong(fixedRateString);

			return currentTimeMillis + fixedRate;
		}
		return Long.MAX_VALUE;
	}

	/**
	 * 获取下次执行任务的间隔时间(精确到毫秒)
	 *
	 * @param scheduled
	 * @param embeddedValueResolver
	 * @return
	 */
	public static long getNextTimeInterval(Scheduled scheduled, StringValueResolver embeddedValueResolver) {
		if (StringUtils.hasText(scheduled.cron())) {
			try {
				CronExpression exp = new CronExpression(scheduled.cron());

				String zone = scheduled.zone();
				TimeZone timeZone;
				if (StringUtils.hasText(zone)) {
					timeZone = StringUtils.parseTimeZoneString(zone);
					exp.setTimeZone(timeZone);
				}

				long currentTimeMillis = System.currentTimeMillis();
				Date nextTime = exp.getNextValidTimeAfter(new Date(currentTimeMillis));
				return nextTime.getTime() - currentTimeMillis;
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		}
		if (scheduled.fixedDelay() > 0 || StringUtils.hasText(scheduled.fixedDelayString())) {
			if (scheduled.fixedDelay() > 0) {
				return scheduled.fixedDelay();
			}

			String fixedDelayString = scheduled.fixedDelayString();
			fixedDelayString = embeddedValueResolver.resolveStringValue(fixedDelayString);
			long fixedDelay = Long.parseLong(fixedDelayString);

			return fixedDelay;
		}
		if (scheduled.fixedRate() > 0 || StringUtils.hasText(scheduled.fixedRateString())) {
			if (scheduled.fixedRate() > 0) {
				return scheduled.fixedRate();
			}

			String fixedRateString = scheduled.fixedRateString();
			fixedRateString = embeddedValueResolver.resolveStringValue(fixedRateString);
			long fixedRate = Long.parseLong(fixedRateString);

			return fixedRate;
		}

		return 0;
	}
}
