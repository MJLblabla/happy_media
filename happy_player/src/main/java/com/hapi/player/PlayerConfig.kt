package com.hapi.player

import android.content.Context

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

    internal var cacheContext:Context?=null




    fun setLoop(loop:Boolean):PlayerConfig{
        return this
    }

    fun setFromLastPosition(fromLastPosition: Boolean):PlayerConfig{
        this.isFromLastPosition = fromLastPosition
        return this
    }

    fun setUseCache(cacheContext:Context?):PlayerConfig{
        if(cacheContext==null){
            isUseCache = false
        }else{
            isUseCache = true
            this.cacheContext = cacheContext
        }
        return this
    }
}