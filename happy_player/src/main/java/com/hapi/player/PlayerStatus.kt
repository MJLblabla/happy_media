package com.hapi.player


/**
 * 播放错误
 */
val STATE_ERROR = -1
/**
 * 播放未开始
 */
val STATE_IDLE = 0
/**
 * 播放准备中
 */
val STATE_PREPARING = 1
/**
 * 播放准备就绪
 */
val STATE_PREPARED = 2
/**
 * 正在播放
 */
val STATE_PLAYING = 3
/**
 * 暂停播放
 */
val STATE_PAUSED = 4
/**
 * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
 */
val STATE_BUFFERING_PLAYING = 5
/**
 * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
 */
val STATE_BUFFERING_PAUSED = 6
/**
 * 播放完成
 */
val STATE_COMPLETED = 7