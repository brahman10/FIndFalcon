package com.traktez.findfalcon.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.traktez.findfalcon.R
import com.traktez.findfalcon.data.entity.FindFalconeRequest
import com.traktez.findfalcon.data.entity.PlanetEntity
import com.traktez.findfalcon.data.entity.State
import com.traktez.findfalcon.databinding.FragmentFindFalconBinding
import com.traktez.findfalcon.databinding.ShipSelectViewBinding
import com.traktez.findfalcon.ui.adapters.PlanetSelectionAdapter
import com.traktez.findfalcon.utils.NetworkUtil
import com.traktez.findfalcon.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** we can create BaseFragment as well to do some common work
we can use to inflate views directly in base fragment and so on.
I'm not using base fragment in this project as its just two fragment project
 */

@AndroidEntryPoint
class FindFalconFragment : Fragment(), PlanetSelectionAdapter.PlanetChoiceHandler {

    private lateinit var _binding: FragmentFindFalconBinding
    private val binding get() = _binding
    private val viewModel by viewModels<FindFalconViewModel>()
    private lateinit var dialog: Dialog

    @Inject
    lateinit var networkUtil: NetworkUtil
    private val dialogBinding by lazy { ShipSelectViewBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFindFalconBinding.inflate(inflater, container, false)
        initListeners()
        initObservers()
        getData()
        return binding.root
    }

    private fun getData() {
        if (networkUtil.hasNetwork()) {
            viewModel.getData()
        } else {
            // show no network screen here.
        }
    }

    private fun initListeners() {
        binding.lvChoices.adapter = viewModel.planetSelectionAdapter
        //initialise dialog
        dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.setContentView(dialogBinding.root)
            this.window?.setLayout(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnBegin.setOnClickListener {
            val itemSelected = dialogBinding.spnShip.selectedItem
            var speed = 0
            val planetName = viewModel.currentplanet.planetName
            val timeTaken = viewModel.timeTaken
            var oldTime = 0
            var newTime = 0
            viewModel.vehicleList.filter {
                it.vehicleName == itemSelected
            }.map { ship ->
                if (viewModel.hashMap.containsKey(planetName)) {
                    if (viewModel.hashMap[planetName]!!.vehicleName != ship.vehicleName) {
                        viewModel.vehicleList.first { it == viewModel.hashMap[planetName] }.apply {
                            this.let {
                                it.totalNumber++
                            }
                        }
                        val oldSpeed = viewModel.hashMap[planetName]?.speed ?: 0
                        if (oldSpeed > 0) {
                            oldTime = viewModel.currentplanet.distance / oldSpeed
                        }
                        ship.totalNumber--
                        speed = ship.speed
                    }
                } else {
                    ship.totalNumber--
                    speed = ship.speed
                }
                viewModel.hashMap[planetName] = ship
            }.also {
                if (speed != 0) {
                    newTime = (viewModel.currentplanet.distance / speed)
                    val itemIndex = viewModel.planetList.indexOf(viewModel.currentplanet)
                    viewModel.currentplanet.planetSearching = true
                    viewModel.currentplanet.vehicleName = itemSelected as String
                    viewModel.planetSelectionAdapter.planetList[itemIndex] = viewModel.currentplanet
                    if (viewModel.hashMap.size == 4) {
                        binding.btnFindFalcone.visibility = View.VISIBLE
                        viewModel.planetSelectionAdapter.isSearchQuotaFull = true
                    } else {
                        binding.btnFindFalcone.visibility = View.GONE
                    }
                    viewModel.planetSelectionAdapter.notifyDataSetChanged()
                }
            }

            binding.tvTimeTaken.text = (timeTaken + newTime - oldTime).toString()
            dialog.dismiss()
        }
        binding.btnFindFalcone.setOnClickListener {
            findFalcone()
        }
    }

    private fun initObservers() {
        viewModel.planetListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    viewModel.planetSelectionAdapter.apply {
                        viewModel.planetList = it.data
                        this.planetChoiceHandler = this@FindFalconFragment
                        planetList = it.data
                        notifyDataSetChanged()
                    }
                }

                is State.Loading -> {

                }

                is State.Error -> {
                    toast(requireContext(), it.errorMessage ?: "Something went wrong")

                }
            }
        }

        viewModel.vehicleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    viewModel.vehicleList = it.data
                }

                is State.Loading -> {

                }

                is State.Error -> {
                    toast(requireContext(), it.errorMessage ?: "Something went wrong")

                }
            }
        }

        viewModel.tokenResponse.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    val requestBody = FindFalconeRequest(it.data.token, ArrayList<String>().apply {
                        this.addAll(viewModel.hashMap.keys)
                    }, ArrayList<String>().apply {
                        viewModel.hashMap.keys.forEach {
                            viewModel.hashMap[it]?.let {
                                this.add(it.vehicleName)
                            }
                        }
                    })
                    viewModel.findFalcone(requestBody)
                }

                is State.Loading -> {

                }

                is State.Error -> {
                    toast(requireContext(), it.errorMessage ?: "Something went wrong")
                }
            }
        }

        viewModel.falconeResponse.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    if (it.data.status != "success") {
                        toast(requireContext(), "Not found")
                    } else {
                        findNavController().navigate(
                            FindFalconFragmentDirections.toResult(
                                viewModel.timeTaken.toString(),
                                it.data.planet_name
                            )
                        )
                    }
                }

                is State.Loading -> {

                }

                is State.Error -> {
                    toast(requireContext(), it.errorMessage ?: "Something went wrong")
                }
            }
        }
    }


    override fun selectedPlanet(planet: PlanetEntity) {
        viewModel.currentplanet = planet
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            updateVehicleChoices()
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spnShip.adapter = arrayAdapter
        dialog.show()
    }

    override fun deselectedPlanet(planet: PlanetEntity) {
        val chosenVehicle = viewModel.hashMap[planet.vehicleName]
        chosenVehicle!!.totalNumber++
        val i: Int = viewModel.vehicleList.indexOf(chosenVehicle)
        if (i != -1) {
            viewModel.vehicleList[i] = chosenVehicle
        }
        val timeTaken = (planet.distance / chosenVehicle.speed)
        binding.tvTimeTaken.text = (viewModel.timeTaken - timeTaken).toString()
        viewModel.hashMap.remove(planet.planetName)
        binding.btnFindFalcone.visibility = View.GONE
    }

    private fun updateVehicleChoices(): ArrayList<String> {
        val vehicleChoices = ArrayList<String>()
        vehicleChoices.add(getString(R.string.select_space_ship))
        viewModel.vehicleList.forEach { ship ->
            if (viewModel.currentplanet.distance <= ship.maxDistance) {
                if (ship.totalNumber > 0) {
                    vehicleChoices.add(ship.vehicleName)
                }
            }
        }
        return vehicleChoices
    }

    override fun findFalcone() {
        viewModel.getToken()
    }

    override fun sendNoMoreResAlert() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
        builder.setTitle("No More Resources Available")
            .setMessage("Uncheck another item to search this planet")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

}