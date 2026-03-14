package com.owo233.fuckmarketads

import com.owo233.fuckmarketads.hooks.BaseHook
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam

const val TARGET_PACKAGE = "com.xiaomi.market"

class MainHook(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {

    init {
        EzXposed.initXposedModule(base)
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        if (param.packageName != TARGET_PACKAGE) return

        EzXposed.initOnPackageLoaded(param)
        initHooks()
    }

    private fun initHooks(vararg  hooks: BaseHook) {
        for (hook in hooks) {
            try {
                if (hook.isInit) continue
                hook.init()
                hook.isInit = true
            } catch (e: Exception) {
                log("Failed to init hook: ${hook.javaClass.simpleName}", e)
            }
        }
    }
}
