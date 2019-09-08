package com.hapi.player

interface PlayerStatusListener {


    fun onPlayStateChanged(status:Int)

    fun onPlayModeChanged(model:Int)

}