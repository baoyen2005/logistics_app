package com.example.baseapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.baseapp.di.Common
import com.example.baseapp.dialog.ConfirmDialog
import com.example.baseapp.dialog.LoadingDialog
import org.koin.android.ext.android.inject

abstract class BaseFragment :
    Fragment() {
    val confirm by inject<ConfirmDialog>()

    @get:LayoutRes
    protected abstract val layoutId: Int
    protected open val binding: ViewBinding? = null
    abstract val viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (binding != null) {
            requireActivity().setContentView(binding?.root)
        }
        Common.currentActivity = requireActivity()
        initView()
        initListener()
        observerData()
    }

    abstract fun initView()

    abstract fun initListener()

    abstract fun observerData()

    fun showLoading() {
        LoadingDialog.getInstance(requireContext())?.show()
    }

    fun hiddenLoading() {
        LoadingDialog.getInstance(requireContext())?.hidden()
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Common.currentActivity = requireActivity()
    }
}