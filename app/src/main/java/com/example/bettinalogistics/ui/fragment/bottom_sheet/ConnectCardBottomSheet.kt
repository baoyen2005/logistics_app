package com.example.bettinalogistics.ui.fragment.bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.baseapp.view.EditText
import com.example.baseapp.view.safeSubString
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ConnectCardLayoutBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Card
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class ConnectCardBottomSheet : BottomSheetDialogFragment() {
    var onConfirmListener: ((Card) -> Unit)? = null
    var card: Card? = null

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
                dateOfExpired = binding.edtDateExpired.getContentText(),
                cardNumber = binding.edtCardNumber.getContentText()
            )
            if (checkValidate()) {
                onConfirmListener?.invoke(card)
                dismiss()
            }
        }
    }

    private fun checkValidate(): Boolean {
        val name = binding.edtCardName.getContentText()
        val cardNumber = binding.edtCardNumber.getContentText()
        val accountNumber = binding.edtAccountNumber.getContentText()
        val dateOfExpired = binding.edtDateExpired.getContentText()

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
            binding.edtDateExpired.setVisibleMessageError(
                getString(
                    R.string.str_empty_input,
                    getString(R.string.str_date_expired)
                )
            )
        }

        return !binding.edtCardName.isTvErrorVisible()
                && !binding.edtCardNumber.isTvErrorVisible()
                && !binding.edtDateExpired.isTvErrorVisible()
                && !binding.edtAccountNumber.isTvErrorVisible()
    }

    private fun initView() {
        if (card == null) {
            binding.edtCardName.clearContent()
            binding.edtDateExpired.clearContent()
            binding.edtCardNumber.clearContent()
            binding.edtAccountNumber.clearContent()
            binding.btnCustomerInforContinued.isVisible = true
        } else {
            binding.edtCardName.setValueContent(card?.name)
            binding.edtDateExpired.setValueContent(card?.dateOfExpired)
            binding.edtCardNumber.setValueContent(card?.cardNumber)
            binding.edtAccountNumber.setValueContent(card?.accountNumber)
            binding.edtCardName.onTextChange = {
                binding.btnCustomerInforContinued.isVisible = true
            }
            binding.edtDateExpired.onTextChange = {
                binding.btnCustomerInforContinued.isVisible = true
            }
            binding.edtCardNumber.onTextChange = {
                binding.btnCustomerInforContinued.isVisible = true
            }
            binding.edtAccountNumber.onTextChange = {
                binding.btnCustomerInforContinued.isVisible = true
            }
        }

        setTimeForEdittext(binding.edtDateExpired.getEditText())
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

    private fun setTimeForEdittext(edtRound: EditText) {
        edtRound.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val cal = Calendar.getInstance()

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != current) {
                    var clean = p0.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")
                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 7) {
                        sel++
                        i += 2
                    }
                    if (clean == cleanC) sel--

                    var mon: Int? = if (clean.length >= 2) {
                        Integer.parseInt(clean.substring(0, 2))
                    } else null
                    var year: Int? = if (clean.length >= 6) {
                        Integer.parseInt(clean.substring(2, 6))
                    } else null
                    mon?.let {
                        mon = if (it < 1) 1 else if (it > 12) 12 else mon
                        cal.set(Calendar.MONTH, it - 1)
                    }
                    year?.let {
                        year =
                            if (it < Calendar.YEAR) Calendar.YEAR else if (it > 2100) 2100 else year
                        cal.set(Calendar.YEAR, it)
                    }
                    val stringMon =
                        if (mon == null) clean.safeSubString(2, 4) else String.format("%02d", mon)
                    val stringYear =
                        if (year == null) clean.safeSubString(4, 8) else String.format("%02d", year)
                    clean = stringMon.plus(if (stringMon.length == 2) "/" else "")
                        .plus(stringYear)
                    sel = if (sel < 0) 0 else sel
                    current = clean
                    edtRound.setText(current)
                    edtRound.setSelection(if (sel < current.count()) sel else current.count())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable) {
            }
        })
    }
}