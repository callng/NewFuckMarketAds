package com.owo233.fuckmarketads.hooks.market

import com.owo233.fuckmarketads.HookEnv
import com.owo233.fuckmarketads.init.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.FieldFinder.`-Static`.fieldFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil

object TabFilter : BaseHook() {

    private val keepPrefixes by lazy {
        /**
         * native_market_mine // 我的
         * native_market_home // 主页
         * native_market_video // 视频号，在4.99.0或之前的某个版本开始没了
         * native_market_agent // shit 智能体
         * native_app_assemble // shit 应用号
         * native_market_game // 游戏
         * native_market_rank // 榜单
         */
        setOf("native_market_home", "native_market_mine")
    }

    override val name: String
        get() = "过滤底部TAB标签"

    override fun init() {
        ClassUtil.loadClass("com.xiaomi.market.model.TabInfo").also {
            val tabField = it.fieldFinder()
                .filterByName("tag")
                .filterByType(String::class.java)
                .first()

            it.methodFinder()
                .filterByName("fromJSON")
                .filterByParamCount(1)
                .first()
                .also { method ->
                    HookEnv.base.hook(method).intercept { chain ->
                        val result = chain.proceed()
                        val list = (result as List<*>).toMutableList()
                        list.removeAll { item ->
                            val tag = tabField.get(item) as String
                            keepPrefixes.none { prefix -> tag.startsWith(prefix) }
                        }
                        return@intercept list
                    }
                }
        }
    }
}
