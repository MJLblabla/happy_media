package com.pince.happy_media

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hapi.player.video.contronller.DefaultController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mHappyVideoPlayer.addController(DefaultController(this))
        mHappyVideoPlayer.setUp(
            Uri.parse("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"),
            null,
            true
        )


        button.setOnClickListener {
            val i = Intent(this, FullActivity::class.java)
            startActivity(i)
        }

    }


    override fun onBackPressed() {

        if (mHappyVideoPlayer.isTinyWindow()) {
            mHappyVideoPlayer.exitTinyWindow()
            return
        }

        if (mHappyVideoPlayer.isFullScreen()) {
            mHappyVideoPlayer.exitFullScreen()
            return
        }
        return super.onBackPressed()
    }


}
