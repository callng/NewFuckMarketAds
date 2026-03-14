package com.owo233.fuckmarketads

abstract class BaseHook {
    abstract fun init()
    abstract val name: String
    var isInit: Boolean = false
}