package com.example.genericproyectsdk


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.example.documentos.R

import com.example.documentos.expose.NavigationExposeDoc
import com.example.genericproyectsdk.databinding.ActivityMainBinding
import com.example.mapa.expose.NavigationExpose


open class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Modify bar
        modifiColorBar()
        
        iniComponent()
    }

    private fun iniComponent() {
        binding.sdkMap.setOnClickListener {
        NavigationExpose.openSdkMap(this)
        }

        binding.sdkDocument.setOnClickListener {
          //  NavigationExpose.openSdkDocumento(this)
            NavigationExposeDoc.openSdkDocumento(this)
        }
    }
    //Modify bar
    private fun modifiColorBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the color of the notification bar to white
            window.statusBarColor = ContextCompat.getColor(this, R.color.White)

            // Apply the changes
            window.apply {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }
}