package com.ffmpeg.compressor.model

import kotlinx.serialization.Serializable

@Serializable
data class DraftItem(
    val id: String,
    val originalPath: String,
    val draftPath: String,
    val outputFilename: String,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val isPermanentlySaved: Boolean = false
) {
    val isExpired: Boolean
        get() {
            val maxAgeMillis = 7L * 24L * 60L * 60L * 1000L
            return (System.currentTimeMillis() - creationTimestamp) > maxAgeMillis
        }
}
