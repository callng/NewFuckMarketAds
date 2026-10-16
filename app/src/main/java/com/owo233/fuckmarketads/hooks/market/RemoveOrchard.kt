package com.owo233.fuckmarketads.hooks.market

import com.owo233.fuckmarketads.HookEnv
import com.owo233.fuckmarketads.init.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil

/**
 * 移除「果园 / 免费领水果」系列推广。
 *
 * 这几处 UI 看起来是三个独立广告，其实是同一场运营活动(果园)，共 4 个渲染点：
 *   1. 首页搜索栏左侧的果树「免费领」入口            -> EntranceView
 *   2. 「我的」页“应用升级”卡片的「送水果」角标 + 绿色果园皮肤 -> MineUpdateView
 *   3. “应用程序升级”页顶部的「种果树，领水果 / 去果园」横幅  -> OrchardGuideBannerView
 *   4. 应用详情页的果园入口图标                      -> DetailOrchardIconController
 *
 * 关键点：UpgradeGuideConfigManager.isOrchardExperiment() 不读任何实验号，
 * 而是直接看首页那个 entrance 的 deeplink 是否包含 "mimarket://orchard"：
 *
 *     EntranceConfig c = EntranceManager.INSTANCE.getEntranceConfig();
 *     return (c == null ? "" : c.getPageDeeplink()).contains(OrchardSdkHelper.ORCHARD_DEEPLINK);
 *
 * 然后 2/3 全部由它派生：
 *     OrchardEntranceManager.isOpen()               = isOrchardExperiment() && isEnabled() && ...
 *     UpdateListRvAdapter.shouldShowOrchardGuide()  = OrchardEntranceManager.isOpen() && ...
 *     MineUpdateView.needApplyOrchardUIState()      = isEnabled() && isOrchardExperiment()
 *
 * 所以把入口配置打成 null，首页入口自己就走 hide 分支，果园实验整体变 false。
 * 第 4 处走的是另一套 DetailEntryConfig，需要单独关。
 *
 * 备注：APK 里本来就有官方开关(设置 -> 果园入口 -> orchard_entrance_closed)，
 * 这里 hook 的正是官方认可的关闭点，属于安全改动。
 */
object RemoveOrchard : BaseHook() {

    override val name: String
        get() = "移除果园(免费领水果)推广"

    override fun init() {
        // 1) 根因：让 EntranceView 认为「服务端没有下发入口配置」
        //    -> EntranceView.bindView() 走 hide 分支，首页果树入口不显示
        //    -> UpgradeGuideConfigManager.isOrchardExperiment() 随之返回 false
        ClassUtil.loadClass("com.xiaomi.market.util.EntranceManager")
            .methodFinder()
            .filterByName("getEntranceConfig")
            .first()
            .also { HookEnv.base.hook(it).intercept { null } }

        // 2) 显式关掉果园实验开关：
        //    -> OrchardEntranceManager.isOpen() == false
        //       -> UpdateListRvAdapter.shouldShowOrchardGuide() == false
        //          （“应用程序升级”页不再插入果园横幅）
        //    -> MineUpdateView.needApplyOrchardUIState() == false
        //       （升级卡片恢复普通背景，不显示「送水果」角标与「去果园浇水」）
        //    同时 OrchardSdkHelper.fetchOrchardBannerData() 会被拦下，少一次网络请求。
        ClassUtil.loadClass("com.xiaomi.market.util.UpgradeGuideConfigManager")
            .methodFinder()
            .filterByName("isOrchardExperiment")
            .first()
            .also { HookEnv.base.hook(it).intercept { false } }

        // 3) 应用详情页的果园入口图标 —— 这条走的是独立的 DetailEntryConfig，
        //    不受上面两个开关影响，所以单独关掉。
        ClassUtil.loadClass("com.xiaomi.market.business_ui.detail.DetailOrchardIconController")
            .methodFinder()
            .filterByName("shouldShowEntry")
            .first()
            .also { HookEnv.base.hook(it).intercept { false } }
    }
}
