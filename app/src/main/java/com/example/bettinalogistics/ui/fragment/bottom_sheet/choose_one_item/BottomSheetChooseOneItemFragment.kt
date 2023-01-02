package com.example.bettinalogistics.ui.fragment.bottom_sheet.choose_one_item

import android.view.View
import androidx.core.view.isVisible
import com.example.baseapp.BaseBottomSheetFragment
import com.example.baseapp.UtilsBase
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.FragmentCommonChooseOneBinding

class BottomSheetChooseOneItemFragment(
) : BaseBottomSheetFragment() {

    override val binding: FragmentCommonChooseOneBinding by lazy {
        FragmentCommonChooseOneBinding.inflate(layoutInflater)
    }
    override val isFocusFullHeight: Boolean = false
    override val isDraggable: Boolean = true

    private var title = ""
    private var listData = listOf<Any>()
    private var selectedData: Any? = null

    private var onFinishSelect: ((Any) -> Unit)? = null


    override fun initView() {
        binding.tvTitle.text = title
        val adapter = BottomSheetChooseOneItemAdapter()
        binding.rvSelectData.adapter = adapter
        adapter.setData(listData, selectedData)
        binding.rvSelectData.isVisible = listData.isNotEmpty()
        binding.searchServiceContactEmpty.root.isVisible = listData.isEmpty()
        adapter.onItemClickListener = { data ->
            onFinishSelect?.invoke(data)
            dismiss()
        }
        adapter.onResultSearch = {
            binding.tvSearchResult.text = requireContext().getString(R.string.count_search_business_result, UtilsBase.g().getBeautyNumber(it))
            binding.llSearchTitle.visibility = View.VISIBLE
            if (it == 0) {
                binding.llSearchTitle.isVisible = false
                binding.rvSelectData.isVisible = false
                binding.searchServiceContactEmpty.root.isVisible = true
            } else {
                binding.llSearchTitle.isVisible = true
                binding.rvSelectData.isVisible = true
                binding.searchServiceContactEmpty.root.isVisible =false
            }
        }
        binding.edtSearch.onClearText = {
            binding.llSearchTitle.visibility = View.GONE
            binding.edtSearch.setVisibleRightText(true)
            binding.rvSelectData.isVisible = true
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
    }
    override fun initListener() {}


    fun setTitle(title: String): BottomSheetChooseOneItemFragment {
        this.title = title
        return this
    }

    fun setData(listData: List<Any>, selectedId: Any): BottomSheetChooseOneItemFragment {
        this.listData = listData
        this.selectedData = selectedId
        return this
    }

    fun setListener(listener: (Any) -> Unit): BottomSheetChooseOneItemFragment {
        this.onFinishSelect = listener
        return this@BottomSheetChooseOneItemFragment
    }
}