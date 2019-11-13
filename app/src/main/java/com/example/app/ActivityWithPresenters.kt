package com.example.app

import android.util.Log
import kotlinx.coroutines.*

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
    suspend fun loadData(uiScope: CoroutineScope) = uiScope.launch(exceptionHandler) {
        // 在 UI 作用域中调用
    }
}

/**
 * 作为 ActivityWithPresenters 的作用域的扩展
 *
 * 通过委托给构造函数传入的scope来扩展
 */
class ScopedPresenter(scope: CoroutineScope) : CoroutineScope by scope {
    fun loadData() = launchWithExceptionHandler {
        // 作为 ActivityWithPresenters 的作用域的扩展
    }
}

/**
 * 扩展函数
 */
suspend fun CoroutineScope.launchInIO() = launch(Dispatchers.IO + exceptionHandler) {
    // 在调用者的作用域中启动，但使用 IO 调度器
}

/**
 * 扩展函数(加上异常处理)
 */
fun CoroutineScope.launchWithExceptionHandler(start: CoroutineStart = CoroutineStart.DEFAULT,
                                              block: suspend CoroutineScope.() -> Unit): Job {
    return launch(coroutineContext + exceptionHandler, start) {
        // 在调用者的作用域中启动
        block.invoke(this)
    }
}

/**
 * 协程异常的处理
 */
val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    Log.e(TAG, "Caught $exception")
}