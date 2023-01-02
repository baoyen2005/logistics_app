package com.example.bettinalogistics.ui.activity.addorder

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityOrderBinding
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.ui.activity.add_new_order.AddAddressTransactionActivity
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductActivity
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductActivity.Companion.ADD_NEW_PRODUCT
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductActivity.Companion.IS_EDIT
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductActivity.Companion.PRODUCT_EDIT
import com.example.bettinalogistics.ui.fragment.bottom_sheet.SupplierCompanyInfoBottomSheet
import com.example.bettinalogistics.utils.Utils
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
        viewModel.getAllProduct()
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
            if (viewModel.productList.isEmpty()) {
                confirm.newBuild().setNotice(getString(R.string.str_product_empty)).addButtonAgree {
                    intent.putExtra(IS_EDIT, false)
                    val intent = Intent(this, AddNewProductActivity::class.java)
                    resultLauncher.launch(intent)
                }
            } else {
                val companyInfo = SupplierCompanyInfoBottomSheet()
                companyInfo.onConfirmListener = { company ->
                    viewModel.supplierCompany = company
                    resultLauncherAddAddress.launch(
                        AddAddressTransactionActivity.startAddAddressTransactionActivity(
                            this,
                            viewModel.productList,
                            company
                        )
                    )
                }
                companyInfo.show(supportFragmentManager, "aaaaaaa")
            }
        }
        addOrderAdapter.itemExpandOnClick = { product, position, view ->
            val popupMenu: PopupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.product_item_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit_product -> {
                        viewModel.isEdit = true
                        viewModel.beforeEditProductPosition = position
                        val intent = Intent(this, AddNewProductActivity::class.java)
                        intent.putExtra(IS_EDIT, true)
                        intent.putExtra(PRODUCT_EDIT, Utils.g().getJsonFromObject(product))
                        resultLauncher.launch(intent)
                    }

                    R.id.action_delete_product -> {
                        if (product != null) {
                            showLoading()
                            viewModel.productList.remove(product)
                            addOrderAdapter.resetOrderList(viewModel.productList)
                            viewModel.deleteProduct(product)
                        }
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun observeData() {
        viewModel.getAllProductLiveData.observe(this) {
            hiddenLoading()
            if (it.isNullOrEmpty()) {
                confirm.newBuild().setNotice(getString(R.string.str_product_empty)).addButtonAgree {
                    val intent = Intent(this, AddNewProductActivity::class.java)
                    resultLauncher.launch(intent)
                }
            } else {
                viewModel.productList.clear()
                viewModel.productList.addAll(it)
                addOrderAdapter.resetOrderList(viewModel.productList)
            }
        }
        viewModel.deleteProductLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.newBuild().setNotice(getString(R.string.str_delete_success))
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_delete_fail))
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val product = Utils.g().getObjectFromJson(
                        result.data?.getStringExtra(ADD_NEW_PRODUCT).toString(),
                        Product::class.java
                    )
                    if (viewModel.isEdit) {
                        product?.let {
                            viewModel.productList.set(viewModel.beforeEditProductPosition,
                                it
                            )
                        }

                    } else {
                        product?.let { viewModel.productList.add(it) }
                    }
                    addOrderAdapter.resetOrderList(viewModel.productList)
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