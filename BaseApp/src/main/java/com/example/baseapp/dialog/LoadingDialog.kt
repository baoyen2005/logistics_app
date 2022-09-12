package com.example.baseapp.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import com.example.baseapp.R


class LoadingDialog(context: Context?) : Dialog(context!!) {
    var progress: ProgressBar? = null
    private var handler: Handler? = null
    private var run: Runnable? = null
    private var dialog: Dialog? = null
    private fun init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
        handler = Handler()
        dialog = this
        run = Runnable {
            try {
                if (dialog != null && isShowing) {
                    dismiss()
                }
            } catch (e: Exception) {
                ////LogVnp.Shape1(Shape1);
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_notice)
        progress = findViewById<View>(R.id.progressLoading) as ProgressBar?
    }

    override fun onStop() {
        super.onStop()
    }

    fun setCancel(isCancel: Boolean) {
        setCancelable(isCancel)
        setCanceledOnTouchOutside(isCancel)
    }

    override fun show() {
        super.show()
        handler!!.postDelayed(run!!, 90000)
    }

    override fun dismiss() {
        try {
            super.dismiss()
            handler!!.removeCallbacks(run!!)
        } catch (ex: Exception) {
            ////LogVnp.Shape1(ex);
        }
    }

    init {
        init()
    }
}