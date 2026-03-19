package com.owo233.fuckmarketads

import com.owo233.fuckmarketads.apps.Market
import com.owo233.fuckmarketads.apps.Updater
import com.owo233.fuckmarketads.init.AppRegister
import com.owo233.fuckmarketads.init.EasyXposedInit

const val TAG = "FuckMarketAds"

class MainHook : EasyXposedInit() {

    override val registerApp: Set<AppRegister>
        get() = setOf(
            Market,
            Updater
        )
}
