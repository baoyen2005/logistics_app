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
import com.example.bettinalogistics.databinding.FragmentUpdateTrackOrderBottomBinding
import com.example.bettinalogistics.model.Track
import com.example.bettinalogistics.utils.Utils_Date
import com.example.bettinalogistics.utils.Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class UpdateTrackOrderBottomSheet : BottomSheetDialogFragment() {
    var onUpdateOrAddFinish: ((Track) -> Unit)? = null
    var track: Track? = null
    val binding: FragmentUpdateTrackOrderBottomBinding by lazy {
        FragmentUpdateTrackOrderBottomBinding.inflate(layoutInflater)
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
        binding.btnAddOrUpdateTrackSave.setOnClickListener {
            if (binding.edtAddressTrackOrder.getContentText().isEmpty()) {
                binding.edtAddressTrackOrder.setVisibleMessageError(
                    getString(
                        R.string.str_empty_input,
                        getString(R.string.str_address_track)
                    )
                )
            }
            if (binding.edtStatusTrackOrder.getContentText().isEmpty()) {
                binding.edtAddressTrackOrder.setVisibleMessageError(
                    getString(
                        R.string.str_empty_input,
                        getString(R.string.str_status_track)
                    )
                )
            } else {
                var trackUpdate: Track? = null
                if (track == null) {
                    trackUpdate = Track(
                        trackCode = "TRACK${Date().time}",
                        address = binding.edtAddressTrackOrder.getContentText(),
                        status = binding.edtStatusTrackOrder.getContentText(),
                        dateUpdate = Utils_Date.convertformDate(
                            Date(),
                            DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
                        )
                    )
                } else {
                    trackUpdate = Track(
                        trackCode = track?.trackCode,
                        address = binding.edtAddressTrackOrder.getContentText(),
                        status = binding.edtStatusTrackOrder.getContentText(),
                        dateUpdate = Utils_Date.convertformDate(
                            Date(),
                            DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
                        )
                    )
                }
                onUpdateOrAddFinish?.invoke(trackUpdate)
                dismiss()
            }
        }
    }

    private fun initView() {
        if (track == null) {
            binding.edtStatusTrackOrder.clearContent()
            binding.edtAddressTrackOrder.clearContent()
        } else {
            binding.edtStatusTrackOrder.setValueContent(track?.status)
            binding.edtAddressTrackOrder.setValueContent(track?.address)
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
}