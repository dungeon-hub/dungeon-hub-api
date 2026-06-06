package net.dungeonhub.enums

import java.time.Period
import java.time.temporal.TemporalAmount

enum class WarningType(val expiration: TemporalAmount?) {
    Strike(Period.ofMonths(3)),
    Minor,
    Major,
    Serious,
    Warning(Period.ofMonths(1));

    constructor(): this(null)
}