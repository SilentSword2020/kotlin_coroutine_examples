package com.example.app

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.MainCoroutineDispatcher as Main

class MainActivity2 : ScopedAppActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setup2(hello, fab)
        setup(hello, fab)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) return true
        return super.onOptionsItemSelected(item)
    }


    fun setup2(hello: TextView, fab: FloatingActionButton) {
        //在主线程中使用协程更新UI
        updateUIWithCoroutineInMainThread(hello, fab)

    }


    /**
     * 挂起函数：异步执行请求，在主线程显示结果数据
     */
    suspend fun showIOData(hello: TextView) {
        val deferred = async(Dispatchers.IO) {
            Log.e(TAG, "showIOData() current threadName: " + Thread.currentThread().name)

            delay(1000)
            // 实现
            "Countdown IOData ..."
        }
        withContext(Dispatchers.Main) {
            Log.e(TAG, "showIOData() withContext(Dispatchers.Main) current threadName: " + Thread.currentThread().name)

            val data = deferred.await()
            // 在 UI 中展示数据
            hello.text = data
        }
    }


    /**
     * 在主线程中使用协程更新UI
     *
     * 不会阻塞UI线程
     */
    private fun updateUIWithCoroutineInMainThread(hello: TextView, fab: FloatingActionButton) {
        // Activity 的 job 作为父结构时，这里将在 UI 上下文中被调用
        launch(Dispatchers.Main) {

            Log.e(TAG, "updateUIWithCoroutineInMainThread() current threadName: " + Thread.currentThread().name)
            showText(hello, 10)

            showIOData(hello)
        }

    }

    /**
     * 显示倒计时的数
     */
    private suspend fun showText(hello: TextView, count: Int) {
        // 在主线程中启动协程
        for (i in count downTo 1) { // 从 10 到 1 的倒计时
            hello.text = "Countdown $i ..." // 更新文本
            delay(500) // 等待半秒钟
        }
        hello.text = "Done!"
    }


    /**
     * 下面是对耗时方法的处理
     *
     * 把耗时方法的调用放到子线程中执行
     */

    suspend fun fib(x: Int): Int = withContext(Dispatchers.Default) {
        fibBlocking(x)
    }

    fun fibBlocking(x: Int): Int =
            if (x <= 1) x else fibBlocking(x - 1) + fibBlocking(x - 2)


    /**
     * CoroutineStart.UNDISPATCHED: 不进行派发，直接执行任务代码，直到遇到挂起的代码
     *
     * 下面的日志
     *
     *Before launch
     *Inside coroutine
     *After launch
     *After delay
     *
     * 如果不加CoroutineStart.UNDISPATCHED
     *
     * 日志：
     *
     *Before launch
     *After launch
     *Inside coroutine
     *After delay
     *
     */
    fun setup(hello: TextView, fab: FloatingActionButton) {
        fab.setOnClickListener {
            println("Before launch")
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.UNDISPATCHED) {
                // <--- 通知这次改变
                println("Inside coroutine")
                delay(100) // <--- 这里是协程挂起的地方
                println("After delay")
            }
            println("After launch")
        }
    }
}









