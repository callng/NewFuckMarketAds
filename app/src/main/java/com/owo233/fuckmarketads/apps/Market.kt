package com.owo233.fuckmarketads.apps

import com.owo233.fuckmarketads.hooks.market.HideSecurityView
import com.owo233.fuckmarketads.init.AppPackage
import com.owo233.fuckmarketads.init.AppRegister
import io.github.libxposed.api.XposedModuleInterface

object Market : AppRegister() {

    override val packageName: String get() = AppPackage.MARKET

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        autoInitHooks(
            param,
            HideSecurityView
        )
    }
}
