package com.hapi.player.video

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class TextureContainer : FrameLayout {



    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

//
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        return false
//    }
}