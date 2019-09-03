package com.hapi.player.utils

import android.net.Uri

/**
 * @author athoucai
 * @date 2019/2/18
 */
class MediaParams constructor(var path: String? = null,
                              var width:Int,
                              var height:Int,
                              var duration: Long = -1,
                              var mimeType: String? = null) {
}