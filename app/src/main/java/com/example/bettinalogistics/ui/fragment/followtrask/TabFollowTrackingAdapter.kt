package com.example.bettinalogistics.ui.fragment.followtrask

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.ConfirmOrder


class TabFollowTrackingAdapter() : BaseRclvAdapter() {
    private var onItemClickListener: ((CommonEntity) ->Unit)? = null

    override fun getLayoutResource(viewType: Int): Int = R.layout.item_follow_tracking_tab

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> = FollowTrackingTabViewHolder(itemView)

    inner class FollowTrackingTabViewHolder(itemView: View) :
        BaseRclvVH<CommonEntity>(itemView) {
        private val viewTrackingTabItem : View = itemView.findViewById(R.id.viewTrackingTabItem)
        private val tvTrackingTabTitleItem: TextView =  itemView.findViewById(R.id.tvTrackingTabTitleItem)
        init {
            itemView.setSafeOnClickListener {
                val position = bindingAdapterPosition
                if(position > -1){
                    mDataSet.forEachIndexed { index, any ->
                        if ((any as CommonEntity).isHightLight) {
                            if (index == position) return@forEachIndexed
                            any.isHightLight = false
                            notifyItemChanged(index)
                        }
                        if (index == position) {
                            any.isHightLight = true
                            notifyItemChanged(index)
                        }
                    }
                    onItemClickListener?.invoke(mDataSet[position] as CommonEntity)
                }
            }
        }
        override fun onBind(data: CommonEntity) {
            tvTrackingTabTitleItem.text = data.title
            viewTrackingTabItem.isVisible = data.isHightLight
        }
    }

}