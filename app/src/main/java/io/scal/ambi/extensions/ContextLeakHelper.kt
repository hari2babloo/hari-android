package io.scal.ambi.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.UserManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import timber.log.Timber
import java.lang.ref.WeakReference

object ContextLeakHelper {

    @SuppressLint("HandlerLeak")
    private val CLEAN_UP_HANDLER = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)


            val activityRef = msg.obj as WeakReference<*>
            val activity = activityRef.get() as? Activity
            if (null != activity) {
                cleanLeak(activity, false)
            }
        }
    }

    fun cleanLeak(activity: Activity) {
        cleanLeak(activity, true)
    }

    fun cleanLeak(fragment: Fragment) {
        cleanLeakRecyclerView(fragment.view)
        cleanInputMethodLeak(fragment.activity)
    }

    fun cleanLeak(fragment: android.support.v4.app.Fragment) {
        cleanLeakRecyclerView(fragment.view)
        cleanInputMethodLeak(fragment.activity!!)
    }

    private fun cleanLeakRecyclerView(view: View?) {
        if (view is ViewGroup) {
            var i = 0
            val length = view.childCount
            while (i < length) {
                cleanLeakRecyclerView(view.getChildAt(i))
                ++i
            }
        }
        if (view is RecyclerView) {
            view.adapter = null
        }
    }

    private fun cleanLeak(activity: Activity, postHandler: Boolean) {
        cleanClipboardUiManagerLeak(activity)
        cleanResourceLeak(activity)
        cleanLGLeak(activity)
        cleanDisplayManagerLeak(activity)
        cleanPhoneLayoutInflaterLeak(activity)
        cleanSamsungAudioLeak(activity)
        cleanInputMethodLeak(activity)

        if (postHandler) {
            CLEAN_UP_HANDLER.sendMessageDelayed(Message.obtain(CLEAN_UP_HANDLER, 0, WeakReference(activity)), 1000)

            System.gc()
        } else {
            cleanOuterContextLeak(activity)
        }
    }

    private fun cleanSamsungAudioLeak(activity: Activity) {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioManagerClass = audioManager.javaClass

        try {
            val field = audioManagerClass.getDeclaredField("mContext_static")
            field.isAccessible = true
            if (field.get(audioManager) == activity) {
                field.set(audioManager, activity.applicationContext)
            }
        } catch (ignore: Exception) {
            // pass
        }
    }

    private fun cleanPhoneLayoutInflaterLeak(activity: Activity) {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val inflaterClass = inflater.javaClass

        try {
            val field = inflaterClass.superclass.getDeclaredField("mContext")
            field.isAccessible = true
            if (field.get(inflater) == activity) {
                field.set(inflater, activity.applicationContext)
            }
        } catch (ignored: Exception) {
            // pass
        }
    }

    private fun cleanDisplayManagerLeak(activity: Activity) {
        try {
            val displayManager = activity.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val contextField = displayManager.javaClass.getDeclaredField("mContext")
            contextField.isAccessible = true
            if (contextField.get(displayManager) is Activity) {
                contextField.set(displayManager, activity.applicationContext)
            }
        } catch (e: Exception) {
            Timber.e(e, "cleanDisplayManagerLeak")
        }
    }

    /**
     * Fix strange issue with ClipBoardUiManager fix from https://github.com/square/leakcanary/issues/133
     *
     * @param activity context
     */
    @SuppressLint("PrivateApi")
    private fun cleanClipboardUiManagerLeak(activity: Activity) {
        try {
            val method = Class.forName("android.sec.clipboard.ClipboardUIManager")
                .getDeclaredMethod("getInstance", Context::class.java)
            method.isAccessible = true
            method.invoke(null, activity.application)
        } catch (ignored: Exception) {
            // pass
        }
    }

    private fun cleanOuterContextLeak(activity: Activity) {
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT) {
            try {
                val userManager = activity.getSystemService(Context.USER_SERVICE) as UserManager

                val userManagerContextField = userManager.javaClass.getDeclaredField("mContext")
                userManagerContextField.isAccessible = true
                val contextField = userManagerContextField.get(userManager)

                val outerContextField = contextField.javaClass.getDeclaredField("mOuterContext")
                outerContextField.isAccessible = true
                val currentField = outerContextField.get(contextField)

                if (currentField == activity) {
                    outerContextField.set(contextField, activity.applicationContext)
                }
            } catch (ignored: Exception) {
                // pass
            }
        }
    }

    private fun cleanResourceLeak(activity: Activity) {
        try {
            val applicationClass = Class.forName("android.app.Application")
            val loadedApkField = applicationClass.getDeclaredField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField.get(activity.application)
            val resourcesField = loadedApk.javaClass.getDeclaredField("mResources")
            resourcesField.isAccessible = true
            val resourcesObject = resourcesField.get(loadedApk)
            val parallelLoadingContextField = resourcesObject.javaClass.getDeclaredField("mParallelLoadingContext")
            parallelLoadingContextField.isAccessible = true
            val parallelLoadingContext = parallelLoadingContextField.get(resourcesObject)
            val outerContextField = parallelLoadingContext.javaClass.getDeclaredField("mOuterContext")
            outerContextField.isAccessible = true
            val currentField = outerContextField.get(parallelLoadingContext)
            if (currentField == activity) {
                outerContextField.set(parallelLoadingContext, activity.applicationContext)
            }
        } catch (ignored: Exception) {
            // pass
        }
    }

    private fun cleanLGLeak(activity: Activity) {
        try {
            val objectClass = Class.forName("com.lge.loader.LGContextHelper")
            val field = objectClass.getDeclaredField("mLGContext")
            field.isAccessible = true
            val lgContextInstance = field.get(null)
            val contextField = lgContextInstance.javaClass.getDeclaredField("mContext")
            contextField.isAccessible = true
            contextField.set(lgContextInstance, activity.applicationContext)
        } catch (ignored: Exception) {
            // pass
        }
    }

    private fun cleanInputMethodLeak(activity: Activity) {
        try {
            val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            val currentViewField = InputMethodManager::class.java.getDeclaredField("mServedView")
            val nextViewField = InputMethodManager::class.java.getDeclaredField("mNextServedView")

            currentViewField.isAccessible = true
            nextViewField.isAccessible = true

            currentViewField.set(manager, null)
            nextViewField.set(manager, null)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }
}