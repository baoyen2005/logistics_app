package com.example.bettinalogistics.ui.fragment.bottom_sheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.baseapp.utils.UtilsBase
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.PaymentOrderLayoutBinding
import com.example.bettinalogistics.model.Card
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppPermissionsUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentOrderBottomSheet : BottomSheetDialogFragment() {
    var onConfirmListener: ((String, String, Card) -> Unit)? = null
    var listCard: List<Card>? = null
    var order: Order? = null
    var card : Card? = null
    private var uri: String? = null
    private lateinit var appPermissions: AppPermissionsUtils

    val binding: PaymentOrderLayoutBinding by lazy {
        PaymentOrderLayoutBinding.inflate(layoutInflater)
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
        binding.edtChooseCard.setSafeOnClickListener {
            val chooseOneItem = listCard?.let { it1 ->
                DialogChooseCard(it1) {
                    binding.edtChooseCard.setValueContent(it.name + " - " + it.accountNumber)
                    card = it
                }.setTitle("Chọn thẻ thanh toán")
            }
            chooseOneItem?.show(requireActivity().supportFragmentManager,"ss")
        }
        binding.edtChooseBill.setSafeOnClickListener {
            pickImage()
        }
        binding.btnUserPaymentConfirm.setSafeOnClickListener {
            if(checkValidate()){
                card?.let { it1 ->
                    onConfirmListener?.invoke(
                        binding.edtContentPayment.getContentText(),
                        binding.edtChooseBill.getContentText(), it1
                    )
                }
                dismiss()
            }
        }
    }

    private fun checkValidate(): Boolean{
        if (binding.edtContentPayment.getContentText().isEmpty()) {
            binding.edtContentPayment.setVisibleMessageError(getString(R.string.invalid_field))
        }
        if(card == null){
            binding.edtChooseCard.setVisibleMessageError(getString(R.string.invalid_field))
        }
        return !binding.edtContentPayment.isTvErrorVisible()  && !binding.edtChooseCard.isTvErrorVisible()
    }

    private fun initView() {
        appPermissions = AppPermissionsUtils()
        if (!appPermissions.isStorageOk(requireContext())) {
            appPermissions.requestStoragePermission(requireActivity())
        }
        binding.edtChooseCard.setEnableEdittext(false)
        binding.edtChooseBill.setEnableEdittext(false)
        binding.edtAmountOrder.setEnableEdittext(false)
        binding.edtAmountOrder.setValueContent(
            UtilsBase.g().getDotMoneyHasCcy((order?.amountAfterDiscount ?: 0L).toString(), "d")
        )
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

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        launcher.launch(intent)
    }

    @SuppressLint("SetTextI18n")
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK
            && result.data != null
        ) {
            val uriImg = result.data?.data!!
            requireActivity().contentResolver.takePersistableUriPermission(
                uriImg,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            uri = uriImg.toString()
            binding.edtChooseBill.setValueContent(
                uriImg.toString().substring(0, 40) + "..."
            )
        } else {
            Log.d(AppConstant.TAG, "result = null: ")
        }
    }
}