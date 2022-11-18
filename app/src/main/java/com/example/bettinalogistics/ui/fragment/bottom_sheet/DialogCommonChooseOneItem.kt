package com.example.bettinalogistics.ui.fragment.bottom_sheet

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.DialogServiceContactBinding
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.ui.common_adapter.CommonChooseOneItemAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogCommonChooseOneItem(
    val dataList: List<CommonEntity>,
    val listener: (CommonEntity) -> Unit
) : BottomSheetDialogFragment() {

    val binding : DialogServiceContactBinding by lazy {
        DialogServiceContactBinding.inflate(layoutInflater)
    }

    private var mTitle = ""

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
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CommonChooseOneItemAdapter() {
            listener.invoke(it)
            dismiss()
        }
        binding.rcvContent.isVisible = !dataList.isNullOrEmpty()
        binding.tvNoData.isVisible = dataList.isNullOrEmpty()
        adapter.setData(dataList)

        binding.rcvContent.adapter = adapter

        binding.imgClose.setSafeOnClickListener {
            dismiss()
        }

        binding.edtSearch.onTextChange = {
            adapter.filter.filter(it)
        }

        initTitle()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
            dialog.setOnShowListener { d ->
                bottomSheetBehavior = (d as BottomSheetDialog).behavior
                bottomSheetBehavior?.apply {
                    setupFullHeight(d)
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
                        }
                    }
                    isDraggable = false
                    addBottomSheetCallback(bottomSheetCallback!!)
                }

        }

        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun initTitle() {
        binding.tvTitle.text = mTitle
    }

    fun setTitle(title: String): DialogCommonChooseOneItem{
        mTitle = title
        return this@DialogCommonChooseOneItem
    }
}