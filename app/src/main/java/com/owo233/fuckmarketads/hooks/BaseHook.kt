package com.owo233.fuckmarketads.hooks

abstract class BaseHook {
    abstract fun init()
    abstract val name: String
    var isInit: Boolean = false
}
