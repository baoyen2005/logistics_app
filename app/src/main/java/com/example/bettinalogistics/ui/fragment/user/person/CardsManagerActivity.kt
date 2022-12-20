package com.example.bettinalogistics.ui.fragment.user.person

import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.baseapp.UtilsBase
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityCardsManagerBinding
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ConnectCardBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel

class CardsManagerActivity : BaseActivity() {
    private val cardAdapter: CardAdapter by lazy { CardAdapter() }

    override val viewModel: UserPersonViewModel by viewModel()

    override val binding: ActivityCardsManagerBinding by lazy {
        ActivityCardsManagerBinding.inflate(layoutInflater)
    }
    override fun initView() {
        showLoading()
        viewModel.getAllCard()
        binding.headerCard.tvHeaderTitle.text = getString(R.string.str_list_card)
        binding.rvListCard.adapter = cardAdapter
        binding.emptyListCard.tvEmptyLayoutTitle.text = getString(R.string.str_card_list_empty)
    }

    override fun initListener() {
        binding.headerCard.ivHeaderBack.setOnClickListener {
            finish()
        }
        cardAdapter.onItemClickListener = {
            val cardBottomSheet = ConnectCardBottomSheet()
            cardBottomSheet.card = it
            cardBottomSheet.onDeleteListener = {
                viewModel.deleteCard(it)
            }
            cardBottomSheet.onConfirmListener = {
                viewModel.updateCard(it)
            }
            cardBottomSheet.show(supportFragmentManager,"ssss")
        }
        cardAdapter.onSearchResult = {
            binding.tvResultSearchTitle.isVisible =
                it != 0 && binding.edtCardsListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.isVisible =
                it != 0 && binding.edtCardsListSearch.getContentText().isNotEmpty()
            binding.tvResultSearch.text =
                getString(R.string.str_result_search, UtilsBase.g().getBeautyNumber(it))
            if (it == 0) {
                binding.rvListCard.isVisible = false
                binding.emptyListCard.root.isVisible = true
            } else {
                binding.rvListCard.isVisible = true
                binding.emptyListCard.root.isVisible = false
            }
        }
        binding.tvAddCard.setSafeOnClickListener {
            val cardBottomSheet = ConnectCardBottomSheet()
            cardBottomSheet.card = null
            cardBottomSheet.onConfirmListener = {
                viewModel.addCard(it)
            }
            cardBottomSheet.show(supportFragmentManager,"ssss")
        }
    }

    override fun observeData() {
        viewModel.getAllCardLiveData.observe(this) {
            hiddenLoading()
            if (it != null) {
                cardAdapter.setData(it)
                binding.emptyListCard.root.isVisible = false
                binding.rvListCard.isVisible = true
            } else {
                binding.emptyListCard.root.isVisible = true
                binding.rvListCard.isVisible = false
            }
        }
        viewModel.addCardLiveData.observe(this){
            hiddenLoading()
            if(it){
                confirm.newBuild().setNotice(getString(R.string.add_card_success)).addButtonAgree {
                    showLoading()
                    viewModel.getAllCard()
                }
            }
            else{
                confirm.newBuild().setNotice(getString(R.string.add_card_success)).addButtonAgree {
                    showLoading()
                    viewModel.getAllCard()
                }
            }
        }
        viewModel.updateCardLiveData.observe(this){
            hiddenLoading()
            if(it){
                confirm.newBuild().setNotice(getString(R.string.str_update_card_success)).addButtonAgree {
                    showLoading()
                    viewModel.getAllCard()
                }
            }
            else{
                confirm.newBuild().setNotice(getString(R.string.update_card_failed)).addButtonAgree {
                    showLoading()
                    viewModel.getAllCard()
                }
            }
        }

        viewModel.deleteCardLiveData.observe(this){
            hiddenLoading()
            if(it){
                confirm.newBuild().setNotice(getString(R.string.str_delete_success)).addButtonAgree {
                    showLoading()
                    viewModel.getAllCard()
                }
            }
            else{
                confirm.newBuild().setNotice(getString(R.string.str_delete_fail)).addButtonAgree {
                    showLoading()
                    viewModel.getAllCard()
                }
            }
        }
    }

}