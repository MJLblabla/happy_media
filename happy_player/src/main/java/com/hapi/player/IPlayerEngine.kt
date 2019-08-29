package com.hapi.player

import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface

abstract class IPlayerEngine : IPlayer {


    private var listeners = ArrayList<PlayerStatusListener>()
    var mUrl: Uri? = null
        protected set

    val mPlayerStatusListener = object : PlayerStatusListener {
        override fun onPlayModeChanged(model: Int) {
            listeners.forEach {
                it.onPlayModeChanged(model)
            }
        }

        override fun onPlayStateChanged(status: Int) {
            listeners.forEach {
                it.onPlayStateChanged(status)
            }
        }
    }

    override fun getCurrentUrl(): Uri? {
        return mUrl
    }


    override fun setListener(lister: PlayerStatusListener, add: Boolean) {
        if (add) {
            listeners.add(lister)
        } else {
            listeners.remove(lister)
        }
    }

    abstract fun setSurface(surface: Surface)

    abstract fun setOnVideoSizeChangedListener(videoSizeChangedListener: MediaPlayer.OnVideoSizeChangedListener)
}