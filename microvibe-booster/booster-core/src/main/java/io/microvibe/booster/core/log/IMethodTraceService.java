package io.microvibe.booster.core.log;

/**
 * @author Qt
 * @since May 15, 2018
 */
public interface IMethodTraceService {
	/**
	 * @param info
	 * @return 是否跳过默认的日志记录行为
	 */
	boolean logForThrowing(LogInfo info);

	/**
	 * @param info
	 * @return 是否跳过默认的日志记录行为
	 */
	boolean logForAfter(LogInfo info);

	/**
	 * @param info
	 * @return 是否跳过默认的日志记录行为
	 */
	boolean logForBefore(LogInfo info);

}
