package com.owo233.fuckmarketads

import io.github.libxposed.api.XposedInterface

internal object HookEnv {

    lateinit var base: XposedInterface
        private set

    fun setBase(base: XposedInterface) {
        this.base = base
    }
}
