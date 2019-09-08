package com.hapi.player.video

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import android.view.View
import com.hapi.player.PlayerStatus.MODE_FULL_SCREEN
import com.hapi.player.utils.LogUtil
import com.hapi.player.utils.setScale

/**
 *
 */
internal class HappyTextureView : TextureView {


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

}
