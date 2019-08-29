package com.pince.happy_media

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        b2.setOnClickListener {
            mHappyVideoPlayer.startPlay(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
        }
        btnStart.setOnClickListener {
            mHappyVideoPlayer.startPlay(Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4"))
        }

        btnPause.setOnClickListener {
            mHappyVideoPlayer.pause()
        }

        btnResume.setOnClickListener {
            mHappyVideoPlayer.resume()
        }

    }


}
