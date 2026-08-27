package com.ffmpeg.compressor.model

data class CompressionSettings(
    // Tab 1: Input & Decoder
    val ffmpegPath: String = "ffmpeg/bin/ffmpeg",
    val inputVideo: String = "input_video.mp4",
    val inputDecoder: String = "h264_mediacodec",
    val inputAudio: String = "input_audio.mp3",
    val streamMapVideo: String = "0:v:0",
    val streamMapAudio: String = "1:a:0",
    val useSeparateAudio: Boolean = true,
    val useInputDecoder: Boolean = true,

    // Tab 2: Codec & Quality
    val videoCodec: String = "libx264",
    val preset: String = "slow",
    val crf: Int = 18,
    val frameRate: Int = 60,
    val profile: String = "high",
    val level: String = "4.2",
    val pixelFormat: String = "yuv420p",
    val gopSize: Int = 60,
    val minKeyint: Int = 30,

    // Tab 3: Tuning x264 Params
    val ref: Int = 4,
    val bframes: Int = 3,
    val me: String = "umh",
    val aqMode: Int = 2,
    val aqStrength: Float = 1.1f,
    val chromaQpOffset: Int = -2,
    val psyRd: String = "1.0,0.0",
    val deblock: String = "-2,-1",
    val noFastPskip: Int = 1,
    val mbtree: Int = 0,
    val scenecut: Int = 0,

    // Tab 4: Color & Resolution Scale
    val colorPrimaries: String = "bt709",
    val colorTrc: String = "bt709",
    val colorspace: String = "bt709",
    val swsFlags: String = "lanczos+accurate_rnd",
    val vfFilter: String = "setpts=N/(60*TB),scale='if(gt(iw,ih),-2,720)':'if(gt(iw,ih),720,-2)'",

    // Tab 5: Audio & Metadata
    val audioCodec: String = "libfdk_aac",
    val audioProfile: String = "aac_he",
    val audioBitrate: String = "64k",
    val audioFilter: String = "aresample=async=1000",
    val timescale: Int = 60000,
    val metadataTitle: String = "TikTok 8Bit Safe Color H264",
    val movflags: String = "+faststart",

    // Tab 6: Output Folder
    val outputDirectory: String = "output",
    val outputFilename: String = "compressed_output.mp4",

    // Active Selected Quick Preset
    val activePreset: PresetType = PresetType.TIKTOK_8BIT_SAFE
) {

    fun buildX264ParamsString(): String {
        return "ref=$ref:bframes=$bframes:me=$me:aq-mode=$aqMode:aq-strength=$aqStrength:psy-rd=$psyRd:deblock=$deblock:no-fast-pskip=$noFastPskip:mbtree=$mbtree:chroma-qp-offset=$chromaQpOffset:scenecut=$scenecut"
    }

    val fullOutputPath: String
        get() {
            val dir = outputDirectory.trimEnd('/', '\\')
            return if (dir.isEmpty()) outputFilename else "$dir/$outputFilename"
        }

    fun buildCommandString(customInput: String? = null, customOutput: String? = null): String {
        val inVid = customInput ?: inputVideo
        val outPath = customOutput ?: fullOutputPath

        val sb = StringBuilder()
        sb.append(ffmpegPath)

        if (useInputDecoder && inputDecoder.isNotBlank()) {
            sb.append(" -c:v $inputDecoder")
        }
        sb.append(" -i \"$inVid\"")

        if (useSeparateAudio && inputAudio.isNotBlank()) {
            sb.append(" -i \"$inputAudio\"")
        }

        if (streamMapVideo.isNotBlank()) {
            sb.append(" -map $streamMapVideo")
        }
        if (useSeparateAudio && streamMapAudio.isNotBlank()) {
            sb.append(" -map $streamMapAudio")
        }

        sb.append(" -c:v $videoCodec")
        if (preset.isNotBlank()) sb.append(" -preset $preset")
        sb.append(" -crf $crf")
        if (frameRate > 0) sb.append(" -r $frameRate")
        if (profile.isNotBlank() && profile != "auto") sb.append(" -profile:v $profile")
        if (level.isNotBlank() && level != "auto") sb.append(" -level:v $level")
        if (pixelFormat.isNotBlank()) sb.append(" -pix_fmt $pixelFormat")
        if (gopSize > 0) sb.append(" -g $gopSize")
        if (minKeyint > 0) sb.append(" -keyint_min $minKeyint")

        if (videoCodec.contains("264")) {
            sb.append(" -x264-params \"${buildX264ParamsString()}\"")
        }

        if (colorPrimaries.isNotBlank() && colorPrimaries != "auto") sb.append(" -color_primaries $colorPrimaries")
        if (colorTrc.isNotBlank() && colorTrc != "auto") sb.append(" -color_trc $colorTrc")
        if (colorspace.isNotBlank() && colorspace != "auto") sb.append(" -colorspace $colorspace")
        if (swsFlags.isNotBlank()) sb.append(" -sws_flags $swsFlags")
        if (vfFilter.isNotBlank()) sb.append(" -vf \"$vfFilter\"")

        if (audioCodec.isNotBlank()) sb.append(" -c:a $audioCodec")
        if (audioProfile.isNotBlank() && audioProfile != "auto") sb.append(" -profile:a $audioProfile")
        if (audioBitrate.isNotBlank()) sb.append(" -b:a $audioBitrate")
        if (audioFilter.isNotBlank()) sb.append(" -af \"$audioFilter\"")
        if (timescale > 0) sb.append(" -video_track_timescale $timescale")
        if (metadataTitle.isNotBlank()) sb.append(" -metadata title=\"$metadataTitle\"")
        if (movflags.isNotBlank()) sb.append(" -movflags $movflags")

        sb.append(" -y \"$outPath\"")

        return sb.toString()
    }

    fun buildCommandArgsList(actualInput: String, actualOutput: String): List<String> {
        val list = mutableListOf<String>()
        list.add(ffmpegPath)

        if (useInputDecoder && inputDecoder.isNotBlank()) {
            list.add("-c:v")
            list.add(inputDecoder)
        }
        list.add("-i")
        list.add(actualInput)

        if (useSeparateAudio && inputAudio.isNotBlank()) {
            list.add("-i")
            list.add(inputAudio)
        }

        if (streamMapVideo.isNotBlank()) {
            list.add("-map")
            list.add(streamMapVideo)
        }
        if (useSeparateAudio && streamMapAudio.isNotBlank()) {
            list.add("-map")
            list.add(streamMapAudio)
        }

        list.add("-c:v")
        list.add(videoCodec)
        if (preset.isNotBlank()) {
            list.add("-preset")
            list.add(preset)
        }
        list.add("-crf")
        list.add(crf.toString())
        if (frameRate > 0) {
            list.add("-r")
            list.add(frameRate.toString())
        }
        if (profile.isNotBlank() && profile != "auto") {
            list.add("-profile:v")
            list.add(profile)
        }
        if (level.isNotBlank() && level != "auto") {
            list.add("-level:v")
            list.add(level)
        }
        if (pixelFormat.isNotBlank()) {
            list.add("-pix_fmt")
            list.add(pixelFormat)
        }
        if (gopSize > 0) {
            list.add("-g")
            list.add(gopSize.toString())
        }
        if (minKeyint > 0) {
            list.add("-keyint_min")
            list.add(minKeyint.toString())
        }

        if (videoCodec.contains("264")) {
            list.add("-x264-params")
            list.add(buildX264ParamsString())
        }

        if (colorPrimaries.isNotBlank() && colorPrimaries != "auto") {
            list.add("-color_primaries")
            list.add(colorPrimaries)
        }
        if (colorTrc.isNotBlank() && colorTrc != "auto") {
            list.add("-color_trc")
            list.add(colorTrc)
        }
        if (colorspace.isNotBlank() && colorspace != "auto") {
            list.add("-colorspace")
            list.add(colorspace)
        }
        if (swsFlags.isNotBlank()) {
            list.add("-sws_flags")
            list.add(swsFlags)
        }
        if (vfFilter.isNotBlank()) {
            list.add("-vf")
            list.add(vfFilter)
        }

        if (audioCodec.isNotBlank()) {
            list.add("-c:a")
            list.add(audioCodec)
        }
        if (audioProfile.isNotBlank() && audioProfile != "auto") {
            list.add("-profile:a")
            list.add(audioProfile)
        }
        if (audioBitrate.isNotBlank()) {
            list.add("-b:a")
            list.add(audioBitrate)
        }
        if (audioFilter.isNotBlank()) {
            list.add("-af")
            list.add(audioFilter)
        }
        if (timescale > 0) {
            list.add("-video_track_timescale")
            list.add(timescale.toString())
        }
        if (metadataTitle.isNotBlank()) {
            list.add("-metadata")
            list.add("title=$metadataTitle")
        }
        if (movflags.isNotBlank()) {
            list.add("-movflags")
            list.add(movflags)
        }

        list.add("-y")
        list.add(actualOutput)

        return list
    }

    fun generateTermuxScript(): String {
        return """
            |#!/data/data/com.termux/files/usr/bin/bash
            |# FFmpeg Compressor Studio - Auto-Generated Termux Execution Script
            |echo "========================================="
            |echo "  FFmpeg Compressor Studio - Termux Mode "
            |echo "========================================="
            |
            |pkg install ffmpeg -y
            |
            |${buildCommandString().replace(ffmpegPath, "ffmpeg")}
            |
            |echo "Encoding Finished Successfully!"
        """.trimMargin()
    }

    companion object {
        fun getPresetSettings(preset: PresetType): CompressionSettings {
            return when (preset) {
                PresetType.TIKTOK_8BIT_SAFE -> CompressionSettings(
                    ffmpegPath = "ffmpeg/bin/ffmpeg",
                    inputVideo = "[input_video]",
                    inputDecoder = "h264_mediacodec",
                    inputAudio = "[input_audio]",
                    useInputDecoder = true,
                    useSeparateAudio = true,
                    streamMapVideo = "0:v:0",
                    streamMapAudio = "1:a:0",
                    videoCodec = "libx264",
                    preset = "slow",
                    crf = 18,
                    frameRate = 60,
                    profile = "high",
                    level = "4.2",
                    pixelFormat = "yuv420p",
                    gopSize = 60,
                    minKeyint = 30,
                    ref = 4,
                    bframes = 3,
                    me = "umh",
                    aqMode = 2,
                    aqStrength = 1.1f,
                    chromaQpOffset = -2,
                    psyRd = "1.0,0.0",
                    deblock = "-2,-1",
                    noFastPskip = 1,
                    mbtree = 0,
                    scenecut = 0,
                    colorPrimaries = "bt709",
                    colorTrc = "bt709",
                    colorspace = "bt709",
                    swsFlags = "lanczos+accurate_rnd",
                    vfFilter = "setpts=N/(60*TB),scale='if(gt(iw,ih),-2,720)':'if(gt(iw,ih),720,-2)'",
                    audioCodec = "libfdk_aac",
                    audioProfile = "aac_he",
                    audioBitrate = "64k",
                    audioFilter = "aresample=async=1000",
                    timescale = 60000,
                    metadataTitle = "TikTok 8Bit Safe Color H264",
                    movflags = "+faststart",
                    outputDirectory = "output",
                    outputFilename = "[output_path]",
                    activePreset = PresetType.TIKTOK_8BIT_SAFE
                )
                PresetType.ANDROID_MEDIACODEC_HW -> CompressionSettings(
                    ffmpegPath = "ffmpeg/bin/ffmpeg",
                    inputVideo = "[input_video]",
                    inputDecoder = "h264_mediacodec",
                    useInputDecoder = true,
                    useSeparateAudio = false,
                    videoCodec = "h264_mediacodec",
                    preset = "fast",
                    crf = 20,
                    frameRate = 60,
                    profile = "high",
                    level = "4.1",
                    pixelFormat = "nv12",
                    audioCodec = "aac",
                    audioBitrate = "128k",
                    metadataTitle = "Android MediaCodec HW Fast",
                    activePreset = PresetType.ANDROID_MEDIACODEC_HW
                )
                PresetType.X265_HEVC_ULTRA_HQ -> CompressionSettings(
                    ffmpegPath = "ffmpeg/bin/ffmpeg",
                    inputVideo = "[input_video]",
                    useInputDecoder = false,
                    useSeparateAudio = false,
                    videoCodec = "libx265",
                    preset = "medium",
                    crf = 20,
                    frameRate = 60,
                    profile = "main10",
                    level = "5.0",
                    pixelFormat = "yuv420p10le",
                    colorPrimaries = "bt709",
                    colorTrc = "bt709",
                    colorspace = "bt709",
                    audioCodec = "aac",
                    audioBitrate = "192k",
                    metadataTitle = "x265 Ultra High Quality 10Bit",
                    activePreset = PresetType.X265_HEVC_ULTRA_HQ
                )
                PresetType.AV1_NEXT_GEN -> CompressionSettings(
                    ffmpegPath = "ffmpeg/bin/ffmpeg",
                    inputVideo = "[input_video]",
                    useInputDecoder = false,
                    useSeparateAudio = false,
                    videoCodec = "libsvtav1",
                    preset = "6",
                    crf = 26,
                    frameRate = 30,
                    profile = "main",
                    pixelFormat = "yuv420p",
                    audioCodec = "libopus",
                    audioBitrate = "96k",
                    metadataTitle = "AV1 Next-Gen Lowest File Size",
                    activePreset = PresetType.AV1_NEXT_GEN
                )
                PresetType.VP9_WEB_YOUTUBE -> CompressionSettings(
                    ffmpegPath = "ffmpeg/bin/ffmpeg",
                    inputVideo = "[input_video]",
                    useInputDecoder = false,
                    useSeparateAudio = false,
                    videoCodec = "libvpx-vp9",
                    preset = "medium",
                    crf = 24,
                    frameRate = 60,
                    pixelFormat = "yuv420p",
                    audioCodec = "libopus",
                    audioBitrate = "128k",
                    metadataTitle = "VP9 WebM YouTube Ready",
                    activePreset = PresetType.VP9_WEB_YOUTUBE
                )
            }
        }
    }
}
