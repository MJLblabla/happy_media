package com.hapi.player.video

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.hapi.player.*
import com.hapi.player.PlayerStatus.*
import com.hapi.player.utils.LogUtil
import com.hapi.player.utils.PalyerUtil
import com.hapi.player.video.contronller.IController
import com.hapi.player.video.floating.Floating
import com.hapi.player.video.floating.TinyFloatView

class HappyVideoPlayer : FrameLayout, IVideoPlayer, TextureView.SurfaceTextureListener {

    /**
     * wrap content 高度下的 宽高比
     */
    companion object {
        private const val DEFAULT_HEIGHT_RATIO = (36F / 64)
    }

    /*
     wrap content 高度下的 宽高比
     */
    private var defaultHeightRatio = DEFAULT_HEIGHT_RATIO
    /**
     * 获取到视频宽高后的　宽高比
     */
    private var videoHeightRatio = DEFAULT_HEIGHT_RATIO

    private lateinit var mContainer: FrameLayout
    private lateinit var mFloating: Floating
    private lateinit var mTextureView: HappyTextureView
    private lateinit var mSurface: Surface
    private lateinit var mPlayerEngine: IPlayerEngine
    private var mSurfaceTexture: SurfaceTexture? = null


    private var mController: IController? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        attrs?.let {
            var typedArray = context?.obtainStyledAttributes(attrs, R.styleable.happyVideo)
            typedArray?.apply {
                defaultHeightRatio = getFloat(R.styleable.happyVideo_defaultHeightRatio, DEFAULT_HEIGHT_RATIO)
            }
        }
        init()
    }

    private var mCurrentMode = MODE_NORMAL


    private fun init() {
        mContainer = TinyFloatView(context)
        (  mContainer as TinyFloatView ).parentWindType = {mCurrentMode}
        mContainer.setBackgroundColor(Color.BLACK)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.addView(mContainer, params)
        mFloating = Floating(PalyerUtil.scanForActivity(context))
        mTextureView =
            LayoutInflater.from(context).inflate(R.layout.happytextureview, this, false) as HappyTextureView
        mTextureView.surfaceTextureListener = this

        mContainer.removeView(mTextureView)
        mContainer.addView(mTextureView, 0)
        mPlayerEngine = NativeEngine(context)
        mPlayerEngine.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener)

        VideoPlayerManager.instance().currentVideoPlayer = this
    }



    override fun addController(controller: IController) {
        mController = controller
        controller.attach(this)
        mContainer.addView(controller.getView())
        setListener(controller,true)

    }


    override fun startPlay(uir: Uri, headers: Map<String, String>?, position: Int, loop: Boolean, cache: Boolean) {
        mController?.reset()
        mPlayerEngine.startPlay(uir, headers, position, loop, cache)

    }

    override fun startPlayFromLastPosion(uir: Uri, headers: Map<String, String>?, loop: Boolean, cache: Boolean) {
        mController?.reset()
        mPlayerEngine.startPlayFromLastPosion(uir, headers, loop, cache)

    }



    override fun setListener(lister: PlayerStatusListener, add: Boolean) {
        mPlayerEngine.setListener(lister, add)
    }


    override fun getPlayerWindowStatus(): Int {
        return mCurrentMode
    }

    override fun enterFullScreen() {


        if (mCurrentMode == MODE_FULL_SCREEN) {
            return
        }

        // 隐藏ActionBar、状态栏，并横屏
        PalyerUtil.hideActionBar(context)
        PalyerUtil.scanForActivity(context).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val contentView = PalyerUtil.scanForActivity(context)
            .findViewById(android.R.id.content) as ViewGroup
        if (mCurrentMode == MODE_TINY_WINDOW) {
            contentView.removeView(mContainer)
        } else {
            this.removeView(mContainer)
        }
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        contentView.addView(mContainer, params)
        mCurrentMode = MODE_FULL_SCREEN
        mPlayerEngine.mPlayerStatusListener.onPlayModeChanged(mCurrentMode)
        LogUtil.d("MODE_FULL_SCREEN")
    }

    override fun exitFullScreen(): Boolean {

        if (mCurrentMode == MODE_FULL_SCREEN) {
            PalyerUtil.showActionBar(context)
            PalyerUtil.scanForActivity(context).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            val contentView = PalyerUtil.scanForActivity(context)
                .findViewById(android.R.id.content) as ViewGroup
            contentView.removeView(mContainer)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.addView(mContainer, params)

            mCurrentMode = MODE_NORMAL
            mPlayerEngine.mPlayerStatusListener.onPlayModeChanged(mCurrentMode)
            LogUtil.d("MODE_NORMAL")
            return true
        }
        return false

    }

    override fun enterTinyWindow() {


        if (mCurrentMode == MODE_TINY_WINDOW) {
            return
        }
        enterTinyWindowSmooth(false)

    }

    private fun enterTinyWindowSmooth(flag: Boolean) {
        if (mCurrentMode == MODE_TINY_WINDOW) {
            return
        }

        if (flag) {
            mFloating.start(videoHeightRatio,mContainer, object : Floating.OnFloatingDelegate {
                override fun onAninmationEnd() {
                    mCurrentMode = MODE_TINY_WINDOW
                    mPlayerEngine.mPlayerStatusListener.onPlayModeChanged(mCurrentMode)
                    LogUtil.d("MODE_TINY_WINDOW")
                }

                override fun onAnchorRectReach() {
                    this@HappyVideoPlayer.removeView(mContainer)
                }
            })
        } else {
            this.removeView(mContainer)
            val contentView = PalyerUtil.scanForActivity(context)
                .findViewById(Window.ID_ANDROID_CONTENT) as ViewGroup

            // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
            val w =        (PalyerUtil.getScreenWidth(context) * 0.6f).toInt()
            val params = FrameLayout.LayoutParams(
                w ,
                (w * videoHeightRatio ).toInt()
            )
            params.gravity = Gravity.BOTTOM or Gravity.END
            params.rightMargin = PalyerUtil.dp2px(context, 8f)
            params.bottomMargin = PalyerUtil.dp2px(context, 8f)

            contentView.addView(mContainer, params)

            mCurrentMode = MODE_TINY_WINDOW
            mPlayerEngine.mPlayerStatusListener.onPlayModeChanged(mCurrentMode)
            LogUtil.d("MODE_TINY_WINDOW")
        }
    }


    override fun exitTinyWindow(): Boolean {
        if (mCurrentMode == MODE_TINY_WINDOW) {
            mFloating.clear()

            val contentView = PalyerUtil.scanForActivity(context)
                .findViewById(android.R.id.content) as ViewGroup
            contentView.removeView(mContainer)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.addView(mContainer, params)
            mCurrentMode = MODE_NORMAL
            mPlayerEngine.mPlayerStatusListener.onPlayModeChanged(mCurrentMode)
            LogUtil.d("MODE_NORMAL")
            return true
        }
        return false
    }


    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        return mSurfaceTexture == null
    }


    private val mOnVideoSizeChangedListener = MediaPlayer.OnVideoSizeChangedListener { mp, width, height ->
        if(width>0&&height>0){
            videoHeightRatio=height.toFloat()/width
            mTextureView.adaptVideoSize(width, height)
        }
        LogUtil.d("onVideoSizeChanged ——> width：$width， height：$height")
    }


    override fun pause() {
        mPlayerEngine.pause()
    }

    override fun resume() {
        mPlayerEngine.resume()
    }

    override fun seekTo(pos: Int) {
        mPlayerEngine.seekTo(pos)
    }

    override fun setVolume(volume: Int) {
        mPlayerEngine.setVolume(volume)
    }

    override fun getCurrentPlayStatus(): Int {
        return mPlayerEngine.getCurrentPlayStatus()
    }

    override fun getMaxVolume(): Int {

        return mPlayerEngine.getMaxVolume()
    }

    override fun getVolume(): Int {
        return mPlayerEngine.getVolume()
    }

    override fun getDuration(): Long {
        return mPlayerEngine.getDuration()
    }

    override fun getCurrentPosition(): Long {
        return mPlayerEngine.getCurrentPosition()
    }

    override fun releasePlayer() {
        mPlayerEngine.releasePlayer()
        mSurfaceTexture?.release()
        VideoPlayerManager.instance().releaseVideoPlayer()
        mController?.detach()
    }


    override fun onDetachedFromWindow() {
        releasePlayer()
        super.onDetachedFromWindow()
        LogUtil.d("onDetachedFromWindow")
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
        mSurfaceTexture = p0
        mSurface = Surface(mSurfaceTexture)
        mPlayerEngine.setSurface(mSurface)
    }

    override fun getBufferPercentage(): Int {
        return mPlayerEngine.getBufferPercentage()
    }


    override fun isFullScreen(): Boolean {
        return mCurrentMode == MODE_FULL_SCREEN
    }

    override fun isTinyWindow(): Boolean {
        return mCurrentMode == MODE_TINY_WINDOW
    }

    override fun isNormal(): Boolean {
        return mCurrentMode == MODE_NORMAL
    }

    override fun isIdle(): Boolean {

        return mPlayerEngine.isIdle()
    }

    override fun isPreparing(): Boolean {
        return mPlayerEngine.isPreparing()
    }

    override fun isPrepared(): Boolean {
        return mPlayerEngine.isPrepared()
    }

    override fun isBufferingPlaying(): Boolean {
        return mPlayerEngine.isBufferingPlaying()
    }

    override fun isBufferingPaused(): Boolean {
        return mPlayerEngine.isBufferingPaused()
    }

    override fun isPlaying(): Boolean {
        return mPlayerEngine.isPlaying()
    }

    override fun isPaused(): Boolean {
        return mPlayerEngine.isPaused()
    }

    override fun isError(): Boolean {
        return mPlayerEngine.isError()
    }

    override fun isCompleted(): Boolean {
        return mPlayerEngine.isCompleted()
    }

    override fun getCurrentUrl(): Uri? {
        return mPlayerEngine.getCurrentUrl()
    }




    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // 获取宽-测量规则的模式和大小
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        // 获取高-测量规则的模式和大小
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)


        // 高度为warpcontent 正常模式
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT || isNormal()) {
            val sizeH = widthSize * defaultHeightRatio
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(sizeH.toInt(), MeasureSpec.EXACTLY))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}