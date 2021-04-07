package com.aaa.df

import android.Manifest
import android.app.NotificationManager
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
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DfuServiceInitiator.createDfuNotificationChannel(applicationContext);
        checkPermissions()

        PathUtil.initVar(this)
        File(PathUtil.getPathX("fuck")).writeBytes(byteArrayOf(0x32.toByte(), 0x98.toByte()))


        val starter = DfuServiceInitiator("DF:87:95:4B:C8:B3")
                .setDeviceName("DuoEK 0508")
        val fileName: String = "f.zip"
        val directory: String =PathUtil.filePath

        starter.setZip(directory + fileName)
        val controller = starter.start(applicationContext, DfuService::class.java)

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
            val temp = percent * 360 / 100
            Log.e("fuck", "sdf " + temp)
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