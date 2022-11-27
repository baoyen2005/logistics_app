package com.example.baseapp

import android.R
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.baseapp.di.Common
import com.example.baseapp.dialog.BaseDialog
import com.example.baseapp.dialog.ConfirmDialog
import com.example.baseapp.dialog.LoadingDialog
import com.example.baseapp.dialog.LoadingDialogVn
import org.koin.android.ext.android.inject


abstract class BaseActivity :
    AppCompatActivity() {
    val confirm by inject<ConfirmDialog>()
    val loading by inject<LoadingDialogVn>()
    protected open val binding: ViewBinding? = null
    abstract val viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (binding != null) {
            setContentView(binding?.root)
        }
        Common.currentActivity = this
        initView()
        initListener()
        observeData()
    }
    abstract fun initView()
    abstract fun initListener()
    abstract fun observeData()

    fun showLoading() {
        loading.show()
    }

    fun hiddenLoading() {
        loading.dismiss()
    }

    fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Common.currentActivity = this
    }

    /**
     * Close SoftKeyboard when user click out of EditText
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun showAlertDialog(message: String) {
        BaseDialog(this)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}