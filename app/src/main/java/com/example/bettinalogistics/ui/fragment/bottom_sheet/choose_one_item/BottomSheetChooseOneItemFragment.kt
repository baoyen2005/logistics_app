package com.example.bettinalogistics.ui.fragment.bottom_sheet.choose_one_item

import com.example.baseapp.BaseBottomSheetFragment
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
        adapter.onItemClickListener = { data ->
            onFinishSelect?.invoke(data)
            dismiss()
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