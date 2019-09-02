package com.hapi.player.video

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import com.hapi.player.PlayerStatus.MODE_FULL_SCREEN

import com.hapi.player.PlayerStatus.MODE_NORMAL

/**
 *
 */
internal class HappyTextureView : TextureView {

    private var videoHeight: Int = 0
    private var videoWidth: Int = 0

    private var centerCropError = 0f

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun adaptVideoSize(videoWidth: Int, videoHeight: Int) {
        if (this.videoWidth != videoWidth && this.videoHeight != videoHeight) {
            this.videoWidth = videoWidth
            this.videoHeight = videoHeight
            requestLayout()
        }
    }

    override fun setRotation(rotation: Float) {
        if (rotation != getRotation()) {
            super.setRotation(rotation)
            requestLayout()
        }
    }


    var parentWindType: (() -> Int)? = null

    fun setCenterCropError(centerCropError: Float) {
        this.centerCropError = centerCropError
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec

        val viewRotation = rotation

//        // 如果判断成立，则说明显示的TextureView和本身的位置是有90度的旋转的，所以需要交换宽高参数。
//        if (viewRotation == 90f || viewRotation == 270f) {
//            val tempMeasureSpec = widthMeasureSpec
//            widthMeasureSpec = heightMeasureSpec
//            heightMeasureSpec = tempMeasureSpec
//        }


        var width = View.getDefaultSize(videoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(videoHeight, heightMeasureSpec)

        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)

        //控件宽高

        if (videoWidth > 0 && videoHeight > 0) {


            val hightRatio = heightSpecSize / height.toFloat()
            val withRatio = widthSpecSize / width.toFloat()
            /**
             * 横瓶播放器　播放横屏视频　
             */

            if (((width > height && widthSpecSize > heightSpecSize)

                        || (width < height && widthSpecSize < heightSpecSize))

                && parentWindType?.invoke() != MODE_FULL_SCREEN

            ) {
                val max = if (hightRatio < withRatio) {
                    withRatio
                } else {
                    hightRatio
                }

                val tempW = (height * max - heightSpecSize) / heightSpecSize
                val tempH = (tempW * max - widthSpecSize) / widthMeasureSpec

                if (Math.abs(tempH) > centerCropError && Math.abs(tempW) > centerCropError) {

                    height = (height * max).toInt()
                    width = (width * max).toInt()
                }
            } else {


                if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                    // the size is fixed
                    width = widthSpecSize
                    height = heightSpecSize
                    // for compatibility, we adjust size based on aspect ratio
                    if (videoWidth * height < width * videoHeight) {
                        width = height * videoWidth / videoHeight
                    } else if (videoWidth * height > width * videoHeight) {
                        height = width * videoHeight / videoWidth
                    }
                } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                    // only the width is fixed, adjust the height to match aspect ratio if possible
                    width = widthSpecSize
                    height = width * videoHeight / videoWidth
                    if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                        // couldn't match aspect ratio within the constraints
                        height = heightSpecSize
                        width = height * videoWidth / videoHeight
                    }
                } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                    // only the height is fixed, adjust the width to match aspect ratio if possible
                    height = heightSpecSize
                    width = height * videoWidth / videoHeight
                    if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                        // couldn't match aspect ratio within the constraints
                        width = widthSpecSize
                        height = width * videoHeight / videoWidth
                    }
                } else {
                    // neither the width nor the height are fixed, try to use actual video size
                    width = videoWidth
                    height = videoHeight
                    if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                        // too tall, decrease both width and height
                        height = heightSpecSize
                        width = height * videoWidth / videoHeight
                    }
                    if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                        // too wide, decrease both width and height
                        width = widthSpecSize
                        height = width * videoHeight / videoWidth
                    }
                }
            }
        }

        setMeasuredDimension(width, height)
    }
}
