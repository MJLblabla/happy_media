package com.hapi.player.engine

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.view.Surface
import com.hapi.player.AbsPlayerEngine
import com.hapi.player.PlayerStatus
import com.hapi.player.utils.LogUtil
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer



class IjkEngine (context: Context) : AbsPlayerEngine(context) {


    private var mUrl: Uri? = null

    private var continueFromLastPosition = false
    private var mBufferPercentage: Int = 0
    private var mHeaders: Map<String, String>? = null
    private var isUsePreLoad = false

    /**
     * 原生 播放器
     */
    private val mIMediaPlayer: IMediaPlayer by lazy {
        val m = IjkMediaPlayer()
        m.setAudioStreamType(AudioManager.STREAM_MUSIC)
        m.setOnPreparedListener(mOnPreparedListener)
        m.setOnCompletionListener(mOnCompletionListener)
        m.setOnErrorListener(mOnErrorListener)
        m.setOnInfoListener(mOnInfoListener)
        m.setOnBufferingUpdateListener(mOnBufferingUpdateListener)

        m
    }



    override fun setSurface(surface: Surface) {
        mIMediaPlayer.setSurface(surface)
    }

    override fun readPlayerConfig() {
        continueFromLastPosition = mPlayerConfig.isFromLastPosition
        mIMediaPlayer.isLooping = mPlayerConfig.loop
    }


    override fun setUpAfterDealUrl(uir: Uri, headers: Map<String, String>?, preLoading: Boolean) {
        mUrl = uir
        mHeaders = headers
        isUsePreLoad = preLoading
        if (isUsePreLoad) {
            openMedia()
        }
    }

    override fun startPlay() {
        if (mCurrentState == PlayerStatus.STATE_PRELOADED_WAITING || mCurrentState == PlayerStatus.STATE_PREPARED) {
            startCall.invoke()
            return
        }
        if (mCurrentState == PlayerStatus.STATE_PRELOADING) {
            isUsePreLoad = false
            mCurrentState = PlayerStatus.STATE_PREPARING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            return
        }
        openMedia()
    }


    private fun openMedia() {

        try {
            savePotion()
            pause()
            mIMediaPlayer.stop()
            mIMediaPlayer.reset()
            mIMediaPlayer.isLooping = mPlayerConfig.loop
            mCurrentState = if (isUsePreLoad) {
                PlayerStatus.STATE_PRELOADING
            } else {
                PlayerStatus.STATE_PREPARING
            }
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            mIMediaPlayer.setDataSource(context.applicationContext, mUrl, mHeaders)
            mIMediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            mCurrentState = PlayerStatus.STATE_ERROR
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("            mIMediaPlayer.prepareAsync() 准备失败")
        }
    }

    override fun setOnVideoSizeChangedListener(videoSizeChangedListener: OnVideoSizeChangedListener) {

        mIMediaPlayer.setOnVideoSizeChangedListener { p0, p1, p2, p3, p4 ->
            videoSizeChangedListener.onVideoSizeChanged(this,p1,p2)
        }
    }

    override fun pause() {

        if (mCurrentState == PlayerStatus.STATE_PLAYING) {
            mIMediaPlayer.pause()
            mCurrentState = PlayerStatus.STATE_PAUSED
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_PAUSED")
        }
        if (mCurrentState == PlayerStatus.STATE_BUFFERING_PLAYING) {
            mIMediaPlayer.pause()
            mCurrentState = PlayerStatus.STATE_BUFFERING_PAUSED
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_BUFFERING_PAUSED")
        }
    }

    override fun resume() {

        if (mCurrentState == PlayerStatus.STATE_PAUSED) {
            mIMediaPlayer.start()
            mCurrentState = PlayerStatus.STATE_PLAYING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_PLAYING")
        } else if (mCurrentState == PlayerStatus.STATE_BUFFERING_PAUSED) {
            mIMediaPlayer.start()
            mCurrentState = PlayerStatus.STATE_BUFFERING_PLAYING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("STATE_BUFFERING_PLAYING")
        } else if (mCurrentState == PlayerStatus.STATE_COMPLETED || mCurrentState == PlayerStatus.STATE_ERROR) {
            mIMediaPlayer.reset()
            openMedia()
        } else {
            LogUtil.d("VideoPlayer在mCurrentState == " + mCurrentState + "时不能调用restart()方法.")
        }
    }

