package com.example.bettinalogistics.ui.activity.addorder

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityOrderBinding
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.ui.activity.add_new_order.AddAddressTransactionActivity
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductActivity
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductActivity.Companion.IS_EDIT
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductActivity.Companion.PRODUCT_EDIT
import com.example.bettinalogistics.utils.Utils
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderActivity : BaseActivity() {
    private lateinit var addOrderAdapter: AddOrderAdapter

    override val viewModel: OrderViewModel by viewModel()

    override val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        addOrderAdapter = AddOrderAdapter()
        binding.layoutHeaderOrder.tvHeaderTitle.text = getString(R.string.header_product)
        binding.rvOrderList.adapter = addOrderAdapter
        showLoading()
        viewModel.initDatabase(this)
        viewModel.getAllAddedProduct()
    }

    override fun initListener() {
        binding.layoutHeaderOrder.ivHeaderBack.setOnClickListener {
            if (viewModel.productList.isEmpty()) {
                finish()
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_confirm_order))
                    .addButtonAgree {
                        confirm.dismiss()
                    }.addButtonCancel(getString(com.example.baseapp.R.string.cancel)) {
                        confirm.dismiss()
                        finish()
                    }
            }
        }
        binding.btnAddOrderProduct.setOnClickListener {
            intent.putExtra(IS_EDIT, false)
            val intent = Intent(this, AddNewProductActivity::class.java)
            resultLauncher.launch(intent)
        }
        binding.btnOrderContinued.setOnClickListener {
//            showLoading()
            //        viewModel.addOrder(order)
            if (viewModel.productList.isNullOrEmpty()) {
                confirm.newBuild().setNotice(getString(R.string.str_product_empty)).addButtonAgree {
                    intent.putExtra(IS_EDIT, false)
                    val intent = Intent(this, AddNewProductActivity::class.java)
                    resultLauncher.launch(intent)
                }
            } else {
                val addedProduct = AddedProduct(Utils.g().getJsonFromObject(viewModel.productList))
                viewModel.deleteAllAddedProduct()
                viewModel.insertAddedProduct(addedProduct)
                resultLauncherAddAddress.launch(
                    AddAddressTransactionActivity.startAddAddressTransactionActivity(
                        this,
                        viewModel.productList
                    )
                )
            }
        }
        addOrderAdapter.itemExpandOnClick = { product, view ->
            val popupMenu: PopupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.product_item_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit_product -> {
                        val intent = Intent(this, AddNewProductActivity::class.java)
                        intent.putExtra(IS_EDIT, true)
                        intent.putExtra(PRODUCT_EDIT, Utils.g().getJsonFromObject(product))
                        resultLauncher.launch(intent)
                    }

                    R.id.action_delete_product -> {
                        if (product != null) {
                            viewModel.deleteProduct(product)
                            viewModel.getAllProduct()
                        }
                    }
                }
                true
            })
            popupMenu.show()
        }
    }

    override fun observeData() {
        viewModel.getAllAddedProductLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                confirm.newBuild().setNotice(getString(R.string.str_product_empty)).addButtonAgree {
                    val intent = Intent(this, AddNewProductActivity::class.java)
                    resultLauncher.launch(intent)
                }
            } else {
                it.let {
                    it.forEach {
                        val listProduct: List<Product> = Utils.g().provideGson()
                            .fromJson(it.productList, object :
                                TypeToken<List<Product>>() {}.type) ?: listOf()
                        viewModel.productList.addAll(listProduct)
                    }
                }
            }
        }

        viewModel.getAllProductLiveData.observe(this)
        {
            hiddenLoading()
            it?.let { viewModel.productList.addAll(it) }
            addOrderAdapter.resetOrderList(viewModel.productList)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    showLoading()
                    viewModel.getAllProduct()
                    //    val product =  Utils.g().getObjectFromJson(result.data?.getStringExtra(ADD_NEW_PRODUCT).toString(),Product::class.java)
//                    product?.let { viewModel.productList.add(it) }
//                    addOrderAdapter.resetOrderList(viewModel.productList)
                }
            }
        }
    private var resultLauncherAddAddress =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    finish()
                }
            }
        }
}