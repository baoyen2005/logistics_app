package com.example.baseapp

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    abstract val binding: ViewBinding
    abstract val isFocusFullHeight: Boolean
    open val isExpanded: Boolean = true
    open val isDraggable: Boolean = false

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyleBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        observerLiveData()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupHeight(bottomSheetDialog)

            bottomSheetBehavior = bottomSheetDialog.behavior
            bottomSheetBehavior?.apply {
                bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float
                    ) {

                    }

                    override fun onStateChanged(
                        bottomSheet: View,
                        newState: Int
                    ) {
                        onStageChanged(newState)
                    }
                }
                addBottomSheetCallback(bottomSheetCallback!!)
            }
        }
        return dialog
    }

    private fun setupHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        if(isFocusFullHeight) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams
        }
        if (isExpanded) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        behavior.isDraggable = this@BaseBottomSheetFragment.isDraggable

    }

    override fun dismiss() {
        bottomSheetCallback?.let {
            bottomSheetBehavior?.removeBottomSheetCallback(it)
            bottomSheetBehavior = null
        }
        super.dismiss()
    }


    open fun onStageChanged(newState: Int) {}

    abstract fun initView()

    abstract fun initListener()

    open fun observerLiveData() {}
}