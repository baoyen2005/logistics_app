package com.example.baseapp

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.baseapp.dialog.BaseDialog

abstract class BaseFragment<BindingType : ViewDataBinding, ViewModelType : BaseViewModel> :
    Fragment() {
    @get:LayoutRes
    protected abstract val layoutId: Int
    private var _binding: BindingType? = null
    val binding: BindingType
        get() = _binding
            ?: throw IllegalStateException("Cannot access view after view destroyed or before view creation")

    private lateinit var viewModel: ViewModelType
    abstract fun getVM(): ViewModelType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DataBindingUtil.bind(view)
        _binding?.lifecycleOwner = viewLifecycleOwner

        if(savedInstanceState == null){
            initView(savedInstanceState)

            setOnClick()

            bindingStateView()

            bindingAction()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState != null){
            initView(savedInstanceState)

            setOnClick()

            bindingStateView()

            bindingAction()
        }
    }

    open fun setOnClick() {

    }

    open fun initView(savedInstanceState: Bundle?) {

    }

    open fun bindingStateView() {

    }

    open fun bindingAction() {

    }

    override fun onDestroyView() {
        _binding?.unbind()
        _binding = null
        super.onDestroyView()
    }

    fun showHideLoading(isShow: Boolean) {
        if (activity != null && activity is BaseActivity) {
            if (isShow) {
                (activity as BaseActivity?)!!.showLoading()
            } else {
                (activity as BaseActivity?)!!.hiddenLoading()
            }
        }
    }

    fun showAlertDialog(message: String, onPositive: BaseDialog.OnDialogListener? = null) {
        BaseDialog(requireContext())
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok, onPositive)
            .show()
    }

    fun showAlertDialog(@StringRes message: Int) {
        BaseDialog(requireContext())
            .setMessage(message.toString())
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    fun showConfirmDialog(
        @StringRes message: Int,
        onPositive: BaseDialog.OnDialogListener? = null,
        onNegative: BaseDialog.OnDialogListener? = null,
    ) {
        BaseDialog(requireContext())
            .setMessage(message.toString())
            .setPositiveButton(R.string.ok, onPositive)
            .setNegativeButton(R.string.cancel, onNegative)
            .show()
    }
}