package com.hapi.player.utils

import android.media.MediaMetadataRetriever
import android.support.annotation.WorkerThread
import android.text.TextUtils
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @date 2018/12/14
 */
class MediaMetadataRetrieverUtil {





    suspend fun queryVideoParams(videoPath: String?) = suspendCoroutine<MediaParams?> { continuation ->
        continuation.resume(getVideoParams(videoPath))
    }


    private fun getVideoParams(videoPath: String?): MediaParams? {
        if (TextUtils.isEmpty(videoPath)) {
            return null
        }
        val media = MediaMetadataRetriever()
        try {
            val path = videoPath
            media.setDataSource(path)

            val video_length = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            val mimeType = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            val width = media.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
            val height = media.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()

            return MediaParams(path, width, height,video_length, mimeType)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            media?.let {
                it.release()
            }
        }
        return null
    }

}