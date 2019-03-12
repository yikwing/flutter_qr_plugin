package com.example.flutter_method_channel

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


class FlutterMethodChannelPlugin(var activity: FlutterActivity) : MethodCallHandler, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {


    private val REQUEST_CODE_SCAN_ACTIVITY = 2777
    private val REQUEST_CODE_CAMERA_PERMISSION = 3777


    private var pendingResult: Result? = null
    private var executeAfterPermissionGranted: Boolean = false

    companion object {

        private lateinit var instance: FlutterMethodChannelPlugin

        @JvmStatic
        fun registerWith(registrar: PluginRegistry.Registrar) {

            val channel = MethodChannel(registrar.messenger(), "flutter_method_channel")
            instance = FlutterMethodChannelPlugin(registrar.activity() as FlutterActivity)
            registrar.addActivityResultListener(instance)
            registrar.addRequestPermissionsResultListener(instance)
            channel.setMethodCallHandler(instance)

        }
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        if (pendingResult != null) {
            result.error("ALREADY_ACTIVE", "QR Code reader is already active", null)
            return
        }
        pendingResult = result
        if (call.method == "qrcode") {

            val intent = Intent(activity, QRCodeActivity::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE_SCAN_ACTIVITY)

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        activity.requestPermissions(arrayOf<String>(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
    }

    private fun shouldShowRequestPermissionRationale(activity: Activity,
                                                     permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            activity.shouldShowRequestPermissionRationale(permission)
        } else false
    }

    private fun checkSelfPermission(context: Context, permission: String?): Int {
        if (permission == null) {
            throw IllegalArgumentException("permission is null")
        }
        return context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == REQUEST_CODE_SCAN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val string = data.getStringExtra("qrcode")
                    Log.d("this", string)
                    pendingResult?.success(string)
                }
            } else {
                pendingResult!!.success("")
            }
            pendingResult = null
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]

                if (permission == Manifest.permission.CAMERA) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        if (executeAfterPermissionGranted) {
                            val intent = Intent(activity, QRCodeActivity::class.java)
                            activity.startActivityForResult(intent, REQUEST_CODE_SCAN_ACTIVITY)
                        }
                    } else {
                        setNoPermissionsError()
                    }
                }
            }
        }
        return false
    }

    private fun setNoPermissionsError() {
        pendingResult!!.error("permission", "you don't have the user permission to access the camera", null)
        pendingResult = null
    }
}
