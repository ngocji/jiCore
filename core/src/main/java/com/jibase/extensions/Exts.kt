package com.jibase.extensions

fun Long.formatMinSecDuration(): String {
    val totalSeconds = (this + 500) / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun Long.formatFullDuration(): String {
    val totalSeconds = (this + 500) / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun Long.formatSize() = toDouble().formatSize()

fun Double.formatSize(): String {
    return try {
        val kilobyte = 1024f
        val megabyte = kilobyte * 1024f
        val gigabyte = megabyte * 1024f
        val terabyte = gigabyte * 1024f

        if ((this >= 0) && (this < kilobyte)) {
            "$this B"
        } else if ((this >= kilobyte) && (this < megabyte)) {
            "${String.format("%.2f", (this / kilobyte))} KB"
        } else if ((this >= megabyte) && (this < gigabyte)) {
            "${String.format("%.2f", this / megabyte)} MB"
        } else if ((this >= gigabyte) && (this < terabyte)) {
            "${String.format("%.2f", this / gigabyte)} GB"
        } else if (this >= terabyte) {
            "${String.format("%.2f", this / terabyte)} TB"
        } else {
            "$this Bytes"
        }
    } catch (e: Exception) {
        ""
    }
}