package com.example.bettinalogistics.ui.fragment.bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.baseapp.view.safeSubString
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ConnectCardLayoutBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Card
import com.example.bettinalogistics.ui.EditTextRound
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class ConnectCardBottomSheet : BottomSheetDialogFragment() {
    var onConfirmListener: ((Card) -> Unit)? = null
    var card: Card? = null
    var onDeleteListener : (() -> Unit)? = null

    val binding: ConnectCardLayoutBinding by lazy {
        ConnectCardLayoutBinding.inflate(layoutInflater)
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
            val card = Card(
                user = AppData.g().currentUser,
                name = binding.edtCardName.getContentText(),
                accountNumber = binding.edtAccountNumber.getContentText(),
                dateOfExpired = binding.edtDateExpired.getTextEdit(),
                cardNumber = binding.edtCardNumber.getContentText()
            )
            if (checkValidate()) {
                onConfirmListener?.invoke(card)
                dismiss()
            }
        }
        binding.btnDeleteCard.setSafeOnClickListener {
            onDeleteListener?.invoke()
        }
    }

    private fun checkValidate(): Boolean {
        val name = binding.edtCardName.getContentText()
        val cardNumber = binding.edtCardNumber.getContentText()
        val accountNumber = binding.edtAccountNumber.getContentText()
        val dateOfExpired = binding.edtDateExpired.getTextEdit()

        if (name.isEmpty()) {
            binding.edtCardName.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.str_bank_name)
                )
            )
        }
        if (cardNumber.isEmpty()) {
            binding.edtCardNumber.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.str_card_number)
                )
            )
        }

        if (accountNumber.isEmpty()) {
            binding.edtAccountNumber.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.str_account_number)
                )
            )
        }

        if (dateOfExpired.isEmpty()) {
            binding.tvDateExpiredError.text = getString(
                R.string.str_empty_input,
                getString(R.string.str_date_expired)
            )
            binding.tvDateExpiredError.isVisible = true
        }

        return !binding.edtCardName.isTvErrorVisible()
                && !binding.edtCardNumber.isTvErrorVisible()
                && !binding.tvDateExpiredError.isVisible
                && !binding.edtAccountNumber.isTvErrorVisible()
    }

    private fun initView() {
        binding.edtCardNumber.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.edtAccountNumber.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.edtDateExpired.setInputType(InputType.TYPE_CLASS_NUMBER)
        setTimeForEdittext(binding.edtDateExpired)
        binding.btnDeleteCard.isVisible = card != null
        if (card == null) {
            binding.edtCardName.clearContent()
            binding.edtDateExpired.clearText()
            binding.edtCardNumber.clearContent()
            binding.edtAccountNumber.clearContent()
            binding.btnCustomerInforContinued.isVisible = true
        } else {
            binding.edtCardName.setValueContent(card?.name)
            card?.dateOfExpired?.let { binding.edtDateExpired.setEditText(it) }
            binding.edtCardNumber.setValueContent(card?.cardNumber)
            binding.edtAccountNumber.setValueContent(card?.accountNumber)
            binding.edtCardName.onTextChange = {
                binding.btnCustomerInforContinued.isVisible = true
            }
            binding.edtCardNumber.onTextChange = {
                binding.btnCustomerInforContinued.isVisible = true
            }
            binding.edtAccountNumber.onTextChange = {
                binding.btnCustomerInforContinued.isVisible = true
            }
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

    private fun setTimeForEdittext(edtRound: EditTextRound) {
        var current = ""
        val cal = Calendar.getInstance()
        edtRound.onTextChange = { p0 ->
            if (p0.toString() != current) {
                var clean = p0.toString().replace("[^\\d.]|\\.".toRegex(), "")
                val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")
                val cl = clean.length
                var sel = cl
                var i = 2
                while (i <= cl && i < 5) {
                    sel++
                    i += 2
                }
                if (clean == cleanC) sel--

                var month: Int? = if (clean.length >= 2) {
                    Integer.parseInt(clean.substring(0, 2))
                } else null
                var year: Int? = if (clean.length >= 6) {
                    Integer.parseInt(clean.substring(2, 6))
                } else null
                month?.let {
                    month = if (it < 1) 1 else if (it > 12) 12 else month
                    cal.set(Calendar.MONTH, it - 1)
                }
                Log.d(TAG, "setTimeForEdittext: ${Calendar.YEAR}")
                year?.let {
                    year =
                        if (it < Calendar.YEAR) Calendar.YEAR else if (it > 2100) 2100 else year
                    cal.set(Calendar.YEAR, it)
                }

                val stringMonth =
                    if (month == null) clean.safeSubString(0, 2)
                    else String.format("%02d", month)
                val stringYear =
                    if (year == null) clean.safeSubString(2, 6)
                    else String.format("%02d", year)
                clean = stringMonth
                    .plus(
                        if (stringMonth.length == 2) "/"
                        else ""
                    )
                    .plus(stringYear)
                sel = if (sel < 0) 0 else sel
                current = clean
                edtRound.setEditText(current)
                edtRound.setSelection(if (sel < current.count()) sel else current.count())
                binding.btnCustomerInforContinued.isVisible = true
                binding.tvDateExpiredError.isVisible = false
            }
        }
    }
}