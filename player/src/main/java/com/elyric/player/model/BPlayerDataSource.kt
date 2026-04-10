package com.elyric.player.model

/**
 * 音视频数据类型
 */
class BPlayerDataSource {
    val urlMap: LinkedHashMap<String, String> = LinkedHashMap();
    fun setSource(name:String,url:String){
        urlMap[name] = url
    }
}