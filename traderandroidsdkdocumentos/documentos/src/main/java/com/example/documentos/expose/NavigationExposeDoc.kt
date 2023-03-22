package com.example.documentos.expose

import android.content.Context
import android.content.Intent
import com.example.documentos.ui.DocumentActivityView

object NavigationExposeDoc {
    @JvmStatic
    fun openSdkDocumento(context: Context) {
        val intent = Intent(context, DocumentActivityView::class.java)
        context.startActivity(intent)
    }
}