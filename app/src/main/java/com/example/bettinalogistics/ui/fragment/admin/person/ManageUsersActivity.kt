package com.example.bettinalogistics.ui.fragment.admin.person

import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityManageUsersBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageUsersActivity : BaseActivity() {
    private val adminListUserAdapter: AdminListUserAdapter by lazy { AdminListUserAdapter() }

    override val viewModel: AdminAccountViewModel by viewModel()

    override val binding: ActivityManageUsersBinding by lazy {
        ActivityManageUsersBinding.inflate(layoutInflater)
    }

    override fun initView() {
        showLoading()
        viewModel.getAllUser()
        binding.rvAdminListUser.adapter = adminListUserAdapter
        binding.emptyAdminListUser.tvEmptyLayoutTitle.text = getString(R.string.str_user_list_empty)
    }

    override fun initListener() {
        adminListUserAdapter.onItemClickListener = {
            startActivity(UserDetailAdminActivity.startDetailOrderActivity(this, it))
        }
        adminListUserAdapter.onSearchResult = {
            binding.tvResultSearchTitle.isVisible =
                it != 0 && binding.edtAdminUserListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.isVisible =
                it != 0 && binding.edtAdminUserListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.text =
                getString(R.string.str_result_search, UtilsBase.g().getBeautyNumber(it))
            if (it == 0) {
                binding.rvAdminListUser.isVisible = false
                binding.emptyAdminListUser.root.isVisible = true
            } else {
                binding.rvAdminListUser.isVisible = true
                binding.emptyAdminListUser.root.isVisible = false
            }
        }
    }

    override fun observeData() {
        viewModel.getAllUserLiveData.observe(this) {
            hiddenLoading()
            if (it != null) {
                adminListUserAdapter.setData(it)
                binding.emptyAdminListUser.root.isVisible = false
                binding.rvAdminListUser.isVisible = true
            } else {
                binding.emptyAdminListUser.root.isVisible = true
                binding.rvAdminListUser.isVisible = false
            }
        }
    }

}