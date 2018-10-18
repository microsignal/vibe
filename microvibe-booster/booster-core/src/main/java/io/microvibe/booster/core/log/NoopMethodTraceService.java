package io.microvibe.booster.core.log;

public class NoopMethodTraceService implements IMethodTraceService {
	@Override
	public boolean logForThrowing(LogInfo info) {
		return false;
	}

	@Override
	public boolean logForAfter(LogInfo info) {
		return false;
	}

	@Override
	public boolean logForBefore(LogInfo info) {
		return false;
	}
}
