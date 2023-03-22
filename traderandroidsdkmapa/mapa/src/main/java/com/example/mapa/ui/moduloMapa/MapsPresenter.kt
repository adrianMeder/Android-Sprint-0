package com.example.mapa.ui.moduloMapa

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class MapsPresenter(
    private val view: IMaps.View,
    private val context: Context,
    private val activity: Activity,
) : IMaps.Presenter {

    private val model = MapsModel(this, context)
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    //Check if the location is active
    fun checkGPSStatus() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            view.showEnableGPSDialog()
        }
    }

    fun onGPSDialogDismissed() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            view.showEnableGPSDialog()
        } else {
            view.hideEnableGPSDialog()
        }
    }

    //check if location permission is active
    fun onLocationPermissionButtonClicked() {

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // If the app doesn't have the permission, ask the user for it
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)

        } else {
            // If the app already has the permission, perform the desired action
            view.doSomethingWithLocation()
        }
    }

    // Check if the permission request is for the location and if the permission was granted
    fun onLocationPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // If permission was granted, perform the desired action
            loadMap()
            view.doSomethingWithLocation()
        } else {
            // If permission was not granted, display an error message to the user
            view.showLocationPermissionDeniedError()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    //Show map
    fun loadMap() {
        try {
            // Initialize the map and display it in the view
            view.showMap()
        } catch (e: Exception) {
            // If there are any errors, print an error message to the view
            view.showError()
        }
    }

    override fun reedUbication() {
        model.reedUbication()
    }

    override fun setUbication(latitud: Double, longitud: Double, infoUbicacion: String) {
        view.setUbication(latitud, longitud, infoUbicacion)
    }
}