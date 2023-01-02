package com.example.bettinalogistics.ui.fragment.bottom_sheet.choose_one_item

import android.view.View
import android.widget.TextView
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.BaseVHData
import com.example.baseapp.view.ThemeUtils
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity

class BottomSheetChooseOneItemAdapter : BaseRclvAdapter() {

    var onItemClickListener: ((Any) -> Unit)? = null

    fun setData(listData: List<Any>, selectedData: Any?) {
        val list = mutableListOf<BSChooseOneItemVHData>()
        listData.forEach { dataItem ->
            val data = BSChooseOneItemVHData(dataItem).apply {
                when (realData) {
                    is CommonEntity -> {
                        dataItem as CommonEntity
                        selectedData as CommonEntity
                        title = dataItem.title ?: ""
                        isSelected = dataItem.getIdString() == selectedData.getIdString()
                    }
                }
            }
            list.add(data)
        }
        reset(list)
    }

    class BSChooseOneItemVHData(data: Any) : BaseVHData<Any>(data) {
        var isSelected = false
        var title = ""
    }

    override fun getLayoutResource(viewType: Int): Int {
        return R.layout.item_choose_common
    }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> {
        return BottomSheetChooseOneItemHolder(itemView)
    }

    inner class BottomSheetChooseOneItemHolder(itemView: View) :
        BaseRclvVH<BSChooseOneItemVHData>(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.tvChooseCommonOneTitleItm)

        init {
            itemView.setSafeOnClickListener {
                val position = bindingAdapterPosition
                if (position > -1) {
                    val data = mDataSet[position] as BSChooseOneItemVHData
                    if (!data.isSelected) {
                        onItemClickListener?.invoke(data.realData!!)
                    }
                }
            }
        }

        override fun onBind(data: BSChooseOneItemVHData) {
            textView.text = data.title
            itemView.isSelected = data.isSelected

            val idIcon = if (data.isSelected) {
                R.drawable.ic_radio_button_selected
            } else {
                R.drawable.ic_radio_button_unselected
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ThemeUtils.getDrawable(textView.context, idIcon),
                null
            )
        }

    }


}