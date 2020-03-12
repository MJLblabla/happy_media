package com.hapi.player


class PlayerConfig {


    /**
     * 循环播放
     */
    internal var loop = false

    /**
     * 从上一次的位置继续播放
     */
    internal var isFromLastPosition = false




    fun setLoop(loop: Boolean): PlayerConfig {
        this.loop = loop
        return this
    }

    fun setFromLastPosition(fromLastPosition: Boolean): PlayerConfig {
        this.isFromLastPosition = fromLastPosition
        return this
    }

}