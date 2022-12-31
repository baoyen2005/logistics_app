package com.example.bettinalogistics.ui.fragment.bottom_sheet

import com.example.baseapp.BaseBottomSheetFragment
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.SupplierCompanyInfoLayoutBinding
import com.example.bettinalogistics.model.SupplierCompany

class SupplierCompanyInfoBottomSheet : BaseBottomSheetFragment() {
    var onConfirmListener: ((SupplierCompany) -> Unit)? = null

    override val isFocusFullHeight: Boolean = true
    override val isDraggable: Boolean = true

    override val binding: SupplierCompanyInfoLayoutBinding by lazy {
        SupplierCompanyInfoLayoutBinding.inflate(layoutInflater)
    }

    override fun initListener() {
        binding.btnCustomerInforContinued.setSafeOnClickListener {
            val supplierCompany = SupplierCompany(
                name = binding.edtCompanyName.getContentText(),
                address = binding.edtCompanyAddress.getContentText(),
                phone = binding.edtCompanyPhone.getContentText(),
                email = binding.edtCompanyEmail.getContentText()
            )
            if (checkValidate()) {
                onConfirmListener?.invoke(supplierCompany)
                dismiss()
            }
        }
    }

    private fun checkValidate(): Boolean {
        val name = binding.edtCompanyName.getContentText()
        val address = binding.edtCompanyAddress.getContentText()
        val phone = binding.edtCompanyPhone.getContentText()
        val email = binding.edtCompanyEmail.getContentText()
        when {
            name.isEmpty() -> {
                binding.edtCompanyName.setVisibleMessageError(getString(R.string.invalid_field))
            }
            address.isEmpty() -> {
                binding.edtCompanyAddress.setVisibleMessageError(getString(R.string.invalid_field))
            }
            phone.isEmpty() -> {
                binding.edtCompanyPhone.setVisibleMessageError(getString(R.string.invalid_field))
            }
            email.isEmpty() -> {
                binding.edtCompanyEmail.setVisibleMessageError(getString(R.string.invalid_field))
            }
        }
        return !binding.edtCompanyName.isTvErrorVisible()
                && !binding.edtCompanyAddress.isTvErrorVisible()
                && !binding.edtCompanyPhone.isTvErrorVisible()
                && !binding.edtCompanyEmail.isTvErrorVisible()
    }

    override fun initView() {
    }
}