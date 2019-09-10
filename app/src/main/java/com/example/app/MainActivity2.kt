package com.example.app

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainCoroutineDispatcher as Main

class MainActivity2 : ScopedAppActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setup2(hello, fab)
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
        updateUIWithCoroutineInMainThread(hello)

    }

    /**
     * 在主线程中使用协程更新UI
     *
     * 不会阻塞UI线程
     */
    private fun updateUIWithCoroutineInMainThread(hello: TextView) {
        launch(Dispatchers.Main) {

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
}









