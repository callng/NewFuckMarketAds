package com.owo233.fuckmarketads.hooks.test

import com.owo233.fuckmarketads.init.BaseHook

object Test : BaseHook() {

    override fun init() {}

    override val name: String
        get() = "测试Hook"
}
