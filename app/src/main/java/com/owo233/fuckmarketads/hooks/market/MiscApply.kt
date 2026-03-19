package com.owo233.fuckmarketads.hooks.market

import com.owo233.fuckmarketads.HookEnv
import com.owo233.fuckmarketads.init.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil

object MiscApply : BaseHook() {

    override val name: String
        get() = "部分细节处理"

    override fun init() {
        ClassUtil.loadClass("com.xiaomi.market.model.LocalAppInfo").apply {
            methodFinder()
                .filterByName("isInstalledByMarket")
                .first()
                .also { HookEnv.base.hook(it).intercept { true } }

            methodFinder()
                .filterByName("getInstallerSourceInUpdateInterface")
                .first()
                .also { HookEnv.base.hook(it).intercept { "0" } }
        }

        ClassUtil.loadClass("com.xiaomi.market.model.AppInfo").apply {
            methodFinder()
                .filterByName("maybeStolenApp") // 显示被篡改签名的APP
                .first()
                .also { HookEnv.base.hook(it).intercept { false } }

            methodFinder()
                .filterByName("isSignatureInconsistent") // 禁用签名不一致检查
                .first()
                .also { HookEnv.base.hook(it).intercept { false } }

            methodFinder()
                .filterByName("isDownloadDisable") // 允许下载被禁用的APP
                .first()
                .also { HookEnv.base.hook(it).intercept { false } }

            methodFinder()
                .filterByName("isInconsistentUpdate") // 禁用更新不一致检查
                .first()
                .also { HookEnv.base.hook(it).intercept { false } }

            methodFinder()
                .filterByName("shouldHideAutoUpdate") // 显示被隐藏的自动更新
                .first()
                .also { HookEnv.base.hook(it).intercept { false } }
        }
    }
}
