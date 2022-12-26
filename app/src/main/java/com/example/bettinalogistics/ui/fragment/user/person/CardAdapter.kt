package com.example.bettinalogistics.ui.fragment.user.person

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.BaseVHData
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.Card

class CardAdapter : BaseRclvAdapter(), Filterable {

    private val listFilter = mutableListOf<CardVHData>()
    var onItemClickListener: ((Card) -> Unit)? = null
    var onSearchResult: ((Int) -> Unit)? = null

    override fun getItemCount(): Int {
        return listFilter.size
    }

    override fun getItemDataAtPosition(position: Int): Any {
        return listFilter[position]
    }

    fun setData(dataList: List<Card>) {
        val datas = mutableListOf<CardVHData>()
        dataList.forEachIndexed { index, data ->
            datas.add(CardVHData(data))
        }
        listFilter.clear()
        listFilter.addAll(datas)
        reset(listFilter)
    }

    override fun getLayoutResource(viewType: Int): Int = R.layout.card_item

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        CardViewHolder(itemView)


    class CardVHData(data: Any) : BaseVHData<Any>(data) {

    }

    inner class CardViewHolder(itemView: View) : BaseRclvVH<CardVHData>(itemView) {
        private val tvBankNameItem: TextView by lazy { itemView.findViewById(R.id.tvBankNameItem) }
        private val tvAccountNumberItem: TextView by lazy { itemView.findViewById(R.id.tvAccountNumberItem) }
        private val constUserItem: ConstraintLayout by lazy { itemView.findViewById(R.id.constUserItem) }

        init {
            constUserItem.setSafeOnClickListener {
                if (bindingAdapterPosition > -1) {
                    val data = listFilter[bindingAdapterPosition]
                    data.realData?.let {
                        onItemClickListener?.invoke(it as Card)
                    }
                }
            }
        }

        override fun onBind(adminconstUserItemVhData: CardVHData) {
            val data = adminconstUserItemVhData.realData as Card
            tvBankNameItem.text = data.name ?: ""
            tvAccountNumberItem.text = data.accountNumber ?: ""
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString().trim()
                if (charString.isEmpty()) {
                    return null
                } else {
                    val filteredList: MutableList<Any> = ArrayList()
                    val results = FilterResults()
                    val key = normalization(charString)
                    mDataSet.forEach { data ->
                        if (data is CardVHData && data.realData is Card && normalization(((data.realData as Card).cardNumber?.lowercase())).contains(
                                key.lowercase()
                            )
                            || data is CardVHData && data.realData is Card && normalization((data.realData as Card).accountNumber?.lowercase()).contains(
                                key.lowercase()
                            )
                        ) {
                            filteredList.add(data)
                        }
                    }
                    results.values = filteredList
                    return results
                }
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                listFilter.clear()
                if (filterResults?.values == null) {
                    listFilter.addAll(mDataSet as ArrayList<CardVHData>)
                } else {
                    listFilter.addAll(filterResults.values as ArrayList<CardVHData>)
                }
                onSearchResult?.invoke(listFilter.size)
                notifyDataSetChanged()
            }
        }
    }
}

