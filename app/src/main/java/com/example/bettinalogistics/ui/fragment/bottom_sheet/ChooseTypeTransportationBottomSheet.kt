package com.example.bettinalogistics.ui.fragment.bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentChooseTypeTransportationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChooseTypeTransportationBottomSheet : BottomSheetDialogFragment() {
    var typeTransaction: String? = null
    var methodTransaction: String? = null
    var confirmChooseTypeTransaction: ((String, String) -> Unit)? = null
    val binding: FragmentChooseTypeTransportationBottomBinding by lazy {
        FragmentChooseTypeTransportationBottomBinding.inflate(layoutInflater)
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
        initObserver()
        return binding.root
    }

    private fun initObserver() {

    }

    private fun initListener() {
        if (binding.checkBoxPayFullTax.isChecked) {
            binding.checkBoxPayLumpsum.isChecked = false
            methodTransaction = binding.checkBoxPayFullTax.text.toString()
        } else {
            binding.checkBoxPayFullTax.isChecked = false
            methodTransaction = binding.checkBoxPayLumpsum.text.toString()
        }
        if (binding.linearRoadTransport.isClickable) {
            binding.linearSeaTransport.isClickable = false
            binding.linearRoadTransport.setBackgroundResource(R.drawable.shape_ffffff_stroke_004a9c_corner_12)
            binding.linearSeaTransport.setBackgroundResource(R.drawable.shape_bg_fffff_corner_12)
            typeTransaction = requireContext().getString(R.string.str_road_transport)
        } else {
            binding.linearRoadTransport.isClickable = false
            binding.linearSeaTransport.setBackgroundResource(R.drawable.shape_ffffff_stroke_004a9c_corner_12)
            binding.linearRoadTransport.setBackgroundResource(R.drawable.shape_bg_fffff_corner_12)
            typeTransaction = requireContext().getString(R.string.str_sea_transport)

        }
        binding.btnChooseTypeTransportationConfirm.setOnClickListener {
            if(typeTransaction.isNullOrEmpty()){
                binding.tvErrorTypeTransaction.visibility = View.VISIBLE
                binding.tvErrorTypeTransaction.text =requireContext().getString(R.string.tr_choose_type_transportation)
            }
            else if(methodTransaction.isNullOrEmpty()){
                binding.tvErrorMethodTransaction.visibility = View.VISIBLE
                binding.tvErrorMethodTransaction.text =requireContext().getString(R.string.tr_choose_method_transportation)
            }
            else {
                binding.tvErrorTypeTransaction.visibility = View.GONE
                binding.tvErrorMethodTransaction.visibility = View.GONE
                confirmChooseTypeTransaction?.invoke(typeTransaction?:"", methodTransaction?:"")
                dismiss()
            }
        }
    }

    private fun initView() {
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