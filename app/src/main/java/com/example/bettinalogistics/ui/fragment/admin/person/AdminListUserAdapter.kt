package com.example.bettinalogistics.ui.fragment.admin.person

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.BaseVHData
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.User

class AdminListUserAdapter : BaseRclvAdapter(), Filterable {

    private val listFilter = mutableListOf<AdminUserVHData>()
    var onItemClickListener: ((User) -> Unit)? = null
    var onSearchResult: ((Int) -> Unit)? = null

    override fun getItemCount(): Int {
        return listFilter.size
    }

    override fun getItemDataAtPosition(position: Int): Any {
        return listFilter[position]
    }

    fun setData(dataList: List<User>) {
        val datas = mutableListOf<AdminUserVHData>()
        dataList.forEachIndexed { index, data ->
            datas.add(AdminUserVHData(data))
        }
        listFilter.clear()
        listFilter.addAll(datas)
        reset(listFilter)
    }

    override fun getLayoutResource(viewType: Int): Int = R.layout.manage_user_item

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        UserListManagementHolder(itemView)


    class AdminUserVHData(data: Any) : BaseVHData<Any>(data) {

    }

    inner class UserListManagementHolder(itemView: View) : BaseRclvVH<AdminUserVHData>(itemView) {
        private val tvUserNameAdminItem: TextView by lazy { itemView.findViewById(R.id.tvUserNameAdminItem) }
        private val tvUserEmailAdminItem: TextView by lazy { itemView.findViewById(R.id.tvUserEmailAdminItem) }
        private val ivUserAvtAdminItem: ImageView by lazy { itemView.findViewById(R.id.ivUserAvtAdminItem) }
        private val constUserItem: ConstraintLayout by lazy { itemView.findViewById(R.id.constUserItem) }

        init {
            constUserItem.setSafeOnClickListener {
                if (bindingAdapterPosition > -1) {
                    val data = listFilter[bindingAdapterPosition]
                    data.realData?.let {
                        onItemClickListener?.invoke(it as User)
                    }
                }
            }
        }

        override fun onBind(adminconstUserItemVhData: AdminUserVHData) {
            val data = adminconstUserItemVhData.realData as User
            tvUserNameAdminItem.text = data.fullName ?: ""
            tvUserEmailAdminItem.text = data.email ?: ""
            Glide.with(itemView.context).load(data.image).into(ivUserAvtAdminItem)
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
                        if (data is AdminUserVHData && data.realData is User && normalization(((data.realData as User).fullName?.lowercase())).contains(
                                key.lowercase()
                            )
                            || data is AdminUserVHData && data.realData is User && normalization((data.realData as User).email?.lowercase()).contains(
                                key.lowercase()
                            )
                            || data is AdminUserVHData && data.realData is User && normalization((data.realData as User).phone?.lowercase()).contains(
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
                    listFilter.addAll(mDataSet as ArrayList<AdminUserVHData>)
                } else {
                    listFilter.addAll(filterResults.values as ArrayList<AdminUserVHData>)
                }
                onSearchResult?.invoke(listFilter.size)
                notifyDataSetChanged()
            }
        }
    }
}

