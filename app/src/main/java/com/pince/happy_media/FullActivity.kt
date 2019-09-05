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
        mHappyVideoPlayer.setUp(Uri.parse("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"), null, true)
    }
}
