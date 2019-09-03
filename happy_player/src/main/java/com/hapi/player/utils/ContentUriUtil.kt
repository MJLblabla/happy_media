package com.hapi.player.utils

import android.media.MediaMetadataRetriever
import android.support.annotation.WorkerThread
import android.text.TextUtils
import java.io.File

/**
 * @date 2018/12/14
 */
object ContentUriUtil {



    var callBack :( ()->MediaParams?)?=null



    fun queryVideoParams(videoPath: String?){

    }


            @WorkerThread
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
            return MediaParams(path, video_length, mimeType)
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