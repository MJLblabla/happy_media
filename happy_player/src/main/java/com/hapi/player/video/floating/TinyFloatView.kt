package com.hapi.player.video.floating

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.hapi.player.PlayerStatus.MODE_NORMAL
import com.hapi.player.PlayerStatus.MODE_TINY_WINDOW
import com.hapi.player.utils.LogUtil
import com.hapi.player.utils.PalyerUtil

class TinyFloatView : FrameLayout {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var startX = 0f
    private var startY = 0f

    var parentWindType: (() -> Int)? = null


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        LogUtil.d("onInterceptTouchEvent " + event?.action)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.rawX
                startY = event.rawY
                return super.onInterceptTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {

                val dx = event.rawX - startX
                val dy = event.rawY -startY
                LogUtil.d("updateWindowPos " + dx+"    "+dy)
                if (Math.abs(dx)>0 && Math.abs(dy)>0 &&parentWindType?.invoke() == MODE_TINY_WINDOW) {
                    return true
                }

            }
        }

        return super.onInterceptTouchEvent(event)

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {

                val x = event.rawX - startX
                val y = event.rawY - startY
                val screenWidth = PalyerUtil.getScreenWidth(context)
                val scrrenHeight = PalyerUtil.getScreenHeight(context)
                startX = event.rawX
                startY = event.rawY

                if (getX() + x < 0 || getX()  + x + width > screenWidth) {
                    return true
                }
                if (getY() + y < 0 || getY() + y + height > scrrenHeight) {
                    return true
                }

                updateWindowPos(x, y)
                return true
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.onTouchEvent(event)
    }


    private fun updateWindowPos(x: Float, y: Float) {
        if(x==0f && y==0f){
            return
        }

        translationX += x
        translationY +=y
//        val lp = layoutParams
//        if(lp is FrameLayout.LayoutParams){
//
//            lp.topMargin= (lp.topMargin+y).toInt()
//            lp.leftMargin = (lp.leftMargin+x).toInt()
//            layoutParams = lp
//            requestLayout()
//        }

    }


}