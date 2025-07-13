@file:OptIn(ExperimentalTime::class)

package com.glanci.core.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime


fun getCurrentTimestamp(): Long {
    return Clock.System.now().toEpochMilliseconds()
}
