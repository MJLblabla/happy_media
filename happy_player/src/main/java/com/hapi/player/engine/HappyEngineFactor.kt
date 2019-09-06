package com.hapi.player.engine

import android.content.Context
import com.hapi.player.AbsPlayerEngine
import com.hapi.player.IPlayer

object HappyEngineFactor {

    fun newPlayer(context: Context, engineType: EngineType): AbsPlayerEngine {
        return when (engineType) {
            EngineType.IJK_PLAYER -> {
                IjkEngine(context)
            }
            EngineType.MEDIA_PLAYER -> {
                NativeEngine(context)
            }
        }

    }

}