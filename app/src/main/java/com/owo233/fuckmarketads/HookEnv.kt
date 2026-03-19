package com.owo233.fuckmarketads

import io.github.libxposed.api.XposedModule

internal object HookEnv {

    lateinit var base: XposedModule
        private set

    lateinit var hostClassLoader: ClassLoader
        private set

    fun setBase(base: XposedModule) {
        this.base = base
    }

    fun setHostClassLoader(cl: ClassLoader) {
        this.hostClassLoader = cl
    }
}
