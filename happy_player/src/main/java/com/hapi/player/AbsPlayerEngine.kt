package com.hapi.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import com.hapi.player.cache.HttpProxyCacheManager

abstract class AbsPlayerEngine : IPlayer {


    private var listeners = ArrayList<PlayerStatusListener>()

    private var originUri: Uri? = null

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

    final override fun getCurrentUrl(): Uri? {
        return originUri
    }




    final override fun startPlay(uir: Uri, headers: Map<String, String>?, loop: Boolean, fromLastPosition: Boolean) {
        originUri = uir
        playAfterDealUrl(uir, headers, loop, fromLastPosition)
    }

    abstract fun playAfterDealUrl(uir: Uri, headers: Map<String, String>?, loop: Boolean, fromLastPosition: Boolean)

    final override fun startPlayWithCache(
        uir: Uri,
        context: Context,
        headers: Map<String, String>?,
        loop: Boolean,
        fromLastPosition: Boolean
    ) {

        val str = uir.path
           val proxyUrl = if (str.startsWith("http")) {
                val proxy = HttpProxyCacheManager.getHttpProxyCacheManager().getProxy(context.applicationContext)
                val proxyUrl = proxy.getProxyUrl(str)
                Uri.parse(proxyUrl)
            } else {
               uir
            }
        originUri = proxyUrl
        playAfterDealUrl(proxyUrl!!, headers, loop, fromLastPosition)
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