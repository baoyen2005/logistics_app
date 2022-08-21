package com.example.baseapp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.baseapp.dialog.BaseDialog
import com.example.baseapp.dialog.LoadingDialog

abstract class BaseActivity<BindingType : ViewDataBinding, ViewModelType : BaseViewModel> :
    AppCompatActivity() {
    @get:LayoutRes
    protected abstract val layoutId: Int
    private var _binding: BindingType? = null
    val binding: BindingType get() = _binding!!

    private lateinit var viewModel: ViewModelType
    abstract fun getVM(): ViewModelType

    abstract fun showHideProgress(isShow: Boolean)
    protected abstract fun onReady(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutId)
        _binding?.lifecycleOwner = this
        setContentView(binding.root)
        viewModel = getVM()
    }
    fun showLoading() {
        LoadingDialog.getInstance(this)?.show()
    }

    fun hiddenLoading() {
        LoadingDialog.getInstance(this)?.hidden()
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

    private fun showAlertDialog(message: String) {
        BaseDialog(this)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}