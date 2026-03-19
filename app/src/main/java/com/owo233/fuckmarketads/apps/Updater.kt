package com.owo233.fuckmarketads.apps

import com.owo233.fuckmarketads.hooks.updater.BypassOTACheck
import com.owo233.fuckmarketads.init.AppPackage
import com.owo233.fuckmarketads.init.AppRegister
import io.github.libxposed.api.XposedModuleInterface

object Updater : AppRegister() {

    override val packageName: String
        get() = AppPackage.UPDATER

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        autoInitHooks(
            param,
            BypassOTACheck
        )
    }
}
