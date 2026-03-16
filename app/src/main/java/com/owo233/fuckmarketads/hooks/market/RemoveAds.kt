package com.owo233.fuckmarketads.hooks.market

import com.owo233.fuckmarketads.init.BaseHook
import com.owo233.fuckmarketads.util.getFieldValue
import com.owo233.fuckmarketads.util.setFieldValue
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.FieldFinder.`-Static`.fieldFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createAfterHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createAfterHooks
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHooks

object RemoveAds : BaseHook() {

    private val pageCollapseStateExpand by lazy {
        ClassUtil.loadClass($$"com.xiaomi.market.ui.UpdateListRvAdapter$PageCollapseState")
            .getFieldValue("Expand")
    }

    private val detailTypeV4 by lazy {
        ClassUtil.loadClass("com.xiaomi.market.business_ui.detail.DetailType")
            .getFieldValue("V4")
    }

    override val name: String
        get() = "移除一系列广告"

    override fun init() {
        // 下载队列页面 / 为你优先
        ClassUtil.loadClass("com.xiaomi.market.ui.DownloadListFragment")
            .methodFinder()
            .filterByName("parseRecommendGroupResult")
            .first()
            .createHook { returnConstant(null) }

        // 搜索建议
        ClassUtil.loadClass("com.xiaomi.market.business_ui.search.NativeSearchSugFragment")
            .methodFinder()
            .filterByName("getRequestParams")
            .first()
            .createAfterHook { param ->
                @Suppress("UNCHECKED_CAST")
                param.result = (param.result as Map<String, Any>).toMutableMap().apply {
                    this["adFlag"] = 0
                }
            }

        // 搜索页面
        ClassUtil.loadClass("com.xiaomi.market.business_ui.search.NativeSearchGuideFragment").apply {
            methodFinder()
                .filterByName("parseResponseData")
                .first()
                .createAfterHook { param ->
                    // com.xiaomi.market.common.component.componentbeans.SearchHistoryComponent
                    @Suppress("UNCHECKED_CAST")
                    param.result = (param.result as List<Any>).filter { component ->
                        component.javaClass.name.contains("SearchHistoryComponent")
                    }
                }

            methodFinder()
                .filterByName("isLoadMoreEndGone")
                .first()
                .createHook { returnConstant(true) }
        }

        // 搜索结果页面
        // 会导致某些APP无法被搜索到, 比如"小米商城"
        ClassUtil.loadClass("com.xiaomi.market.business_ui.search.NativeSearchResultFragment")
            .methodFinder()
            .filterByName("parseResponseData")
            .first()
            .createAfterHook { param ->
                // com.xiaomi.market.common.component.componentbeans.ListAppComponent
                @Suppress("UNCHECKED_CAST")
                param.result = (param.result as List<Any>).filter { component ->
                    component.javaClass.name.contains("ListAppComponent")
                }
            }

        // 应用升级页面
        ClassUtil.loadClass("com.xiaomi.market.ui.UpdateListRvAdapter").apply {
            methodFinder()
                .filterByName("generateRecommendGroupItems")
                .first()
                .createHook { returnConstant(null) }

            constructorFinder().toList()
                .createAfterHooks { param ->
                    fieldFinder()
                        .filterByName("forceExpanded")
                        .first()
                        .set(param.thisObject, true)

                    fieldFinder()
                        .filterByName("foldButtonVisible")
                        .first()
                        .set(param.thisObject, false)

                    fieldFinder()
                        .filterByName("pageCollapseState")
                        .first()
                        .set(param.thisObject, pageCollapseStateExpand)
                }
        }

        // 应用详情页
        ClassUtil.loadClass("com.xiaomi.market.common.network.retrofit.response.bean.AppDetailV3").apply {
            methodFinder()
                .filter {
                    name in setOf(
                        "isBrowserMarketAdOff",
                        "isBrowserSourceFileAdOff"
                    )
                }.toList().createHooks { returnConstant(true) }

            methodFinder()
                .filter {
                    name in setOf(
                        "needShowAds",
                        "needShowAdsWithSourceFile",
                        "isInternalAd",
                        "showRecommend",
                        "showTopBanner",
                        "showTopVideo",
                        "isSourceFileShowAdStyle",
                        "getShowOpenScreenAd"
                    )
                }.toList().createHooks { returnConstant(false) }
        }

        // 开屏广告
        ClassUtil.loadClass("com.xiaomi.market.ui.splash.DetailSplashAdManager").apply {
            methodFinder()
                .filter {
                    name in setOf(
                        "canRequestSplashAd",
                        "isRequestApprovedByServer",
                        "isOpenFromMsa",
                        "isRequesting"
                    )
                }.toList().createHooks { returnConstant(false) }

            methodFinder()
                .filterByName("tryToRequestSplashAd")
                .first()
                .createHook { returnConstant(null) }
        }

        // 底层
        ClassUtil.loadClass("com.xiaomi.market.ui.splash.SplashManager").apply {
            methodFinder()
                .filter {
                    name in setOf(
                        "canShowSplash",
                        "needShowSplash",
                        "needRequestFocusVideo",
                        "isPassiveSplashAd"
                    )
                }.toList().createHooks { returnConstant(false) }

            methodFinder()
                .filter {
                    name in setOf(
                        "tryAdSplash",
                        "trySplashWhenApplicationForeground",
                        "preLoadSplashCover",
                        "shownSplashCoverIfNeed"
                    )
                }.toList().createHooks { returnConstant(null) }
        }

        // 主界面核心
        ClassUtil.loadClass("com.xiaomi.market.business_ui.main.MarketTabActivity")
            .methodFinder()
            .filter {
                name in setOf(
                    "tryShowRecallReCommend",
                    "tryShowRecommend",
                    "trySplash",
                    "fetchSearchHotList"
                )
            }.toList().createHooks { returnConstant(null) }

        // 应用详情
        ClassUtil.loadClass("com.xiaomi.market.ui.detail.BaseDetailActivity")
            .methodFinder()
            .filterByName("initParams")
            .first()
            .createAfterHook { param ->
                param.thisObject.setFieldValue("detailType", detailTypeV4)
            }
    }
}
