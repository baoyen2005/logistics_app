package com.example.bettinalogistics.ui.fragment.user.detail_order

import com.example.baseapp.BaseBottomSheetFragment
import com.example.baseapp.UtilsBase
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentDetailUserTrackBinding
import com.example.bettinalogistics.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior

class DetailUserTrackBottomSheetFragment : BaseBottomSheetFragment() {
    var track: Track? = null
    override val binding: FragmentDetailUserTrackBinding by lazy {
        FragmentDetailUserTrackBinding.inflate(layoutInflater)
    }
    override val isFocusFullHeight: Boolean = false

    override val isDraggable = true

    override fun initListener() {
    }

    override fun initView() {
        binding.tvDetailOrderTrackSmallTitle.text = getString(R.string.str_info_track_for, track?.trackCode ?: "")
        binding.tvDetailDateTrack.text = getString(R.string.str_update_to, track?.dateUpdate ?: "")
        binding.tvDetailAddressTrack.text = getString(R.string.str_now_address, track?.address ?: "")
        binding.tvDetailStatusTrack.text = getString(R.string.str_now_status, track?.status ?: "")
        binding.tvDetailShipperInfo.text =
            getString(R.string.str_shipper_info, track?.shipper?.name ?: "", UtilsBase.g().maskPhoneNumber(track?.shipper?.phone ?: ""))
    }


    override fun onStageChanged(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss()
        }
    }
}
