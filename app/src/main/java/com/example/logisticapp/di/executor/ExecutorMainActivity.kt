package com.example.logisticapp.di.executor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticapp.R
import com.example.logisticapp.di.executor.recyclerview.RecyclerViewOrderAdapterExecutor
import com.example.logisticapp.di.logistics.LogisticMainActivity
import com.example.logisticapp.di.logistics.LogisticOrderActivity
import com.example.logisticapp.di.logistics.recyclerview.RecyclerViewOrderAdapter
import com.example.logisticapp.domain.models.Order
import com.example.logisticapp.domain.models.OrderRecycler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ExecutorMainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var recyclerViewOrderAdapter: RecyclerViewOrderAdapterExecutor? = null
    private var orderList = mutableListOf<OrderRecycler>()

    private lateinit var uidOrderText: TextView
    private lateinit var startPointText: TextView
    private lateinit var finishPointText: TextView
    private lateinit var historyBtn: FloatingActionButton

    val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://logisticapp-5ba6a-default-rtdb.europe-west1.firebasedatabase.app").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_executor_main)

        orderList = ArrayList()

        recyclerView = findViewById(R.id.orderRecyclerView)
        recyclerViewOrderAdapter = RecyclerViewOrderAdapterExecutor(this@ExecutorMainActivity, orderList)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewOrderAdapter

        uidOrderText = findViewById(R.id.uidOrderText)
        startPointText = findViewById(R.id.startPointText)
        finishPointText = findViewById(R.id.finishPointText)

        historyBtn = findViewById(R.id.historyBtn)

        historyBtn.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ExecutorMainActivity, ExecutorHistroyActivity::class.java))
        })

        prepareOrderList()
    }

    private fun prepareData() {
        if (orderList.size != 0) {
            uidOrderText.setText(orderList[0].UID)
            startPointText.setText(orderList[0].nameStart)
            finishPointText.setText(orderList[0].nameFinish)

            if (orderList[0].status != "Выполняется") {
                val myRef = databaseReference.child("orders").child(orderList[0].UID!!)

                orderList[0].status = "Выполняется"

                val updatedData = HashMap<String, Any>()
                updatedData["status"] = orderList[0].status!!

                myRef.updateChildren(updatedData)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener {
                        // Обработка ошибки
                    }
            }
        }
    }

    private fun prepareOrderList() {
        val myRef = databaseReference.child("orders")

        val queryExecutor: Query = myRef.orderByChild("executor").equalTo(FirebaseAuth.getInstance().currentUser?.uid)

        queryExecutor.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val filteredOrders = mutableListOf<OrderRecycler>()

                for (userSnapshot in dataSnapshot.children) {
                    val order = userSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        val orderId = userSnapshot.key ?: "Unknown"

                        var orderRecycler : OrderRecycler = OrderRecycler(order.start, order.finish,
                            order.nameStart, order.nameFinish, order.descProduct, order.executor, order.status,
                            orderId)

                        filteredOrders.add(orderRecycler)
                    }
                }

                val ordersInProgress = filteredOrders.filter { it.status == "Готов к исполнению" || it.status == "Выполняется"}
                orderList.addAll(ordersInProgress)


                recyclerViewOrderAdapter!!.notifyDataSetChanged()

                prepareData()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибки запроса
                println("Ошибка получения пользователей: ${databaseError.message}")
            }
        })
    }
}