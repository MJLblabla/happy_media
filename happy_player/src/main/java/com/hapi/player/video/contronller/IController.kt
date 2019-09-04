package com.hapi.player.video.contronller

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.hapi.player.PlayerStatusListener
import com.hapi.player.video.IVideoPlayer

interface IController : PlayerStatusListener {



    fun getView(): View
    fun attach(player: IVideoPlayer){}
    fun detach()
    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    fun reset()
}