package com.ffmpeg.compressor.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object FFmpegExtractor {

    suspend fun extractIfNeeded(
        context: Context,
        zipFileName: String = "ffmpeg.zip",
        onProgress: (progress: Float, status: String) -> Unit
    ): String = withContext(Dispatchers.IO) {
        // PERBAIKAN: Gunakan filesDir privat dengan nama berakhiran .so (Lolos SELinux & Aman Izin Tulis)
        val targetFile = File(context.filesDir, "libffmpeg.so")

        // Jika biner sudah ada, ukurannya valid (>10MB), dan bisa dieksekusi, langsung pakai
        if (targetFile.exists() && targetFile.length() > 10 * 1024 * 1024 && targetFile.canExecute()) {
            return@withContext targetFile.absolutePath
        }

        withContext(Dispatchers.Main) {
            onProgress(0f, "Menyiapkan ekstraksi file biner...")
        }

        var totalBytes = 0L
        try {
            context.assets.open(zipFileName).use { input ->
                ZipInputStream(input).use { zipInput ->
                    var entry = zipInput.nextEntry
                    while (entry != null) {
                        if (entry.size > 0) totalBytes += entry.size
                        entry = zipInput.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (totalBytes <= 0) totalBytes = 30 * 1024 * 1024L

        // Proses ekstraksi dari assets/ffmpeg.zip
        context.assets.open(zipFileName).use { input ->
            ZipInputStream(input).use { zipInput ->
                var entry = zipInput.nextEntry
                while (entry != null) {
                    // Mencari entry biner 'ffmpeg' di dalam zip
                    if (entry.name == "ffmpeg" || entry.name.endsWith("ffmpeg") || entry.name.endsWith(".so")) {
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var extractedBytes = 0L

                        FileOutputStream(targetFile).use { output ->
                            while (zipInput.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                extractedBytes += bytesRead

                                val progress = ((extractedBytes.toFloat() / totalBytes.toFloat()) * 100f).coerceIn(0f, 99f)
                                withContext(Dispatchers.Main) {
                                    onProgress(progress, "Mengekstrak Biner FFmpeg... (${progress.toInt()}%)")
                                }
                            }
                        }
                        break
                    }
                    entry = zipInput.nextEntry
                }
            }
        }

        // Paksa beri izin eksekusi Linux (chmod 755)
        try {
            targetFile.setExecutable(true, false)
            val chmodProcess = Runtime.getRuntime().exec("chmod 755 ${targetFile.absolutePath}")
            chmodProcess.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        withContext(Dispatchers.Main) {
            onProgress(100f, "Ekstraksi Selesai!")
        }

        return@withContext targetFile.absolutePath
    }
}