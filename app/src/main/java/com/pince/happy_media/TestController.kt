package com.pince.happy_media

import android.view.View
import com.hapi.player.video.IVideoPlayer
import com.hapi.player.video.contronller.IController

class TestController : IController {





    override fun getView(): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun attach(player: IVideoPlayer) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun detach() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reset() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayStateChanged(status: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayModeChanged(model: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}