package net.talentum.jackie.tools;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicTools {

	public static boolean getAndNegate(AtomicBoolean atomicBoolean) {
		boolean v;
		do {
		  v = atomicBoolean.get();
		} while (!atomicBoolean.compareAndSet(v, !v));
		return v;
	}
	
}
