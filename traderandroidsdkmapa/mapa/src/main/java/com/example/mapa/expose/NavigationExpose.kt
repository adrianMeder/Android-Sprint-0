package com.example.mapa.expose

import android.content.Context
import android.content.Intent
import com.example.mapa.ui.moduloMapa.MapsActivityView

object NavigationExpose {
    @JvmStatic
    fun openSdkMap(context: Context) {
        val intent = Intent(context, MapsActivityView::class.java)
        context.startActivity(intent)
    }
}