package com.owo233.fuckmarketads.hooks.market

import android.view.View
import com.owo233.fuckmarketads.init.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHooks

object HideSecurityView : BaseHook() {

    override fun init() {
        ClassUtil.loadClass(
            "com.xiaomi.market.business_ui.main.mine.app_security.MineAppSecurityView"
        ).apply {
            constructorFinder().toList().createHooks {
                after { (it.thisObject as View).visibility = View.GONE }
            }

            methodFinder().filterByName("checkSettingSwitch").first().createHook {
                returnConstant(false)
            }

            methodFinder().filterByName("checkShown").first().createHook {
                returnConstant(false)
            }
        }
    }

    override val name: String
        get() = "隐藏应用安全检查视图"
}
