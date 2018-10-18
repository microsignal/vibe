package io.microvibe.booster.core.base.search.builder.support;

import java.util.concurrent.atomic.AtomicInteger;

public class BindedKeys {

	private static final ThreadLocal<AtomicInteger> BINDED_SEQ = ThreadLocal.withInitial(() -> new AtomicInteger(0));

	public static int nextSeq() {
		return BINDED_SEQ.get().incrementAndGet();
	}

	public static void resetSeq() {
		BINDED_SEQ.remove();
	}

	public static String nextBindedKey() {
		long nextSeq = 0xFFFFFFFFL & nextSeq();
		return "p" + nextSeq;
	}
}
