package com.owo233.fuckmarketads.init

import android.util.Log
import com.owo233.fuckmarketads.HookEnv
import com.owo233.fuckmarketads.TAG
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam


abstract class EasyXposedInit(
    base: XposedInterface,
    param: ModuleLoadedParam
) : XposedModule(base, param) {

    private lateinit var packageParam: PackageLoadedParam

    abstract val registerApp: Set<AppRegister>

    init {
        EzXposed.initXposedModule(base)
        HookEnv.setBase(base)
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        packageParam = param

        registerApp.forEach { app ->
            if (app.packageName == param.packageName) {
                EzXposed.initOnPackageLoaded(param)
                runCatching {
                    app.onPackageLoaded(param)
                }.onFailure { e ->
                    log(Log.ERROR, TAG, "Failed call handleLoadPackage, package: ${app.packageName}", e)
                }
            }
        }
    }
}
