
package com.millburnx.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

public fun Double.toRadians(): Double = Math.toRadians(this)

public fun Double.toDegrees(): Double = Math.toDegrees(this)

public fun Double.normalizeRadians(): Double {
    return atan2(sin(this), cos(this))
}

public fun Double.normalize(): Double {
    return this.toRadians().normalizeRadians().toDegrees()
}