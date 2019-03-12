package com.yikwing.zxingdemo

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView
import com.yikwing.zxingdemo.R

class QRCodeActivity : Activity(), QRCodeView.Delegate {

    lateinit var zxingview: ZXingView

    //光线变换
    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    override fun onScanQRCodeSuccess(result: String) {

        val vibrator = getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)

        Log.d("this", result)

        setResult(RESULT_OK, Intent().putExtra("qrcode", result))
        finish()
    }

    override fun onScanQRCodeOpenCameraError() {
        Log.d("this", "相机出错")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        zxingview = findViewById(R.id.zxingview)

        zxingview.setDelegate(this)
        zxingview.getScanBoxView().setOnlyDecodeScanBoxArea(true)
//        zxingview.startSpotDelay(500)
    }

    override fun onStart() {
        super.onStart()
        zxingview.startCamera()
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

        zxingview.startSpotAndShowRect()
    }

    override fun onStop() {
        super.onStop()
        zxingview.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        zxingview.onDestroy()
    }

}