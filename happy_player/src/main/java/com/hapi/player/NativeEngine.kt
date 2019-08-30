package com.hapi.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import com.hapi.player.PlayerStatus.*
import com.hapi.player.utils.LogUtil
import com.hapi.player.utils.PalyerUtil
import com.hapi.player.video.VideoPlayerManager

/**
 * 原生播放引擎
 */
class NativeEngine(private val context: Context) : IPlayerEngine() {




    private var mCurrentState = STATE_IDLE

    private var skipToPosition: Int = 0
    private var continueFromLastPosition = false
    private var mBufferPercentage: Int = 0
    private var mHeaders: Map<String, String>? = null


    /**
     * 原生 播放器
     */
    val mMediaPlayer: MediaPlayer by lazy {
        val m = MediaPlayer()
        m.setAudioStreamType(AudioManager.STREAM_MUSIC)
        m.setOnPreparedListener(mOnPreparedListener)
        m.setOnCompletionListener(mOnCompletionListener)
        m.setOnErrorListener(mOnErrorListener)
        m.setOnInfoListener(mOnInfoListener)
        m.setOnBufferingUpdateListener(mOnBufferingUpdateListener)

        m
    }


    /**
     * 音频焦点
     */
    private val mAudioManager: AudioManager by lazy {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        audioManager
    }

    private var audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> pause()
                else -> {

                }
            }
        }

    override fun setOnVideoSizeChangedListener(videoSizeChangedListener: MediaPlayer.OnVideoSizeChangedListener) {
        mMediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener)
    }

    override fun setSurface(surface: Surface) {
        mMediaPlayer.setSurface(surface)
    }


    override fun startPlay(uir: Uri, headers: Map<String, String>?, position: Int, loop: Boolean, cache: Boolean) {
        mUrl = uir
        skipToPosition = position
        mMediaPlayer.isLooping = loop
        mHeaders = headers
        openMedia()
    }


    private fun openMedia() {
        mCurrentState = STATE_PREPARING
        mPlayerStatusListener.onPlayStateChanged(mCurrentState)

        try {
            pause()
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            mMediaPlayer.setDataSource(context.applicationContext, mUrl, mHeaders)
            mMediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            mCurrentState = STATE_ERROR
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("            mMediaPlayer.prepareAsync() 准备失败")
        }
    }


    override fun startPlayFromLastPosion(uir: Uri, headers: Map<String, String>?, loop: Boolean, cache: Boolean) {
        startPlay(uir, headers, 0, loop, cache)
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


    override fun setVolume(volume: Int) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    override fun getCurrentPlayStatus(): Int {
        return mCurrentState;
    }

    override fun getMaxVolume(): Int {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    override fun getVolume(): Int {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
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

        mCurrentState = STATE_IDLE
        mAudioManager.abandonAudioFocus(audioFocusChangeListener)
        mMediaPlayer.release()
        mPlayerStatusListener.onPlayStateChanged(mCurrentState)
        if (isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused()) {
            PalyerUtil.savePlayPosition(context, mUrl.toString(), getCurrentPosition().toInt())
        } else if (isCompleted()) {
            PalyerUtil.savePlayPosition(context, mUrl.toString(), 0)
        }

        Runtime.getRuntime().gc()
    }


    private val mOnPreparedListener = MediaPlayer.OnPreparedListener { mp ->
        mCurrentState = STATE_PREPARED
        mPlayerStatusListener.onPlayStateChanged(mCurrentState)
        LogUtil.d("onPrepared ——> STATE_PREPARED")
        mp.start()
        // 从上次的保存位置播放
        if (continueFromLastPosition) {
            val savedPlayPosition = PalyerUtil.getSavedPlayPosition(context, mUrl?.path)
            mp.seekTo(savedPlayPosition.toInt())
        }
        // 跳到指定位置播放
        if (skipToPosition != 0) {
            mp.seekTo(skipToPosition)
        }
    }

    private val mOnCompletionListener = MediaPlayer.OnCompletionListener {
        mCurrentState = STATE_COMPLETED
        mPlayerStatusListener.onPlayStateChanged(mCurrentState)
        LogUtil.d("onCompletion ——> STATE_COMPLETED")
        // 清除屏幕常亮
        //  mContainer.setKeepScreenOn(false)
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
                mCurrentState = STATE_PLAYING
                mPlayerStatusListener.onPlayStateChanged(mCurrentState)
                LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING")
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


    override fun isIdle(): Boolean {
        return mCurrentState == STATE_IDLE
    }

    override fun isPreparing(): Boolean {
        return mCurrentState == STATE_PREPARING
    }

    override fun isPrepared(): Boolean {
        return mCurrentState == STATE_PREPARED
    }

    override fun isBufferingPlaying(): Boolean {
        return mCurrentState == STATE_BUFFERING_PLAYING
    }

    override fun isBufferingPaused(): Boolean {
        return mCurrentState == STATE_BUFFERING_PAUSED
    }

    override fun isPlaying(): Boolean {
        return mMediaPlayer.isPlaying
    }

    override fun isPaused(): Boolean {
        return mCurrentState == STATE_PAUSED || isBufferingPaused()
    }

    override fun isError(): Boolean {
        return mCurrentState == STATE_ERROR
    }

    override fun isCompleted(): Boolean {
        return mCurrentState == STATE_COMPLETED
    }


}