package com.hapi.player.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.annotation.WorkerThread
import android.text.TextUtils
import android.util.Log
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @date 2018/12/14
 */
class MediaMetadataRetrieverUtil {





    suspend fun queryVideoParams(videoPath:String,header:Map<String,String>) = suspendCoroutine<MediaParams?> { continuation ->



        var mediaParams:MediaParams?=null
        val media = FFmpegMediaMetadataRetriever()
        try {
            Log.d("getVideoParams","thread"+Thread.currentThread().id)
            val path = videoPath
            media.setDataSource(path,header)
            val video_length = media.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            val width = media.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
            val height = media.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
            mediaParams=  MediaParams(path, width, height,video_length)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            media?.let {
                it.release()
            }
        }

        continuation.resume(mediaParams)
    }




}