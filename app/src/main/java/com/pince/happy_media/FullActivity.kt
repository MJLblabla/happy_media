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
        mHappyVideoPlayer.setUp(Uri.parse("https://cdn.static.orzzhibo.com/20190936/d0c134694343d1a83044ffa84afc4972.mp4"), null, true)
    }



}
