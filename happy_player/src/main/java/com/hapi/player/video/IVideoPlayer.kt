package com.hapi.player.video

import android.net.Uri
import com.hapi.player.IPlayer
import com.hapi.player.video.contronller.IController

interface IVideoPlayer : IPlayer {



    fun setUp(uir: Uri, headers :Map<String, String> ?=null,cover:Uri, preLoading:Boolean = false)


    /**
     * 锁定屏幕
     * @return　成功失败
     */
    fun lockScreen(toLock:Boolean):Boolean

    fun isLock():Boolean

    /**
     * 设置背景
     */
    fun setCover(uir: Uri?)

    /**
     * 添加控制器
     */
    fun addController(controller: IController)
    /**
     * 窗口模式
     */
    fun getPlayerWindowStatus(): Int


    /**
     * 进入全屏模式
     */
    fun enterFullScreen()

    /**
     * 退出全屏模式
     *
     * @return true 退出
     */
    fun exitFullScreen(): Boolean

    /**
     * 进入小窗口模式
     */
    fun enterTinyWindow()

    /**
     * 退出小窗口模式
     *
     * @return true 退出小窗口
     */
    fun exitTinyWindow(): Boolean


     fun isFullScreen(): Boolean
     fun isTinyWindow(): Boolean
     fun isNormal(): Boolean


}