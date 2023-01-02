package com.example.bettinalogistics.ui.fragment.bottom_sheet.choose_one_item

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.BaseVHData
import com.example.baseapp.view.ThemeUtils
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.TypeCommonEntity
import com.example.bettinalogistics.ui.common_adapter.CommonChooseOneItemAdapter
import org.checkerframework.checker.units.qual.C

class BottomSheetChooseOneItemAdapter : BaseRclvAdapter(), Filterable {
    private val listFilter = mutableListOf<BSChooseOneItemVHData>()
    var onResultSearch: ((Int) -> Unit)? =null
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
        listFilter.clear()
        listFilter.addAll(list)
        reset(listFilter)
    }

    class BSChooseOneItemVHData(data: Any) : BaseVHData<Any>(data) {
        var isSelected = false
        var title = ""
    }

    override fun getItemCount(): Int {
        return listFilter.size
    }

    override fun getItemDataAtPosition(position: Int): Any {
        return listFilter[position]
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    return null
                } else {
                    val filteredList: MutableList<Any> = ArrayList()
                    val results = FilterResults()
                    val key = normalization(charString)
                    mDataSet.forEach { data ->
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (data is BSChooseOneItemVHData && (normalization(data.title).contains(key))) {
                            filteredList.add(data)
                        }
                    }
                    results.values = filteredList
                    return results
                }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                listFilter.clear()
                if (results?.values == null) {
                    listFilter.addAll(mDataSet as ArrayList<BSChooseOneItemVHData>)
                } else {
                    listFilter.addAll(results.values as ArrayList<BSChooseOneItemVHData>)
                }
                notifyDataSetChanged()
            }
        }

    }

}