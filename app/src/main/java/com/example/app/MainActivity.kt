package com.example.app

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.MainCoroutineDispatcher as Main

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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
        if (id == R.id.action_settings) {
            startActivity(Intent(this, MainActivity2::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

val TAG: String = "coroutine"

fun setup(hello: TextView, fab: FloatingActionButton) {
    //在主线程中使用协程更新UI
    //updateUIWithCoroutineInMainThread(hello)

    //在主线程中使用协程更新UI，取消协程
    //updateUIWithCancelCoroutineJob(hello, fab)

    //使用actor:最多一个并发任务：同一时间，只有一个协程任务在执行,确保被启动的协程的数量没有无限制的增长
    //updateUIWithCoroutineActor(fab, hello)

    //使用actor + capacity = Channel.CONFLATED: 只处理最新的事件，总共执行2次
    updateUIWithCoroutineActor2(fab, hello)
}

/**
 * 使用actor + capacity = Channel.CONFLATED
 *
 *只处理最新的事件
 *当动画运行中时如果这个圆形按钮被点击，动画将在结束后重新运行。
 * TODO 仅仅一次。只会重复一次
 * 在倒数进行中时，重复点击将被 合并 ，只有最近的事件才会被处理
 *
 * 确保被启动的协程的数量没有无限制的增长
 */
private fun updateUIWithCoroutineActor2(fab: FloatingActionButton, hello: TextView) {
    fab.onClick2 {
        Log.e(TAG, "updateUIWithCoroutineActor2() current threadName: " + Thread.currentThread().name + ", time:" + SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
        showText(hello, 10)
    }
}


/**
 * view的扩展函数
 */
fun View.onClick2(action: suspend (View) -> Unit) {
    Log.e(TAG, "View.onClick2() ")

    // 启动一个 actor
    val eventActor = GlobalScope.actor<View>(Dispatchers.Main, capacity = Channel.CONFLATED) {
        for (event in channel) action(event)
    }
    // 设置一个监听器来启用 actor
    setOnClickListener {
        eventActor.offer(it)
    }
}

/**
 * 使用actor
 *
 * 最多一个并发任务：同一时间，只有一个协程任务在执行
 *
 * 会忽略之后的任务
 *
 * 确保被启动的协程的数量没有无限制的增长
 */
private fun updateUIWithCoroutineActor(fab: FloatingActionButton, hello: TextView) {
    fab.onClick {
        Log.e(TAG, "updateUIWithCoroutineActor() current threadName: " + Thread.currentThread().name)
        showText(hello, 10)
    }
}


/**
 * view的扩展函数
 */
fun View.onClick(action: suspend (View) -> Unit) {
    Log.e(TAG, "View.onClick() ")
    // 启动一个 actor
    val eventActor = GlobalScope.actor<View>(Dispatchers.Main) {
        for (event in channel) action(event)
    }
    // 设置一个监听器来启用 actor
    setOnClickListener {
        eventActor.offer(it)
    }
}


/**
 * 在主线程中使用协程更新UI
 *
 * 并取消协程
 *
 * 不会阻塞UI线程
 */
private fun updateUIWithCancelCoroutineJob(hello: TextView, fab: FloatingActionButton) {
    val job = GlobalScope.launch(Dispatchers.Main) {

        Log.e(TAG, "updateUIWithCancelCoroutineJob() current threadName: " + Thread.currentThread().name)

        // 在主线程中启动协程
        for (i in 100 downTo 1) { // 从 10 到 1 的倒计时
            hello.text = "Countdown $i ..." // 更新文本
            delay(500) // 等待半秒钟
        }
        hello.text = "Done!"
    }
    // 在点击时取消协程
    fab.setOnClickListener {
        Log.e(TAG, "updateUIWithCancelCoroutineJob() cancel job")
        job.cancel()

        /**
         * TODO
         * Job.cancel 的调用是完全线程安全和非阻塞的。
         * 它仅仅是示意协程取消它的任务，而不会去等待任务事实上的终止。
         * 它可以在任何地方被调用。
         * 在已经取消或已完成的协程上调用它不会做任何事情。
         **/
    }
}


/**
 * 在主线程中使用协程更新UI
 *
 * 不会阻塞UI线程
 */
private fun updateUIWithCoroutineInMainThread(hello: TextView) {
    GlobalScope.launch(Dispatchers.Main) {

        Log.e(TAG, "updateUIWithCoroutineInMainThread() current threadName: " + Thread.currentThread().name)
        showText(hello, 20)
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
