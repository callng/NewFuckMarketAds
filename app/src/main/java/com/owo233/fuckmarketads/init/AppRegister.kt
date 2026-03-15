package com.owo233.fuckmarketads.init

import android.util.Log
import com.owo233.fuckmarketads.HookEnv
import com.owo233.fuckmarketads.TAG
import io.github.libxposed.api.XposedModuleInterface

abstract class AppRegister : XposedModuleInterface {

    abstract val packageName: String

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) = Unit

    protected fun autoInitHooks(
        param: XposedModuleInterface.PackageLoadedParam,
        vararg hooks: BaseHook
    ) {
        hooks.also {
            HookEnv.base.log(Log.INFO, TAG, "Try to Hook: $packageName", null)
        }.forEach {
            runCatching {
                if (it.isInit) return@forEach
                it.setParam(param)
                it.init()
                it.isInit = true
            }.onFailure { e ->
                HookEnv.base.log(Log.ERROR, TAG, "Failed to Hook: ${it.name}", e)
            }
        }
    }
}