    override fun seekTo(pos: Int) {
        mIMediaPlayer.seekTo(pos.toLong())
    }

    override fun getDuration(): Long {
        return mIMediaPlayer.duration.toLong()
    }

    override fun getBufferPercentage(): Int {
        return mBufferPercentage
    }

    override fun getCurrentPosition(): Long {
        return mIMediaPlayer.currentPosition.toLong()
    }

    override fun releasePlayer() {
        super.releasePlayer()
        mIMediaPlayer.release()
    }

    private val startCall = {
        reqestFouces()
        isUsePreLoad = false
        mIMediaPlayer.start()
        // 从上次的保存位置播放
        if (continueFromLastPosition) {
            val savedPlayPosition = getLastPosition()
            if (savedPlayPosition > 0) {
                mIMediaPlayer.seekTo(savedPlayPosition.toLong())
            }
        }
    }
    private val mOnPreparedListener = IMediaPlayer.OnPreparedListener { mp ->
        if (isUsePreLoad) {
            mCurrentState = PlayerStatus.STATE_PRELOADED_WAITING
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("onPrepared ——> STATE_PREPARED   wait noticePreLoading")
        } else {
            mCurrentState = PlayerStatus.STATE_PREPARED
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            LogUtil.d("onPrepared ——> STATE_PREPARED")
            startCall.invoke()
        }
    }


    private val mOnCompletionListener = IMediaPlayer.OnCompletionListener {
        mCurrentState = PlayerStatus.STATE_COMPLETED
        mPlayerStatusListener.onPlayStateChanged(mCurrentState)
        savePotion()
        LogUtil.d("onCompletion ——> STATE_COMPLETED")

    }


    private val mOnErrorListener = object : IMediaPlayer.OnErrorListener {
        override fun onError(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
            mCurrentState = PlayerStatus.STATE_ERROR
            mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            return true
        }
    }

    private val mOnInfoListener = object : IMediaPlayer.OnInfoListener {
        override fun onInfo(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                if (mIMediaPlayer.isPlaying) {
                    mCurrentState = PlayerStatus.STATE_PLAYING
                    mPlayerStatusListener.onPlayStateChanged(mCurrentState)
                    LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING")
                } else {
                    LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：视频暂停中 但是切换旋转回调了播放")
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // IMediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == PlayerStatus.STATE_PAUSED || mCurrentState == PlayerStatus.STATE_BUFFERING_PAUSED) {
                    mCurrentState = PlayerStatus.STATE_BUFFERING_PAUSED
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED")
                } else {
                    mCurrentState = PlayerStatus.STATE_BUFFERING_PLAYING
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING")
                }
                mPlayerStatusListener.onPlayStateChanged(mCurrentState)
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，IMediaPlayer恢复播放/暂停
                if (mCurrentState == PlayerStatus.STATE_BUFFERING_PLAYING) {
                    mCurrentState = PlayerStatus.STATE_PLAYING
                    mPlayerStatusListener.onPlayStateChanged(mCurrentState)
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING")
                }
                if (mCurrentState == PlayerStatus.STATE_BUFFERING_PAUSED) {
                    mCurrentState = PlayerStatus.STATE_PAUSED
                    mPlayerStatusListener.onPlayStateChanged(mCurrentState)
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED")
                }
            }
//            else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
//                // 视频旋转了extra度，需要恢复
//                if (mTextureView != null) {
//                    mTextureView.setRotation(extra)
//                    LogUtil.d("视频旋转角度：$extra")
//                }
//            }
//
            else if (what == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                LogUtil.d("视频不能seekTo，为直播视频")
            } else {
                LogUtil.d("onInfo ——> what：$what")
            }
            return true
        }
    }

    private val mOnBufferingUpdateListener =
        IMediaPlayer.OnBufferingUpdateListener { mp, percent -> mBufferPercentage = percent }

    override fun isPlaying(): Boolean {
        return mIMediaPlayer.isPlaying
    }

}