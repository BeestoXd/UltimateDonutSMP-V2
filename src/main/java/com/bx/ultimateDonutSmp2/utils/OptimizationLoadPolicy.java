package com.bx.ultimateDonutSmp2.utils;

/**
 * Maps a TPS/MSPT sample to an optimization load state.
 *
 * <p>CRITICAL follows TPS only. Paper's average tick time can spike for a few ticks
 * while the one-minute TPS is still ~19 (creative fly, a teleport, one slow chunk).
 * Treating that MSPT spike as CRITICAL throttles scoreboard/tablist and looks like a
 * crash in the log when the server is otherwise fine.</p>
 */
public final class OptimizationLoadPolicy {

    public enum State {
        NORMAL,
        WARN,
        CRITICAL
    }

    private OptimizationLoadPolicy() {
    }

    public static State classify(
            double tps,
            double mspt,
            double criticalTps,
            double warnTps,
            double warnMspt
    ) {
        if (tps > 0.0D && tps < criticalTps) {
            return State.CRITICAL;
        }
        if ((tps > 0.0D && tps < warnTps) || (mspt > 0.0D && mspt > warnMspt)) {
            return State.WARN;
        }
        return State.NORMAL;
    }

    /**
     * Paper's one-minute TPS after {@code Done} is the whole short tick window, including
     * JVM JIT and delayed init. Classifying that as CRITICAL throttles tasks and looks like
     * a crash while nobody is online.
     */
    public static boolean inStartupGrace(long uptimeMillis, long graceMillis) {
        return graceMillis > 0L && uptimeMillis >= 0L && uptimeMillis < graceMillis;
    }
}
