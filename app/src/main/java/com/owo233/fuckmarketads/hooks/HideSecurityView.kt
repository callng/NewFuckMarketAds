package com.owo233.fuckmarketads.hooks

import android.view.View
import com.owo233.fuckmarketads.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook

object HideSecurityView : BaseHook() {

    override fun init() {
        loadClass(
            "com.xiaomi.market.business_ui.main.mine.app_security.MineAppSecurityView"
        ).apply {
            constructorFinder().forEach { constructor ->
                constructor.createHook {
                    after { (it.thisObject as View).visibility = View.GONE }
                }
            }

            methodFinder().filterByName("checkShown").first().createHook {
                before { it.result = false }
            }
        }
    }

    override val name: String get() = "隐藏应用安全检查视图"
}
