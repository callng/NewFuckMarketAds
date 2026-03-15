package com.owo233.fuckmarketads

import com.owo233.fuckmarketads.apps.Market
import com.owo233.fuckmarketads.init.AppRegister
import com.owo233.fuckmarketads.init.EasyXposedInit
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam

const val TAG = "FuckMarketAds"

class MainHook(base: XposedInterface, param: ModuleLoadedParam) : EasyXposedInit(base, param) {

    override val registerApp: Set<AppRegister>
        get() = setOf(
            Market
        )
}
