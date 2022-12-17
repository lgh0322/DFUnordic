package com.aaa.df

import android.Manifest
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import java.util.*


class MainActivity : AppCompatActivity() {
    val dataScope = CoroutineScope(Dispatchers.IO)

    lateinit var scanner: BluetoothLeScanner
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DfuServiceInitiator.createDfuNotificationChannel(applicationContext);
        checkPermissions()

        PathUtil.initVar(this)


        val bluetoothManager =
         getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = bluetoothManager.adapter
        scanner =mBluetoothAdapter.bluetoothLeScanner
        val settings: ScanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()
        val builder = ScanFilter.Builder()
        val filter = builder.build()
        //

        dataScope.launch {
            delay(3000)
            scanner.startScan(null, settings, leScanCallback)

        }


    }


    fun dada(name:String,addr:String){
        scanner.stopScan(leScanCallback)
        val starter = DfuServiceInitiator(addr)
            .setDeviceName(name)

        starter.setZip(R.raw.aa)
        val controller = starter.start(applicationContext, DfuService::class.java)
    }
    data class Fuck(val name:String,val addr:String)
    val ggx= arrayListOf<Fuck>()
    var isUpdating =false

    private var leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(
            callbackType: Int,
            result: ScanResult,
        ) {
            super.onScanResult(callbackType, result)
            val name=result.device.name
            val addr=result.device.address
            name?.let {
                if(it.isNotEmpty()){
                    if(it.contains("Duo",true)){
                        Log.e("fuck",name+","+addr)
                        var same=false
                        for(k in ggx){
                            if(k.name==it){
                                same=true
                            }
                        }
                        if(!same){
                            ggx.add(Fuck(it,addr))
                            if(isUpdating==false){
                                isUpdating=true
                                dada(it,addr)
                            }
                        }
                    }

                }
            }

        }
    }









    private fun checkPermissions() {
        val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionDeniedList: MutableList<String> = ArrayList()
        for (permission in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            } else {
                permissionDeniedList.add(permission)
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            val deniedPermissions = permissionDeniedList.toTypedArray()
            ActivityCompat.requestPermissions(this, deniedPermissions, 12)
        }
    }


    //DFU进度监听器
    private val mDfuProgressListener: DfuProgressListener = object : DfuProgressListenerAdapter() {
        override fun onDeviceConnecting(deviceAddress: String) {

        }

        override fun onDfuProcessStarting(deviceAddress: String) {

        }

        override fun onEnablingDfuMode(deviceAddress: String) {

        }

        override fun onFirmwareValidating(deviceAddress: String) {

        }

        override fun onDeviceDisconnecting(deviceAddress: String) {

        }

        override fun onDfuCompleted(deviceAddress: String) {
            Toast.makeText(this@MainActivity, "固件升级成功", Toast.LENGTH_SHORT).show()
            Handler().postDelayed(object: Runnable {


                override fun run() {
                    val manager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);

        }

        override fun onDfuAborted(deviceAddress: String) {

        }

        override fun onProgressChanged(deviceAddress: String, percent: Int, speed: Float, avgSpeed: Float, currentPart: Int, partsTotal: Int) {
            val temp = percent
            Log.e("erg", "sdf " + temp)
        }

        override fun onError(deviceAddress: String, error: Int, errorType: Int, message: String) {
        }
    }


    override fun onResume() {
        super.onResume()
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener)
    }

    override fun onPause() {
        super.onPause()
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener)
    }
}