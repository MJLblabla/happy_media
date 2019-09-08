package com.hapi.player.video;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.util.AttributeSet;

/**
 * 绑定activity生命周期的播放器
 */
public class LifecycleVideoView extends HappyVideoPlayer implements LifecycleObserver {

    /**
     * onpause暂停了播放
     */
    private Boolean isNeedReplay = false;

    public LifecycleVideoView(Context context) {
        super(context);
    }

    public LifecycleVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)

    public void onResume() {
        if (isNeedReplay && isPaused()) {
            resume();
            isNeedReplay = false;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        releasePlayer();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause(LifecycleOwner lifecycleOwner) {
        // disconnect if connected
        if (isPlaying()) {
            pause();
            isNeedReplay = true;
        }
    }

}
