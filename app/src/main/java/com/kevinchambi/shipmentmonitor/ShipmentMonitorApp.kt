package com.kevinchambi.shipmentmonitor

import android.app.Application
import com.kevinchambi.shipmentmonitor.data.network.RetrofitClient
import com.kevinchambi.shipmentmonitor.utils.SessionManager

class ShipmentMonitorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)
    }
}
