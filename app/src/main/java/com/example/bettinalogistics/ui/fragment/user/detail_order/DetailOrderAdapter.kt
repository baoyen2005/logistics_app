package com.example.bettinalogistics.ui.fragment.user.detail_order

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity

class DetailOrderAdapter : BaseRclvAdapter() {

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_ITEM = 2
    }

    override fun getLayoutResource(viewType: Int): Int =
        when (viewType) {
            TYPE_HEADER -> R.layout.item_detail_order_header
            else -> R.layout.item_detail_order
        }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(itemView)
            else -> DetailOrderViewHolder(itemView)
        }

    override fun getItemViewType(position: Int): Int {
        if (mDataSet[position] is CommonEntity) {
            return (mDataSet[position] as CommonEntity).getTypeLayout()
        }
        return super.getItemViewType(position)
    }

    inner class DetailOrderViewHolder(itemView: View) :
        BaseRclvVH<CommonEntity>(itemView) {
        private val tvDetailTransactionTitleItem: TextView =
            itemView.findViewById(R.id.tvDetailTransactionTitleItem)
        private val tvDetailTransactionDescripItem: TextView =
            itemView.findViewById(R.id.tvDetailTransactionDescripItem)

        override fun onBind(data: CommonEntity) {
            tvDetailTransactionTitleItem.text = data.title
            if (!data.description.isNullOrEmpty()) {
                tvDetailTransactionDescripItem.text = data.description
            } else {
                tvDetailTransactionDescripItem.text = "-"
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : BaseRclvVH<CommonEntity>(itemView) {
        private val ivHeadIconItem: AppCompatImageView = itemView.findViewById(R.id.ivHeadIconItem)
        private val tvHeaderTitleItem: TextView = itemView.findViewById(R.id.tvHeaderTitleItem)

        override fun onBind(data: CommonEntity) {
            data.icon?.let { ivHeadIconItem.setImageResource(it) }
            tvHeaderTitleItem.text = data.getHeader()
        }
    }
}