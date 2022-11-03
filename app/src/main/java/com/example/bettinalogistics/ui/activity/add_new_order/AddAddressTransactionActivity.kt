package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.Context
import android.content.Intent
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.databinding.ActivityAddAddressTransactionBinding
import com.example.bettinalogistics.model.Order
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddAddressTransactionActivity : BaseActivity() {
    companion object{
        const val NEW_ORDER = "newOrder"
        fun startAddAddressTransactionActivity(context :Context, order: Order): Intent{
            val intent = Intent(context, AddAddressTransactionActivity::class.java)
            intent.putExtra(NEW_ORDER, com.example.bettinalogistics.utils.Utils.g().getJsonFromObject(order))
            return intent
        }
    }

    override val viewModel: AddNewOrderViewModel by viewModel()

    override val binding: ActivityAddAddressTransactionBinding by lazy {
        ActivityAddAddressTransactionBinding.inflate(layoutInflater)
    }
    override fun initView() {

    }

    override fun initListener() {
        binding.btnTrack.setOnClickListener {
            val sSource = binding.edtSource.text.toString().trim()
            val sDestination = binding.edtDestination.text.toString().trim()

        }
    }

    override fun observeData() {

    }
}