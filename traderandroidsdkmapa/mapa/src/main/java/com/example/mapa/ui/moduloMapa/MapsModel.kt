package com.example.mapa.ui.moduloMapa

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlin.math.log

class MapsModel (private val presenter: IMaps.Presenter,private val context: Context):IMaps.Model {

    var db = FirebaseFirestore.getInstance()
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var infoUbicacion: String =""

    // Called and Get values from firebase
   override fun reedUbication() {
        var datos = ""
        db.collection("cordenadas")
            .get()
            .addOnSuccessListener { resultado ->
                for (documento in resultado) {
                    datos += "${documento.id} : ${documento.data}\n"
                    if (documento != null) {
                        val data = documento.data // obtenemos los datos del documento
                        if (data != null) {
                            val geoPoint = data["ubicacion1"] as? GeoPoint
                            if (geoPoint != null) {
                                latitud = geoPoint.latitude
                                longitud = geoPoint.longitude
                                infoUbicacion= (data["infoUbicacion"]as? String).toString()
                                presenter.setUbication(latitud,longitud,infoUbicacion)
                                // Do something with the latitude and longitude values
                            } else {
                                // The field "location" is not a valid GeoPoint object
                            }
                        } else {
                            // The document has no data
                        }
                    } else {
                        // Could not get the document
                    }
                }
            }
            .addOnFailureListener { exeption ->
            Toast.makeText(context, "no se pudo conectar ${exeption.message}", Toast.LENGTH_SHORT).show()
              
            }
    }
}