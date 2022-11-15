package com.vnpay.merchant.ui.fragment.bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentConfirmBottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmBottomSheetFragment : BottomSheetDialogFragment() {

    val binding: FragmentConfirmBottomDialogBinding by lazy {
        FragmentConfirmBottomDialogBinding.inflate(layoutInflater)
    }

    private var iconResource: Int = 0
    private var title: String = ""
    private var description: String = ""
    private var descriptionConfirm: String = "Xác nhận"
    private var descriptionCancel: String = "Quay lại"

    private var confirmListener: (() -> Unit)? = null
    fun setConfirmListener(listener: (() -> Unit)) {
        confirmListener = listener
    }
    private var cancelListener: (() -> Unit)? = null
    fun setCancelListener(listener: (() -> Unit)) {
        cancelListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyleBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.btnCancel.setSafeOnClickListener {
            cancelListener?.invoke()
            dismiss()
        }

        binding.btnConfirm.setSafeOnClickListener {
            confirmListener?.invoke()
        }
    }

    private fun initView() {
        binding.tvTitle.text = title
        binding.btnConfirm.text = descriptionConfirm
        binding.btnCancel.text = descriptionCancel
        binding.tvContent.text = description
        binding.imvPreview.setImageResource(iconResource)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            Handler().postDelayed(Runnable {
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                setupFullHeight(bottomSheetDialog)
            }, 300L)
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = binding.root.height
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun setTitle(title: String): ConfirmBottomSheetFragment {
        this.title = title
        return this@ConfirmBottomSheetFragment
    }

    fun setContent(content: String): ConfirmBottomSheetFragment {
        description = content
        return this@ConfirmBottomSheetFragment
    }

    fun setIcon(icon: Int): ConfirmBottomSheetFragment {
        iconResource = icon
        return this@ConfirmBottomSheetFragment
    }

    fun setTitleBtnConfirm(titleConfirm: String): ConfirmBottomSheetFragment {
        descriptionConfirm = titleConfirm
        return this@ConfirmBottomSheetFragment
    }

    fun setTitleBtnCancel(titleCancel: String): ConfirmBottomSheetFragment {
        descriptionCancel = titleCancel
        return this@ConfirmBottomSheetFragment
    }

    companion object {
        const val TYPE_CONFIRM_REMOVE_USER_PER = "TYPE_CONFIRM_REMOVE_USER_PER"
    }
}