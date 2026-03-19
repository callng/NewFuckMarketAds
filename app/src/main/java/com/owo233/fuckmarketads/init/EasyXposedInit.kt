package com.owo233.fuckmarketads.init

import android.util.Log
import com.owo233.fuckmarketads.HookEnv
import com.owo233.fuckmarketads.TAG
import io.github.kyuubiran.ezxhelper.core.EzXReflection
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

abstract class EasyXposedInit : XposedModule() {

    abstract val registerApp: Set<AppRegister>

    override fun onModuleLoaded(param: ModuleLoadedParam) {
        HookEnv.setBase(this)
    }

    override fun onPackageReady(param: PackageReadyParam) {
        if (!param.isFirstPackage) return

        registerApp.forEach { app ->
            if (app.packageName == param.packageName) {
                HookEnv.setHostClassLoader(param.classLoader)
                EzXReflection.init(param.classLoader)
                runCatching {
                    app.onPackageReady(param)
                }.onFailure { e ->
                    log(Log.ERROR, TAG, "Failed call handleLoadPackage, package: ${app.packageName}", e)
                }
            }
        }
    }
}
