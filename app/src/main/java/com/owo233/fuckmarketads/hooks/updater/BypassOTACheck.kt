package com.owo233.fuckmarketads.hooks.updater

import com.owo233.fuckmarketads.init.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createBeforeHook

object BypassOTACheck : BaseHook() {

    private const val FEATURE_SUPPORT_OTA_VALIDATE = "support_ota_validate"
    private const val FEATURE_SUPPORT_UPDATE_FROM_SDCARD = "support_update_from_sdcard"

    override val name: String
        get() = "禁用 OTA 验证"

    override fun init() {
        /**
         * public static boolean h1() {
         *         if (FeatureParser.hasFeature("support_ota_validate", 1)) {
         *             return FeatureParser.getBoolean("support_ota_validate", false);
         *         }
         *         return false;
         *     }
         */
        ClassUtil.loadClass("miui.util.FeatureParser").apply {
            methodFinder()
                .filterByName("hasFeature")
                .filterByParamCount(2)
                .filterByParamTypes(String::class.java, Int::class.javaPrimitiveType)
                .single()
                .createBeforeHook { param ->
                    when (param.args[0] as String) {
                        FEATURE_SUPPORT_OTA_VALIDATE -> param.result = false
                        FEATURE_SUPPORT_UPDATE_FROM_SDCARD -> param.result = true
                    }
                }

            /**
             * public static boolean g1() {
             *         if (FeatureParser.hasFeature("support_update_from_sdcard", 1)) {
             *             return FeatureParser.getBoolean("support_update_from_sdcard", false);
             *         }
             *         return false;
             *     }
             */
            methodFinder()
                .filterByName("getBoolean")
                .filterByParamCount(2)
                .filterByParamTypes(String::class.java, Boolean::class.javaPrimitiveType)
                .single()
                .createBeforeHook { param ->
                    when (param.args[0] as String) {
                        FEATURE_SUPPORT_UPDATE_FROM_SDCARD -> param.result = true
                    }
                }
        }
    }
}
