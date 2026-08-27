package com.ffmpeg.compressor.model

enum class PresetType(
    val title: String,
    val description: String,
    val badge: String
) {
    TIKTOK_8BIT_SAFE(
        title = "TikTok / IG 8-Bit Safe",
        description = "Optimized H.264 profile high, 720p scaling, 60fps & HE-AAC audio",
        badge = "DEFAULT SAFE"
    ),
    ANDROID_MEDIACODEC_HW(
        title = "Android MediaCodec HW",
        description = "Hardware accelerated decoding & encoding for ultra fast processing",
        badge = "HARDWARE FAST"
    ),
    X265_HEVC_ULTRA_HQ(
        title = "x265 / HEVC Ultra HQ",
        description = "High quality 10-bit H.265 compression with ultra high fidelity",
        badge = "ULTRA QUALITY"
    ),
    AV1_NEXT_GEN(
        title = "AV1 Next-Gen",
        description = "SVT-AV1 codec for maximum file size reduction with high visual quality",
        badge = "SMALLEST SIZE"
    ),
    VP9_WEB_YOUTUBE(
        title = "VP9 Web & YouTube",
        description = "WebM VP9 format compatible with HTML5 web browsers and YouTube",
        badge = "WEB READY"
    )
}
