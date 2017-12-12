package com.example.j.myapplication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
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
        wifiManager.isWifiEnabled = true
        i("a", wifiManager.configuredNetworks.toString())
        i("b", wifiManager.connectionInfo.toString())
        i("b", wifiManager.connectionInfo.toString())

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
                        var listAdapter: CustomListViewAdapter = CustomListViewAdapter(applicationContext, array)
                        listView1.adapter = listAdapter
                        size = array.size

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

            size -= size
            while(size >= 0) {
                i("c", array.get(size).BSSID)
            }
        }
    }

    class CustomListViewAdapter(context: Context, array: List<Any>): BaseAdapter() {

        private var mLayoutInflater: LayoutInflater
        private var array: List<Any>
        private var context: Context

        init {
            this.mLayoutInflater = LayoutInflater.from(context)
            this.array = array
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var view: View
            var v: ViewHolder

            if(p1 == null) {
                view = this.mLayoutInflater.inflate(R.layout.listview_item, null)
                v = ViewHolder(view)
                view.tag = v
            }
            else {
                view = p1
                v = view.tag as ViewHolder
            }

            if(p0 % 2 != 0) v.ssid.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_light))
            v.ssid.setText(array.get(p0).toString())
//            v.ssid.setText("SSID : ${array.get(p0).SSID}, BSSID : ${array.get(p0).BSSID}, LEVEL : ${array.get(p0).level}, FREQUENCY : ${array.get(p0).frequency}, CAPABILITY : ${array.get(p0).capabilities}")
            return view
        }

        override fun getItem(p0: Int): Any {
            return array.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return array.size
        }
    }

    public fun test(id: Int): String {
        return id.toString()
    }

    class ViewHolder(view: View?) {
        var ssid: TextView
        var bssid: TextView
        var frequency: TextView

        init {
            this.ssid = view?.findViewById<TextView>(R.id.ssid) as TextView
            this.bssid = view?.findViewById<TextView>(R.id.bssid) as TextView
            this.frequency = view?.findViewById<TextView>(R.id.frequency) as TextView
        }
    }

}
