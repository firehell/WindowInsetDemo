package com.zz.windowinsetdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setContentView(R.layout.activity_main)
        var bottomContainer = findViewById<ConstraintLayout>(R.id.bottom_container)

        val rootView = window.decorView
            window.navigationBarColor = getColor(R.color.teal_700)

//        bottomContainer.postDelayed(object : Runnable {
//            override fun run() {
//                val params: ConstraintLayout.LayoutParams =
//                    bottomContainer.layoutParams as ConstraintLayout.LayoutParams
//                params.bottomMargin = 100
//                bottomContainer.layoutParams = params
//                Log.d("ceshi", "run: hhh")
//            }
//        }, 2000)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var everGivenInsetsToDecorView = false
        ViewCompat.setOnApplyWindowInsetsListener(
            rootView
        ) { v, insets ->
            val navigationBarsInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            val threshold = (20 * resources.displayMetrics.density).toInt()
            val isGesture = navigationBarsInset.bottom <= threshold.coerceAtLeast(44)
            Log.d("ceshi", "isGesture: $isGesture")
            Log.d("ceshi", "onApplyWindowInsets: " + navigationBarsInset.bottom)

            if (isGesture) {
//                bottomContainer.setPadding(
//                    bottomContainer.paddingLeft,
//                    bottomContainer.paddingTop,
//                    bottomContainer.paddingRight,
//                    bottomContainer.paddingBottom + navigationBarsInset.bottom
//                )
            }

            if (!isGesture) {
                ViewCompat.onApplyWindowInsets(rootView, insets)
                everGivenInsetsToDecorView = true
            } else if (isGesture && everGivenInsetsToDecorView) {
                ViewCompat.onApplyWindowInsets(
                    v, WindowInsetsCompat.Builder().setInsets(
                        WindowInsetsCompat.Type.navigationBars(), Insets.of(
                            navigationBarsInset.left,
                            navigationBarsInset.top,
                            navigationBarsInset.right,
                            0
                        )
                    ).build()
                )
            }

            insets
        }

        //确保一定会触发OnApplyWindowInsetsListener
        if (rootView.isAttachedToWindow) {
            rootView.requestApplyInsets()
        } else {
            rootView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.removeOnAttachStateChangeListener(this)
                    v.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(v: View) {

                }
            })
        }

        ViewCompat.setWindowInsetsAnimationCallback(
            rootView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                private var isImeVisible = false

                override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                    super.onPrepare(animation)
                    isImeVisible = ViewCompat.getRootWindowInsets(rootView)
                        ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val typesInset = insets.getInsets(WindowInsetsCompat.Type.ime())
                    val params: ConstraintLayout.LayoutParams =
                        bottomContainer.layoutParams as ConstraintLayout.LayoutParams
                    params.bottomMargin = typesInset.bottom
                    bottomContainer.layoutParams = params
                    if (!isImeVisible) {
                        Log.d("ceshi", "onProgress: " + typesInset.bottom)
                    } else {
                        Log.d("ceshi", "onProgress: " + isImeVisible)
                    }
                    return insets
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    super.onEnd(animation)
                    Log.d("ceshi", "OnEnd: " + isImeVisible)
                }
            })
    }
}