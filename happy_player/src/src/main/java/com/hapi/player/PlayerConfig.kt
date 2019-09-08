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

    /**
     * 是否使用　边播边缓存
     */
    internal var isUseCache = false


    fun setLoop(loop: Boolean): PlayerConfig {
        this.loop = loop
        return this
    }

    fun setFromLastPosition(fromLastPosition: Boolean): PlayerConfig {
        this.isFromLastPosition = fromLastPosition
        return this
    }

    fun setUseCache(isUseCache: Boolean): PlayerConfig {
        this.isUseCache = isUseCache
        return this
    }
}