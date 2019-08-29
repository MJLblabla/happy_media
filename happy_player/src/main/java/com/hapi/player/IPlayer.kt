package com.hapi.player

import android.net.Uri

interface IPlayer {



    fun getCurrentUrl():Uri?

    fun startPlay(uir: Uri,  headers :Map<String, String> ?=null,position: Int = 0, loop: Boolean = false, cache: Boolean = false)

    fun startPlayFromLastPosion(uir: Uri, headers :Map<String, String>?=null,loop: Boolean = false, cache: Boolean = false)

    fun setListener(lister: PlayerStatusListener,add: Boolean)
    fun pause()

    fun resume()


    fun seekTo(pos: Int)


    /**
     * 设置音量
     *
     * @param volume 音量值
     */
    fun setVolume(volume: Int)


    fun getCurrentPlayStatus(): Int


    /**
     * 获取最大音量
     *
     * @return 最大音量值
     */
    fun getMaxVolume(): Int

    /**
     * 获取当前音量
     *
     * @return 当前音量值
     */
    fun getVolume(): Int

    /**
     * 获取办法给总时长，毫秒
     *
     * @return 视频总时长ms
     */
    fun getDuration(): Long

    /**
     * 获取当前播放的位置，毫秒
     *
     * @return 当前播放位置，ms
     */
    fun getCurrentPosition(): Long


     fun releasePlayer()

     fun isIdle(): Boolean
     fun isPreparing(): Boolean
     fun isPrepared(): Boolean
     fun isBufferingPlaying(): Boolean
     fun isBufferingPaused(): Boolean
     fun isPlaying(): Boolean
     fun isPaused(): Boolean
     fun isError(): Boolean
     fun isCompleted(): Boolean

}