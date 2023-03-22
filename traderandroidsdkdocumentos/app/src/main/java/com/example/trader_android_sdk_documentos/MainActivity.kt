package com.example.trader_android_sdk_documentos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.documentos.expose.NavigationExposeDoc
import com.example.trader_android_sdk_documentos.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iniComponent()
    }

    private fun iniComponent() {
        binding.sdkDocument.setOnClickListener {
            NavigationExposeDoc.openSdkDocumento(this)
        }
    }
}