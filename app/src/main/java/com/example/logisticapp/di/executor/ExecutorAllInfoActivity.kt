package com.example.logisticapp.di.executor

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.logisticapp.R
import com.example.logisticapp.domain.models.OrderRecycler
import com.example.logisticapp.domain.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class ExecutorAllInfoActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    private lateinit var editTextPickupLocation: EditText
    private lateinit var editTextDeliveryLocation: EditText
    private lateinit var editTextInfProduct: EditText
    private lateinit var statusText: TextView
    private lateinit var order: OrderRecycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_executor_all_info)

        order = intent.getSerializableExtra("order") as OrderRecycler

        mapView = findViewById(R.id.mapview)

        editTextPickupLocation = findViewById(R.id.editTextPickupLocation)
        editTextDeliveryLocation = findViewById(R.id.editTextDeliveryLocation)
        editTextInfProduct = findViewById(R.id.editTextInfProduct)
        statusText = findViewById(R.id.statusText)

        val imageProvider = ImageProvider.fromResource(this, R.drawable.placemark_icon)
        val placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(order.start!!.lat, order.start!!.lon)
            setIcon(imageProvider)
        }
        val placemark2 = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(order.finish!!.lat, order.finish!!.lon)
            setIcon(imageProvider)
        }

        prepareDataView()

    }

    private fun prepareDataView() {

        editTextPickupLocation.setText(order.nameStart)
        editTextDeliveryLocation.setText(order.nameFinish)
        editTextInfProduct.setText(order.descProduct)
        statusText.setText(order.status)

    }
}