package com.example.bettinalogistics.ui.fragment.user.followtrask

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity


class UserTabFollowTrackingAdapter() : BaseRclvAdapter() {
    var onItemClickListener: ((CommonEntity) ->Unit)? = null

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