package com.example.bettinalogistics.ui.activity.gg_map

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseapp.dialog.LoadingDialog
import com.example.bettinalogistics.databinding.FragmentSavedPlaceBinding
import com.example.bettinalogistics.interfaces.SaveLocationInterface
import com.example.bettinalogistics.model.SavedPlaceModel
import com.example.bettinalogistics.ui.activity.gg_map.adapter.SavedPlaceAdapter
import com.example.bettinalogistics.utils.State
import com.google.android.material.snackbar.Snackbar

class SavedPlaceFragment : Fragment(), SaveLocationInterface {

    private lateinit var binding: FragmentSavedPlaceBinding
    private lateinit var savedPlaceModelList: ArrayList<SavedPlaceModel>
    private lateinit var loadingDialog: LoadingDialog
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var placeAdapter: SavedPlaceAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedPlaceBinding.inflate(inflater, container, false)

        savedPlaceModelList = ArrayList()
        loadingDialog = LoadingDialog(requireActivity())
        placeAdapter = SavedPlaceAdapter(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Saved Places"
        binding.savedRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            adapter = placeAdapter
        }

        getSavedPlaces()

    }

    override fun onLocationClick(savedPlaceModel: SavedPlaceModel) {
        val intent = Intent(requireContext(), DirectionActivity::class.java)
        intent.putExtra("placeId", savedPlaceModel.placeId)
        intent.putExtra("lat", savedPlaceModel.lat)
        intent.putExtra("lng", savedPlaceModel.lng)

        startActivity(intent)
    }

    private fun getSavedPlaces() {
        lifecycleScope.launchWhenStarted {
            locationViewModel.getUserLocations().collect {
                when (it) {
                    is State.Loading -> {
                        if (it.flag == true) {
                            Log.d("TAG", "getSavedPlaces: called")
                            loadingDialog.show()
                        }
                    }

                    is State.Success -> {
                        loadingDialog.hidden()
                        savedPlaceModelList = it.data as ArrayList<SavedPlaceModel>
                        Toast.makeText(
                            requireContext(),
                            "${savedPlaceModelList.size}",
                            Toast.LENGTH_SHORT
                        ).show()

                        placeAdapter.submitList(savedPlaceModelList)

                    }
                    is State.Failed -> {
                        loadingDialog.hidden()
                        Snackbar.make(
                            binding.root, it.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


}