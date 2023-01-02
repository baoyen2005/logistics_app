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
import com.example.baseapp.BaseBottomSheetFragment
import com.example.baseapp.UtilsBase
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.DialogServiceContactBinding
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.TypeCommonEntity
import com.example.bettinalogistics.ui.common_adapter.CommonChooseOneItemAdapter
import com.example.bettinalogistics.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogCommonChooseOneItem(
    val dataList: List<Any>,
    val listener: (CommonEntity) -> Unit
) : BaseBottomSheetFragment() {

    override val binding : DialogServiceContactBinding by lazy {
        DialogServiceContactBinding.inflate(layoutInflater)
    }
    override val isFocusFullHeight: Boolean = true

    private val adapter: CommonChooseOneItemAdapter by lazy {
        CommonChooseOneItemAdapter()
    }

    private var mTitle = ""
    private var mHint = ""

    override fun initView() {
        adapter.listener = {
            listener.invoke(it)
            dismiss()
        }
        binding.rcvContent.isVisible = dataList.isNotEmpty()
        binding.searchServiceContactEmpty.root.isVisible = dataList.isEmpty()
        adapter.setData(dataList)

        binding.rcvContent.adapter = adapter

        adapter.onResultSearch = {
            binding.tvSearchResult.text = requireContext().getString(R.string.count_search_business_result, UtilsBase.g().getBeautyNumber(it))
            binding.llSearchTitle.visibility = View.VISIBLE
            if (it == 0) {
                binding.llSearchTitle.isVisible = false
                binding.rcvContent.isVisible = false
                binding.searchServiceContactEmpty.root.isVisible = true
            } else {
                binding.llSearchTitle.isVisible = true
                binding.rcvContent.isVisible = true
                binding.searchServiceContactEmpty.root.isVisible =false
            }
        }
        binding.edtSearch.onClearText = {
            binding.llSearchTitle.visibility = View.GONE
            binding.edtSearch.setVisibleRightText(true)
            binding.rcvContent.isVisible = true
            binding.searchServiceContactEmpty.root.isVisible = false
        }
        binding.edtSearch.setRightTextClickListener {
            binding.llSearchTitle.visibility = View.GONE
            binding.edtSearch.clearFocusEdittext()
            binding.edtSearch.setVisibleRightText(false)
        }
        binding.edtSearch.onTextChange = {
            binding.tvSearchResult.isVisible = !it.isNullOrEmpty()
            binding.tvSearchTitle.isVisible = !it.isNullOrEmpty()
            adapter.filter.filter(it)
        }
        initTitle()

    }

    override fun initListener() {
        binding.imgClose.setSafeOnClickListener {
            dismiss()
        }
    }

    private fun initTitle() {
        binding.tvTitle.text = mTitle
        if(mHint.isEmpty()){
            binding.edtSearch.setHint(requireContext().getString(R.string.str_search))
        }
        else{
            binding.edtSearch.setHint(hint = mHint)
        }
    }

    fun setTitle(title: String): DialogCommonChooseOneItem{
        mTitle = title
        return this@DialogCommonChooseOneItem
    }

    fun setHint(hint: String): DialogCommonChooseOneItem{
        mHint = hint
        return this@DialogCommonChooseOneItem
    }
}