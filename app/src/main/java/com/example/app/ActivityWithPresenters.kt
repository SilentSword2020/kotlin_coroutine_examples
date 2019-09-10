package com.example.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ActivityWithPresenters : ScopedAppActivity() {
    fun init() {
        val presenter = Presenter()
        val presenter2 = ScopedPresenter(this)
    }
}

class Presenter {

    /**
     * CoroutineScope的扩展函数:来执行传入的函数参数
     */
    suspend fun loadData() = coroutineScope {
        // 外部 activity 的嵌套作用域
    }

    /**
     * 通过参数传入的uiScope来在 UI 作用域中调用
     */
    suspend fun loadData(uiScope: CoroutineScope) = uiScope.launch {
        // 在 UI 作用域中调用
    }
}

/**
 * 作为 ActivityWithPresenters 的作用域的扩展
 *
 * 通过委托给构造函数传入的scope来扩展
 */
class ScopedPresenter(scope: CoroutineScope) : CoroutineScope by scope {
    fun loadData() = launch {
        // 作为 ActivityWithPresenters 的作用域的扩展
    }
}

/**
 * 扩展函数
 */
suspend fun CoroutineScope.launchInIO() = launch(Dispatchers.IO) {
    // 在调用者的作用域中启动，但使用 IO 调度器
}