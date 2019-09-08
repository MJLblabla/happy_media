package com.hapi.player.audio

import android.content.Context
import com.hapi.player.IPlayer
import com.hapi.player.engine.NativeEngine

object HappyPlayerHelper {


    fun newPlayer(context: Context):IPlayer{
        return NativeEngine(context)
    }

}