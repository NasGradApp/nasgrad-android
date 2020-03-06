package com.nasgrad.issue

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nasgrad.api.model.Issue
import com.nasgrad.api.model.Location
import com.nasgrad.helpers.bitmapDescriptorFromVector
import com.nasgrad.nasGradApp.R
import kotlinx.android.synthetic.main.create_issue_bottom_navigation_layout.*
import kotlinx.android.synthetic.main.fragment_location.*
import timber.log.Timber
import java.io.IOException

class LocationFragment : Fragment(R.layout.fragment_location), OnMapReadyCallback,
    View.OnClickListener,
    GoogleMap.OnCameraIdleListener {

    private var map: GoogleMap? = null
    private lateinit var currentLocation: LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var issue: Issue

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        (activity as CreateIssueActivity).setActionBarTitle(getString(R.string.issue_location_title))

        tvPageIndicator.text = String.format(getString(R.string.create_issue_page_indicator), 1)

        issue = (activity as CreateIssueActivity).issue
        address.text = issue.address

        nextScreen.setOnClickListener(this)

        fusedLocationProviderClient = FusedLocationProviderClient(activity as CreateIssueActivity)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Timber.d("onMapReady")
        this.map = googleMap
        this.map?.setOnCameraIdleListener(this)
        if (issue.address == null) getCurrentLocation()
        updateUI()
    }

    override fun onCameraIdle() {
        Timber.d("onCameraIdle")
        val latLng = map?.cameraPosition?.target!!
        location = Location(latLng.latitude.toString(), latLng.longitude.toString())

        val geoCoder = Geocoder((activity as CreateIssueActivity))
        try {
            val addressList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addressList != null && addressList.size > 0) {
                val locality = addressList[0].getAddressLine(0)
                val country = addressList[0].countryName
                if (locality.isNotEmpty() && country.isNotEmpty()) {
                    address.text = locality
                    Timber.d("$locality, $country")
                }
            } else {
                address.text = resources.getString(R.string.unknown_location)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getCurrentLocation() {
        try {
            if ((activity as CreateIssueActivity).permissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation!!
                locationResult.addOnSuccessListener { location ->
                    if (location != null) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(currentLocation.latitude, currentLocation.longitude), 15f
                            )
                        )
                    } else {
                        Timber.e("Location is null")
                        Toast.makeText(
                            context,
                            "Nepoznata lokacija. Pokusajte ponovo.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.e(e, e.localizedMessage)
        }
    }

    private fun updateUI() {
        if (map != null) {
            try {
                if ((activity as CreateIssueActivity).permissionGranted) {
                    map?.isMyLocationEnabled = true
                    map?.uiSettings?.isMyLocationButtonEnabled = true
                } else {
                    map?.isMyLocationEnabled = false
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            } catch (e: SecurityException) {
                Timber.e(e, e.localizedMessage)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            nextScreen.id -> {
                // save location
                val issue = (activity as CreateIssueActivity).issue
                issue.location = location
                issue.address = address.text.toString()

                (activity as CreateIssueActivity).setFragment(R.id.mainContent, AddPhotoFragment())
            }
        }
    }
}
