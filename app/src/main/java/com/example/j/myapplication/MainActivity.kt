package com.example.j.myapplication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    lateinit var wifiManager: WifiManager
    var size: Int = 0
    var array: ArrayList<ScanResult> = ArrayList<ScanResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
        else {
            wifiManager.startScan()
        }

        val receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1?.action) {
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                        array = wifiManager.scanResults as ArrayList<ScanResult>

                        var filtered: ArrayList<ScanResult> = filter(array, wifiManager.configuredNetworks)
                        textview1.setText("")
                        for(i in sort(filtered)) {
                            textview1.append("${i.SSID}, ${i.level}, ${i.frequency} \n")
                        }
                        compare(sort(filtered))
                    }
                    WifiManager.NETWORK_STATE_CHANGED_ACTION -> sendBroadcast(Intent("wifi.ON_NETWORK_STATE_CHANGED"))
                }
            }
        }

        var iFilter: IntentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        iFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        var registerReceiver = registerReceiver(receiver, iFilter)

        button1.setOnClickListener() {
            wifiManager.startScan()
        }
    }

    // Filtering that scanned wifi is configured
    fun filter(scanned: ArrayList<ScanResult>, configured: List<WifiConfiguration>): ArrayList<ScanResult> {
        var list: ArrayList<ScanResult> = ArrayList()
        for(i in scanned) {
            configured.filter { Regex("\"").replace(it.SSID, "") == i.SSID }.map { list.add(i) }
        }

        return list
    }

    // Sorted by level
    fun sort(scanned: ArrayList<ScanResult>): ArrayList<ScanResult> {
        return ArrayList(scanned.sortedByDescending { it.level })
    }

    fun compare(filtered: List<ScanResult>) {
        // Get connected wifi
        var wifiInfo: WifiInfo = wifiManager.connectionInfo
        var ssid: String = Regex("\"").replace(wifiInfo.ssid, "")

        // Compare filteredList and connected wifi
        //
        for(i in filtered) {
            if(ssid == i.SSID) {
                i("a", "${i.SSID} is connected")
            }
            else {
                if (WifiManager.calculateSignalLevel(wifiInfo.rssi, 5) < i.level) {
                    i("b", "${i.level} is stronger than ${wifiInfo.ssid}")
                }
            }
        }
    }

    fun check() {
        if(wifiManager.connectionInfo.networkId == -1) {

        }
    }

}
