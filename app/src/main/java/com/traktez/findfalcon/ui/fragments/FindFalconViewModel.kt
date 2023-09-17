package com.traktez.findfalcon.ui.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.traktez.findfalcon.data.api.CoroutineApiService
import com.traktez.findfalcon.data.entity.FindFalconeRequest
import com.traktez.findfalcon.data.entity.FindFalconeResponse
import com.traktez.findfalcon.data.entity.PlanetEntity
import com.traktez.findfalcon.data.entity.State
import com.traktez.findfalcon.data.entity.VehicleEntity
import com.traktez.findfalcon.ui.adapters.PlanetSelectionAdapter
import com.traktez.findfalcon.utils.requestAwait
import com.traktez.findfalconeproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindFalconViewModel @Inject constructor(
    private val apiService: CoroutineApiService
) : BaseViewModel() {
    private val _planetListLiveData = MutableLiveData<State<ArrayList<PlanetEntity>>>()
    val planetListLiveData get() = _planetListLiveData

    private val _vehicleLiveData = MutableLiveData<State<ArrayList<VehicleEntity>>>()
    val vehicleLiveData get() = _vehicleLiveData

    private val _tokenResponse = MutableLiveData<State<FindFalconeRequest>>()
    val tokenResponse get() = _tokenResponse

    private val _falconeResponse = MutableLiveData<State<FindFalconeResponse>>()
    val falconeResponse get() = _falconeResponse

    @Inject
    lateinit var planetSelectionAdapter: PlanetSelectionAdapter

    var vehicleList = arrayListOf<VehicleEntity>()
    var planetList = arrayListOf<PlanetEntity>()
    val hashMap = HashMap<String, VehicleEntity>()
    lateinit var currentplanet: PlanetEntity
    var timeTaken = 0


    /**
     * api call for planets and vehicles are calling asynchronously
     * both apis are independent to each other
     */
    fun getData() {
        viewModelScope.async {
            _planetListLiveData.value = requestAwait { apiService.getPlanetsAsync() }.toState()
        }

        viewModelScope.async {
            _vehicleLiveData.value = requestAwait { apiService.getVehiclesAsync() }.toState()
        }
    }

    /**
     * api call to get token to call next api for finding falcon
     */

    fun getToken() {
        viewModelScope.launch {
            _tokenResponse.value = requestAwait { apiService.getTokenAsync() }.toState()
        }
    }

    /**
     * api call to find falcon
     */

    fun findFalcone(request: FindFalconeRequest) {
        viewModelScope.launch {
            _falconeResponse.value = requestAwait { apiService.findFalconeAsync(request) }.toState()
        }
    }

}