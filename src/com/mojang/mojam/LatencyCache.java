package com.mojang.mojam;

public class LatencyCache {

	private static final int CACHE_EMPTY = 0, CACHE_PRIMING = 1, CACHE_PRIMED = 2;
	private static final int CACHE_SIZE = 5;

	private int latencyCacheState = CACHE_EMPTY;
	private int nextLatencyCacheIdx = 0;
	private int[] latencyCache = new int[CACHE_SIZE];

	public void addToLatencyCache(int latency) {
		if (nextLatencyCacheIdx >= latencyCache.length)
			nextLatencyCacheIdx = 0;
		if (latencyCacheState != CACHE_PRIMED) {
			if (nextLatencyCacheIdx == 0 && latencyCacheState == CACHE_PRIMING)
				latencyCacheState = CACHE_PRIMED;
			if (latencyCacheState == CACHE_EMPTY)
				latencyCacheState = CACHE_PRIMING;
		}
		latencyCache[nextLatencyCacheIdx++] = latency;
	}

	public boolean latencyCacheReady() {
		return latencyCacheState == CACHE_PRIMED;
	}

	public int avgLatency() {
		int total = 0;
		for (int latency : latencyCache) {
			total += latency;
		}
		return total / latencyCache.length; // rounds down
	}

}
