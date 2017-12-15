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
    var array: ArrayList<ScanResult> = ArrayList<ScanResult>()
    var array2: ArrayList<WifiConfiguration> = ArrayList<WifiConfiguration>()

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
            configured.filter { Regex("\"").replace(it.SSID, "") == i.SSID }.map { list.add(i); array2.add(it) }
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
        var ssid: String = Regex("\"").replace(wifiInfo.ssid, "") // Remove double quotes

        // Compare filteredList and connected wifi
        //
        for((i, item) in filtered.withIndex()) {
            if(ssid != item.SSID) {
                if (wifiInfo.rssi < item.level) {
                    i("b", "${item.SSID}(${item.level}) is stronger than ${wifiInfo.ssid}(${wifiInfo.rssi})")
                    var wifiConf: WifiConfiguration = array2[i]
                    i("b", "${wifiConf.SSID} 's networkId : ${wifiConf.networkId}")
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(wifiConf.networkId, true)
                    wifiManager.reconnect()
                }
            }
        }
    }

    fun check() {
        if(wifiManager.connectionInfo.networkId == -1) {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    wifiManager.startScan()
                }
                else {
                    // Permission denied
                    i("Permission denied", "User denied the permission")
                }
            }
        }
    }

}
