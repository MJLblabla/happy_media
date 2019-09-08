package com.hapi.player.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import com.hapi.player.been.MediaParams
import com.hapi.player.room.MediaDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @date 2018/12/14
 */
class MediaMetadataRetrieverUtil {


    suspend fun queryVideoParams(context: Context, videoPath: String, header: Map<String, String>) =
        suspendCoroutine<MediaParams?> { continuation ->


            val cache = MediaDataBase.getInstance(context).mediaDao.getMediaParams(videoPath)
            if (cache == null) {
                LogUtil.d("getVideoParams cache==null" + Thread.currentThread().id)

                GlobalScope.launch(Dispatchers.Default) {
                    // 将会获取默认调度器
                    println("Default               : I'm working in thread ${Thread.currentThread().name}")
                    var mediaParams: MediaParams? = null
                    val media = MediaMetadataRetriever()
                    try {
                        LogUtil.d("getVideoParams thread" + Thread.currentThread().id)
                        val path = videoPath
                        media.setDataSource(path, header)
                        val video_length = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
                        val width = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
                        val height = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
                        mediaParams = MediaParams()
                        mediaParams.height = height
                        mediaParams.width = width
                        mediaParams.path = videoPath
                        mediaParams.video_length =video_length

                        LogUtil.d("getVideoParams thread" + Thread.currentThread().id + "  " + width + "      " + height)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        media.let {
                            it.release()
                        }
                    }
                    mediaParams?.let {
                        MediaDataBase.getInstance(context).mediaDao.insert(mediaParams)
                    }
                    continuation.resume(mediaParams)
                }

            } else {
                LogUtil.d("getVideoParams cache!=null" + Thread.currentThread().id)
                continuation.resume(cache)
            }


        }


}