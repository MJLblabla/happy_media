package com.hapi.player


import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import com.hapi.player.cache.HttpProxyCacheManager

abstract class AbsPlayerEngine : IPlayer {

    private var listeners = ArrayList<PlayerStatusListener>()
    protected var mPlayerConfig = PlayerConfig()
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

    override fun startPlay(uir: Uri, headers: Map<String, String>?, preLoading: Boolean) {
        val str = uir.toString()
        val proxyUrl = if (str.startsWith("http") && mPlayerConfig.cacheContext != null && mPlayerConfig.isUseCache) {
            val proxy = HttpProxyCacheManager.getHttpProxyCacheManager()
                .getProxy(mPlayerConfig.cacheContext!!.applicationContext)
            val proxyUrl = proxy.getProxyUrl(str)
            Uri.parse(proxyUrl)
        } else {
            uir
        }
        originUri = proxyUrl
        playAfterDealUrl(proxyUrl!!, headers, preLoading)
    }


    override fun setPlayerConfig(playerConfig: PlayerConfig) {
        mPlayerConfig = playerConfig
        readPlayerConfig()
    }

    override fun getPlayerConfig(): PlayerConfig {
        return mPlayerConfig
    }


    override fun releasePlayer() {
        listeners.clear()
    }

    abstract fun readPlayerConfig()

    abstract fun playAfterDealUrl(uir: Uri, headers: Map<String, String>?, preLoading: Boolean)


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