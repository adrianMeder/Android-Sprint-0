package com.example.mapa.ui.moduloMapa

import android.media.effect.EffectContext

interface IMaps {

    interface View{
    //Check if the location is active
    fun showEnableGPSDialog()
    fun hideEnableGPSDialog()

    //check if location permission is active
    fun doSomethingWithLocation()
    fun showLocationPermissionDeniedError()
    fun requestLocationPermission()

    //show map
    fun showMap()
    fun showError()
    fun setUbication(latitud: Double, longitud: Double,infoUbicacion: String)
    }
    interface Presenter{
        fun reedUbication()
        fun setUbication(latitud: Double, longitud: Double, infoUbicacion: String)
    }

    interface Model{
        fun reedUbication()
    }
}