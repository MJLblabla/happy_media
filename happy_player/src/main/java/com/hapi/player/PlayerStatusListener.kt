package com.hapi.player

interface PlayerStatusListener {




    companion object {

        /**
         * 普通模式
         */
        val MODE_NORMAL = 10
        /**
         * 全屏模式
         */
        val MODE_FULL_SCREEN = 11
        /**
         * 小窗口模式
         */
        val MODE_TINY_WINDOW = 12
    }




    fun onPlayStateChanged(status:Int)

    fun onPlayModeChanged(model:Int)

}