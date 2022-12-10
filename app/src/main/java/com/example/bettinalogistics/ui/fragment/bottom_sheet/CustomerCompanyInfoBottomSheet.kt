package com.example.bettinalogistics.ui.fragment.bottom_sheet

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
import com.example.bettinalogistics.databinding.CustomerCompanyInfoLayoutBinding
import com.example.bettinalogistics.model.UserCompany
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomerCompanyInfoBottomSheet : BottomSheetDialogFragment() {
    var onConfirmListener: ((UserCompany) -> Unit)? = null
    var company: UserCompany? = null

    val binding: CustomerCompanyInfoLayoutBinding by lazy {
        CustomerCompanyInfoLayoutBinding.inflate(layoutInflater)
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
        binding.btnCustomerInforContinued.setSafeOnClickListener {
            val userCompany = UserCompany(
                name = binding.edtCompanyName.getContentText(),
                address = binding.edtCompanyAddress.getContentText(),
                texCode = binding.edtCompanyTexCode.getContentText(),
                businessType = binding.edtCompanyBusinessType.getContentText()
            )
            if (checkValidate()) {
                onConfirmListener?.invoke(userCompany)
                dismiss()
            }
        }
    }

    private fun checkValidate(): Boolean {
        val name = binding.edtCompanyName.getContentText()
        val address = binding.edtCompanyAddress.getContentText()
        val texCode = binding.edtCompanyTexCode.getContentText()
        val businessType = binding.edtCompanyBusinessType.getContentText()
        when {
            name.isEmpty() -> {
                binding.edtCompanyName.setVisibleMessageError(getString(R.string.invalid_field))
                return false
            }
            address.isEmpty() -> {
                binding.edtCompanyAddress.setVisibleMessageError(getString(R.string.invalid_field))
                return false
            }
            texCode.isEmpty() -> {
                binding.edtCompanyTexCode.setVisibleMessageError(getString(R.string.invalid_field))
                return false
            }
            businessType.isEmpty() -> {
                binding.edtCompanyBusinessType.setVisibleMessageError(getString(R.string.invalid_field))
                return false
            }
            else ->{
                binding.edtCompanyName.setGoneMessageError()
                binding.edtCompanyAddress.setGoneMessageError()
                binding.edtCompanyTexCode.setGoneMessageError()
                binding.edtCompanyBusinessType.setGoneMessageError()
                return true
            }
        }
    }

    private fun initView() {
        if (company == null) {
            binding.edtCompanyName.clearText()
            binding.edtCompanyAddress.clearText()
            binding.edtCompanyTexCode.clearText()
            binding.edtCompanyBusinessType.clearText()
        } else {
            binding.edtCompanyName.setValueContent(company?.name?:"")
            binding.edtCompanyAddress.setValueContent(company?.address?:"")
            binding.edtCompanyTexCode.setValueContent(company?.texCode?:"")
            binding.edtCompanyBusinessType.setValueContent(company?.businessType?:"")
        }
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
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = binding.root.height
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}