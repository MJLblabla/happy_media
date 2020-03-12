package com.pince.happy_media

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hapi.player.video.contronller.DefaultController
import kotlinx.android.synthetic.main.activity_full.*

class FullActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full)
        mHappyVideoPlayer.addController(DefaultController(this))
        mHappyVideoPlayer.setUp(Uri.parse("https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209105011F0zPoYzHry.mp4"), null, true)
        lifecycle.addObserver(mHappyVideoPlayer)
    }



}
