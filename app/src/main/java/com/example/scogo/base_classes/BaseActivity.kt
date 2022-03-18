package com.example.scogo.base_classes

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.lang.reflect.Method

open class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        Log.i(TAG, "onWindowFocusChanged()")
        try {
            if (!hasFocus) {
                Log.d(TAG, "close status bar attempt")
                //option 1
                val currentApiVersion = Build.VERSION.SDK_INT
                val service = getSystemService("statusbar")
                val statusbarManager = Class
                    .forName("android.app.StatusBarManager")
                if (currentApiVersion <= 16) {
                    val collapse: Method = statusbarManager.getMethod("collapse")
                    collapse.isAccessible = true
                    collapse.invoke(service)
                } else {
                    val collapse: Method = statusbarManager.getMethod("collapsePanels")
                    collapse.isAccessible = true
                    collapse.invoke(service)
                }
                // option 2
                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                sendBroadcast(it)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        val activityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
    }

    override fun onResume() {
        super.onResume()
        window.setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Do nothing or catch the keys you want to block
        Log.d(TAG, "onKeyDown: ")
        return false
    }
}