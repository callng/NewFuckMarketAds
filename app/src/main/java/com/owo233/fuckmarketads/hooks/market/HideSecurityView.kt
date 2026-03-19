package com.owo233.fuckmarketads.hooks.market

import android.view.View
import com.owo233.fuckmarketads.HookEnv
import com.owo233.fuckmarketads.init.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil

object HideSecurityView : BaseHook() {

    override fun init() {
        ClassUtil.loadClass(
            "com.xiaomi.market.business_ui.main.mine.app_security.MineAppSecurityView"
        ).apply {
            constructorFinder().forEach { ctor ->
                HookEnv.base.hook(ctor).intercept { chain ->
                    val result = chain.proceed()
                    (chain.thisObject as View).visibility = View.GONE
                    return@intercept result
                }
            }

            methodFinder().filterByName("checkSettingSwitch").first().also { method ->
                HookEnv.base.hook(method).intercept { false }
            }

            methodFinder().filterByName("checkShown").first().also { method ->
                HookEnv.base.hook(method).intercept { false }
            }
        }
    }

    override val name: String
        get() = "隐藏应用安全检查视图"
}
