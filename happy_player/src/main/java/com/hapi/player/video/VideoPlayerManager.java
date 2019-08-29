package com.hapi.player.video;

/**
 * 视频播放器管理器.
 */
public class VideoPlayerManager {

    private IVideoPlayer mHappyVideoPlayer;

    private VideoPlayerManager() {
    }

    private static VideoPlayerManager sInstance;

    public static synchronized VideoPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new VideoPlayerManager();
        }
        return sInstance;
    }

    public IVideoPlayer getCurrentVideoPlayer() {
        return mHappyVideoPlayer;
    }

    public void setCurrentVideoPlayer(IVideoPlayer happyVideoPlayer) {
        if (mHappyVideoPlayer != happyVideoPlayer) {
            releaseVideoPlayer();
            mHappyVideoPlayer = happyVideoPlayer;
        }
    }

    public void suspendVideoPlayer() {
        if (mHappyVideoPlayer != null && (mHappyVideoPlayer.isPlaying() || mHappyVideoPlayer.isBufferingPlaying())) {
            mHappyVideoPlayer.pause();
        }
    }

    public void resumeVideoPlayer() {
        if (mHappyVideoPlayer != null && (mHappyVideoPlayer.isPaused() || mHappyVideoPlayer.isBufferingPaused())) {
            mHappyVideoPlayer.resume();
        }
    }

    public void releaseVideoPlayer() {
        if (mHappyVideoPlayer != null) {
            mHappyVideoPlayer = null;
        }
    }

    public boolean onBackPressd() {
        if (mHappyVideoPlayer != null) {
            if (mHappyVideoPlayer.isFullScreen()) {
                return mHappyVideoPlayer.exitFullScreen();
            } else if (mHappyVideoPlayer.isTinyWindow()) {
                return mHappyVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
