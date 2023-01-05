package com.example.bettinalogistics.ui.common_adapter

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.BaseVHData
import com.example.baseapp.view.Textview
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.TypeCommonEntity

class CommonChooseOneItemAdapter() :
    BaseRclvAdapter(), Filterable {
    private val listFilter = mutableListOf<CommonContactVHData>()
    var onResultSearch: ((Int) -> Unit)? =null

    var listener: ((Any) -> Unit)? =null
    override fun getLayoutResource(viewType: Int): Int {
        return R.layout.item_service_contact
    }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> {
        return ServiceVH(itemView)
    }

    override fun getItemCount(): Int {
        return listFilter.size
    }

    override fun getItemDataAtPosition(position: Int): Any {
        return listFilter[position]
    }

    fun setData(dataList: List<Any>) {
        val datas = mutableListOf<CommonContactVHData>()
        dataList.forEachIndexed {index, data->
            datas.add(CommonContactVHData(data))
        }
        listFilter.clear()
        listFilter.addAll(datas)
        reset(listFilter)
    }

    inner class ServiceVH(itemView: View) : BaseRclvVH<CommonContactVHData>(itemView) {
        val tvInformation = itemView.findViewById<Textview>(R.id.tvInformation)
        init {
            itemView.setSafeOnClickListener {
                val position = bindingAdapterPosition
                if (position > -1){
                    val data = listFilter[position]
                    data.realData?.let {
                        listener?.invoke(it as TypeCommonEntity)
                    }
                }
            }
        }

        override fun onBind(data: CommonContactVHData) {
            tvInformation.text = (data.realData as TypeCommonEntity?)?.title?: ""
        }
    }

    class CommonContactVHData(data: Any) : BaseVHData<Any>(data) {
        var isSelected: Boolean = false
        override fun toString(): String {
            return realData.toString()
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
                        if (data is CommonContactVHData && (normalization((data.realData as TypeCommonEntity).title).contains(key))) {
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
                    listFilter.addAll(mDataSet as ArrayList<CommonContactVHData>)
                } else {
                    listFilter.addAll(results.values as ArrayList<CommonContactVHData>)
                }
                notifyDataSetChanged()
            }
        }

    }

}