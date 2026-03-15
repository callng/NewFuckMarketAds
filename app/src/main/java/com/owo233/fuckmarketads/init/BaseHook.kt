package com.owo233.fuckmarketads.init

import io.github.libxposed.api.XposedModuleInterface

abstract class BaseHook {

    private lateinit var param: XposedModuleInterface.PackageLoadedParam

    var isInit: Boolean = false

    abstract val name: String

    abstract fun init()

    fun setParam(param: XposedModuleInterface.PackageLoadedParam) {
        this.param = param
    }

    protected fun getParam(): XposedModuleInterface.PackageLoadedParam {
        if (!this::param.isInitialized) {
            throw IllegalStateException("param should be initialized.")
        }
        return param
    }

    protected fun getClassLoader(): ClassLoader {
        return getParam().classLoader
    }
}
