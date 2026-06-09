package com.kevinchambi.shipmentmonitor.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.MapStyleOptions
import com.kevinchambi.shipmentmonitor.R
import androidx.navigation.NavOptions
import com.kevinchambi.shipmentmonitor.databinding.FragmentMapBinding
import com.kevinchambi.shipmentmonitor.data.model.Vehicle
import com.kevinchambi.shipmentmonitor.utils.MarkerUtils
import com.kevinchambi.shipmentmonitor.utils.SessionManager

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MapViewModel
    private lateinit var sessionManager: SessionManager
    private var googleMap: GoogleMap? = null
    private var mapView: MapView? = null
    companion object {
        private const val DEFAULT_LAT = -12.0464
        private const val DEFAULT_LNG = -77.0428
        private const val DEFAULT_ZOOM = 16f
        private const val MARKER_CLICK_ZOOM = 18f
    }

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
        setupMap(savedInstanceState)
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

    private fun setupMap(savedInstanceState: Bundle?) {
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Muted map style
        try {
            val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            map.setMapStyle(style)
        } catch (_: Exception) {}

        // No my location layer — no blue dot or gray circle
        map.isMyLocationEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isZoomControlsEnabled = true

        // Start in Lima, Peru
        val lima = LatLng(DEFAULT_LAT, DEFAULT_LNG)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, DEFAULT_ZOOM))

        // Zoom to marker on tap
        map.setOnMarkerClickListener { marker ->
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(marker.position, MARKER_CLICK_ZOOM),
                400,
                null
            )
            true // consume the event, no info window
        }

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
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun displayVehicles(map: GoogleMap, vehicles: List<Vehicle>) {
        map.clear()
        if (vehicles.isEmpty()) {
            val lima = LatLng(DEFAULT_LAT, DEFAULT_LNG)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(lima, DEFAULT_ZOOM))
            return
        }

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
                    .icon(markerIcon)
                    .anchor(MarkerUtils.getAnchorU(), MarkerUtils.getAnchorV())
            )
            boundsBuilder.include(position)
        }

        val bounds = boundsBuilder.build()
        val padding = 150
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        mapView?.onDestroy()
        googleMap = null
        mapView = null
        _binding = null
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}