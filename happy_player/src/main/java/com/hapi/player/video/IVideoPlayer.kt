package com.hapi.player.video

import com.hapi.player.IPlayer

interface IVideoPlayer : IPlayer {


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