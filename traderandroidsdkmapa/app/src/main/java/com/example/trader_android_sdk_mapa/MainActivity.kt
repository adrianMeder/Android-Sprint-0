package com.example.trader_android_sdk_mapa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mapa.expose.NavigationExpose
import com.example.trader_android_sdk_mapa.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iniComponent()
    }

    private fun iniComponent() {
        binding.sdkMap.setOnClickListener {
            NavigationExpose.openSdkMap(this)
        }
    }
}