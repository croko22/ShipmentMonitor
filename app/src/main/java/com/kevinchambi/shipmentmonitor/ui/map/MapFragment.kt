package com.kevinchambi.shipmentmonitor.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.kevinchambi.shipmentmonitor.R
import androidx.navigation.NavOptions
import com.kevinchambi.shipmentmonitor.databinding.FragmentMapBinding
import com.kevinchambi.shipmentmonitor.utils.MarkerUtils
import com.kevinchambi.shipmentmonitor.utils.SessionManager

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MapViewModel
    private lateinit var sessionManager: SessionManager
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        setupToolbar()
        setupMap()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    sessionManager.clearSession()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .setLaunchSingleTop(true)
                        .build()
                    findNavController().navigate(R.id.loginFragment, null, navOptions)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
            ?: return
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
        viewModel.loadVehicles()
    }

    private fun setupObservers() {
        viewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            googleMap?.let { map ->
                displayVehicles(map, vehicles)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayVehicles(map: GoogleMap, vehicles: List<com.kevinchambi.shipmentmonitor.data.model.Vehicle>) {
        map.clear()
        if (vehicles.isEmpty()) return

        val boundsBuilder = LatLngBounds.Builder()
        for (vehicle in vehicles) {
            val position = LatLng(vehicle.latitude, vehicle.longitude)
            val markerIcon = MarkerUtils.createCustomMarker(
                requireContext(),
                vehicle.plate,
                vehicle.speed,
                vehicle.status,
                vehicle.angle
            )

            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(vehicle.plate)
                    .snippet(vehicle.speed)
                    .icon(markerIcon)
            )
            boundsBuilder.include(position)
        }

        val bounds = boundsBuilder.build()
        val padding = 100
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleMap = null
        _binding = null
    }
}
