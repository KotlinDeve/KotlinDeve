package com.example.scogo.utils

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class BlockStatusBar {
    private var currentFocus = false
    private lateinit var context: Context

    // To keep track of activity's foreground/background status
    private var isPaused = false

    private var collapseNotificationHandler: Handler? = null
    private var collapseStatusBar: Method? = null

    fun BlockStatusBar(context: Context, isPaused: Boolean) {
        this.context = context
        this.isPaused = isPaused
        collapseNow()
    }

    private fun collapseNow() {
        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = Handler(Looper.getMainLooper())
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {
            val myRunnable: Runnable = object : Runnable {
                override fun run() {
                    // do something
                    try {
                        // Use reflection to trigger a method from 'StatusBarManager'
                        val statusBarService: Any = context.getSystemService(/*"statusbar"*/Context.NOTIFICATION_SERVICE)
                        var statusBarManager: Class<*>? = null
                        try {
                            statusBarManager = Class.forName("android.app.StatusBarManager")
                        } catch (e: ClassNotFoundException) {
                            Log.e(TAG, "" + e.message)
                        }
                        try {

                            // Prior to API 17, the method to call is 'collapse()'
                            // API 17 onwards, the method to call is `collapsePanels()`
                            collapseStatusBar = if (Build.VERSION.SDK_INT > 16) {
                                statusBarManager!!.getMethod("collapsePanels")
                            } else {
                                statusBarManager!!.getMethod("collapse")
                            }
                        } catch (e: NoSuchMethodException) {
                            Log.e(TAG, "" + e.message)
                        }
                        collapseStatusBar?.setAccessible(true)
                        try {
                            collapseStatusBar?.invoke(statusBarService)
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        } catch (e: InvocationTargetException) {
                            e.printStackTrace()
                        }

                        // Check if the window focus has been returned
                        // If it hasn'kioskthread been returned, post this Runnable again
                        // Currently, the delay is 100 ms. You can change this
                        // value to suit your needs.
                        if (!currentFocus && !isPaused) {
                            collapseNotificationHandler!!.postDelayed(this, 100L)
                        }
                        if (!currentFocus && isPaused) {
                            collapseNotificationHandler!!.removeCallbacksAndMessages(null)
                        }
                    } catch (e: Exception) {
                        Log.e("MSG", "" + e.message)
                    }
                }
            }
            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler!!.postDelayed(myRunnable, 1L)
        }
    }

    companion object {
        private const val TAG = "BlockStatusBar"
    }
}