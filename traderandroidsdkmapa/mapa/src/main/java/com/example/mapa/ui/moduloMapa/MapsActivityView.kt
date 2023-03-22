package com.example.mapa.ui.moduloMapa

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapa.R
import com.example.mapa.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MapsActivityView : AppCompatActivity(), IMaps.View {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var presenter: MapsPresenter
    val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Modify bar
        modifyColorBar()
        presenter = MapsPresenter(this, this, this)

        // Check the gps status
        presenter.checkGPSStatus()

        presenter.onLocationPermissionButtonClicked()

        // Load the map
        presenter.loadMap()

        // Read the location taken from firebase
        presenter.reedUbication()

        iniComponent()
    }

    private fun iniComponent() {
        binding.toolbarBack.idBackOperator.setOnClickListener {
            finish()
        }
    }


    // Show GPS activation dialog
    override fun showEnableGPSDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.enable_gps_title))
            .setMessage(getString(R.string.enable_gps_message))
            .setPositiveButton(getString(R.string.enable_gps_positive_button)) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(R.string.enable_gps_negative_button) { _, _ ->
            }
            .setCancelable(false)
            .show()
        // Call the presenter's onGPSDialogDismissed() method when the dialog is closed
        dialog.setOnDismissListener {
            presenter.onGPSDialogDismissed()
        }
    }

    //Modify bar
    private fun modifyColorBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Cambia el color de la barra de notificaciones a rojo
            window.statusBarColor = ContextCompat.getColor(this, R.color.teal_200)

            // Aplica los cambios
            window.apply {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }

    override fun hideEnableGPSDialog() {
        Toast.makeText(this, getString(R.string.enable_gps_activate_msg), Toast.LENGTH_SHORT).show()
    }

    override fun requestLocationPermission() {
    }

    // permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // Send the result of the location permission request to the presenter
        presenter.onLocationPermissionResult(requestCode, permissions, grantResults)
    }


    // Perform the desired action with the location
    override fun doSomethingWithLocation() {
    }

    // Display an error message to the user indicating that location permission is needed to perform the desired action
    override fun showLocationPermissionDeniedError() {
        finish()
        Toast.makeText(this, getString(R.string.enable_gps_error_msg), Toast.LENGTH_SHORT).show()
    }


    //Show and config map
    override fun showMap() {
        // Get the map from the layout and initialize it
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            mMap = googleMap
            // Set map options
            googleMap.uiSettings.isZoomControlsEnabled = true
            // Show my location on the map
            val COLOR_MARCADOR_GREEN =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.addMarker(MarkerOptions().position(currentLatLng)
                        .icon(COLOR_MARCADOR_GREEN).title("Mi ubicación"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
            // Add market on the map
        }
    }

    // Display an error message in the view
    override fun showError() {
        Toast.makeText(this, getString(R.string.error_upload_map), Toast.LENGTH_SHORT).show()
    }

    // Show the data of the location obtained in firebase
    override fun setUbication(latitud: Double, longitud: Double, infoUbi: String) {
        val COLOR_MARCADOR_RED =
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        var obteniedFireBase = LatLng(latitud, longitud)
        mMap.addMarker(MarkerOptions().position(obteniedFireBase).icon(COLOR_MARCADOR_RED)
            .title(infoUbi))
        binding.infoLocation.latitud.setText("Latitud : " + latitud)
        binding.infoLocation.longitud.setText("Longitud : " + longitud)
        binding.infoLocation.infoUbication.setText("Info : " + infoUbi)

    }
}