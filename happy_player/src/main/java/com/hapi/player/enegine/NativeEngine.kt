package com.hapi.player.enegine

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import com.hapi.player.AbsPlayerEngine
import com.hapi.player.PlayerStatus.*
import com.hapi.player.utils.LogUtil
import com.hapi.player.utils.PalyerUtil

/**
 * 原生播放引擎
 */
internal class NativeEngine(  context: Context) : AbsPlayerEngine(context) {

    private var mUrl: Uri? = null

    private var continueFromLastPosition = false
    private var mBufferPercentage: Int = 0
    private var mHeaders: Map<String, String>? = null
    private var isUsePreLoad = false

    /**
     * 原生 播放器
     */
    private val mMediaPlayer: MediaPlayer by lazy {
        val m = MediaPlayer()
        m.setAudioStreamType(AudioManager.STREAM_MUSIC)
        m.setOnPreparedListener(mOnPreparedListener)
        m.setOnCompletionListener(mOnCompletionListener)
        m.setOnErrorListener(mOnErrorListener)
        m.setOnInfoListener(mOnInfoListener)
        m.setOnBufferingUpdateListener(mOnBufferingUpdateListener)

        m
    }

    override fun setOnVideoSizeChangedListener(videoSizeChangedListener: MediaPlayer.OnVideoSizeChangedListener) {
        mMediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener)
    }

    override fun setSurface(surface: Surface) {
        mMediaPlayer.setSurface(surface)
    }

    override fun readPlayerConfig() {
        continueFromLastPosition = mPlayerConfig.isFromLastPosition
        mMediaPlayer.isLooping = mPlayerConfig.loop
    }


    override fun setUpAfterDealUrl(uir: Uri, headers: Map<String, String>?, preLoading: Boolean) {
        mUrl = uir
        mHeaders = headers
        isUsePreLoad = preLoading
        if(isUsePreLoad){
            openMedia()
        }
    }

    override fun startPlay() {
        if (mCurrentState == STATE_PRELOADED_WAITING || mCurrentState == STATE_PREPARED) {
            startCall.invoke()
            return
        }
        if(mCurrentState == STATE_PRELOADING){
            isUsePreLoad = false
            mCurrentState = STATE_PREPARING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            return
        }
        openMedia()
    }


    private fun openMedia() {

        try {
            savePotion()
            pause()
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            mCurrentState=  if(isUsePreLoad){
                STATE_PRELOADING
            }else{
                STATE_PREPARING
            }
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            mMediaPlayer.setDataSource(context.applicationContext, mUrl, mHeaders)
            mMediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            mCurrentState = STATE_ERROR
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("            mMediaPlayer.prepareAsync() 准备失败")
        }
    }

    override fun pause() {

        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause()
            mCurrentState = STATE_PAUSED
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_PAUSED")
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause()
            mCurrentState = STATE_BUFFERING_PAUSED
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_BUFFERING_PAUSED")
        }
    }

    override fun resume() {

        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start()
            mCurrentState = STATE_PLAYING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_PLAYING")
        } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start()
            mCurrentState = STATE_BUFFERING_PLAYING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_BUFFERING_PLAYING")
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mMediaPlayer.reset()
            openMedia()
        } else {
            LogUtil.d("VideoPlayer在mCurrentState == " + mCurrentState + "时不能调用restart()方法.")
        }
    }

    override fun seekTo(pos: Int) {
        mMediaPlayer.seekTo(pos)
    }

    override fun getDuration(): Long {
        return mMediaPlayer.duration.toLong()
    }

    override fun getBufferPercentage(): Int {
        return mBufferPercentage
    }

    override fun getCurrentPosition(): Long {
        return mMediaPlayer.currentPosition.toLong()
    }

    override fun releasePlayer() {
        super.releasePlayer()
        mMediaPlayer.release()
    }

    private val startCall = {
        reqestFouces()
        isUsePreLoad=false
        mMediaPlayer.start()
        // 从上次的保存位置播放
        if (continueFromLastPosition) {
            val savedPlayPosition =getLastPosition()
            if (savedPlayPosition > 0) {
                mMediaPlayer.seekTo(savedPlayPosition.toInt())
            }
        }
    }
    private val mOnPreparedListener = MediaPlayer.OnPreparedListener { mp ->
        if (isUsePreLoad) {
            mCurrentState = STATE_PRELOADED_WAITING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("onPrepared ——> STATE_PREPARED   wait noticePreLoading")
        } else {
            mCurrentState = STATE_PREPARED
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("onPrepared ——> STATE_PREPARED")
            startCall.invoke()
        }
    }


    private val mOnCompletionListener = MediaPlayer.OnCompletionListener {
        mCurrentState = STATE_COMPLETED
        mPlayerStatusListener.onPlayStateChanged(mCurrentState)
        savePotion()
        LogUtil.d("onCompletion ——> STATE_COMPLETED")

    }


    private val mOnErrorListener = object : MediaPlayer.OnErrorListener {
        override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
            mCurrentState = STATE_ERROR
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            return true
        }
    }

    private val mOnInfoListener = object : MediaPlayer.OnInfoListener {
        override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                if(mMediaPlayer.isPlaying){
                    mCurrentState = STATE_PLAYING
                    mPlayerStatusListener.onPlayStateChanged(mCurrentState)
                    LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING")
                }else{
                    LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：视频暂停中 但是切换旋转回调了播放")
                }
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED")
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING")
                }
                mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING
                    mPlayerStatusListener.onPlayStateChanged(mCurrentState)
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING")
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED
                    mPlayerStatusListener.onPlayStateChanged(mCurrentState)
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED")
                }
            }
//            else if (what == MediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
//                // 视频旋转了extra度，需要恢复
//                if (mTextureView != null) {
//                    mTextureView.setRotation(extra)
//                    LogUtil.d("视频旋转角度：$extra")
//                }
//            }
//
            else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                LogUtil.d("视频不能seekTo，为直播视频")
            } else {
                LogUtil.d("onInfo ——> what：$what")
            }
            return true
        }
    }

    private val mOnBufferingUpdateListener =
        MediaPlayer.OnBufferingUpdateListener { mp, percent -> mBufferPercentage = percent }

    override fun isPlaying(): Boolean {
        return mMediaPlayer.isPlaying
    }

}