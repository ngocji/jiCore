package com.jibase.utils

fun progressToValue(progress: Float, min: Float, max: Float): Float {
    return progress * (max - min) / 100f + min
}

fun valueToProgress(value: Float, min: Float, max: Float): Float {
    return (value - min) * 100f / (max - min)
}