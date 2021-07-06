package com.sibyl.sasukehomeDominator.util

import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * @author Sasuke on 2021-06-12.
 */

fun RequestManager.loadNoCache(picPath: String) = this.load(picPath)
                                                                                         .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                                                                         // .skipMemoryCache(true) // 不使用内存缓存

fun RequestManager.loadNoCache(byteArray: ByteArray?) = this.load(byteArray)
                                                                                                 .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                                                                                 // .skipMemoryCache(true) // 不使用内存缓存