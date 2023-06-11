package com.tong.streamdqexp.model

data class WikiClickStream @JvmOverloads constructor(
    var prev: String? = null,
    var curr: String? = null,
    var type: String? = null,
    var count: String? = null,
)