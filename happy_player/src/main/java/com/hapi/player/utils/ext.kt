package com.hapi.player.utils

import java.math.BigDecimal


fun Double.setScale(scale:Int):Double{
    val b =  BigDecimal(this);
    val  d = b.setScale(scale, BigDecimal.ROUND_HALF_UP).toDouble()
    return d
}