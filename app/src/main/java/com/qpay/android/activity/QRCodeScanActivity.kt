package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.qpay.android.R
import com.qpay.android.databinding.ActivityQrcodeScanBinding
import com.qpay.android.databinding.ActivitySendToMobileBinding

class QRCodeScanActivity : AppCompatActivity() {
  private lateinit var binding: ActivityQrcodeScanBinding
  private lateinit var codeScanner: CodeScanner
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_qrcode_scan)

    codeScanner = CodeScanner(this, binding.scannerView)
    codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
    codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
    // ex. listOf(BarcodeFormat.QR_CODE)
    codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
    codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
    codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
    codeScanner.isFlashEnabled = false // Whether to enable flash or not

    codeScanner.decodeCallback = DecodeCallback {
      runOnUiThread {
       // Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
        val intent = Intent(this, SendAmountActivity::class.java)
        intent.putExtra("type","scan")
        intent.putExtra("number",it.text.toString())
        startActivity(intent)
      }
    }
    codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
      runOnUiThread {
        Toast.makeText(this, "Camera initialization error: ${it.message}",
          Toast.LENGTH_LONG).show()
      }
    }

    binding.scannerView.setOnClickListener {
      codeScanner.startPreview()
    }

    binding.ivBack.setOnClickListener { finish() }
  }

  override fun onResume() {
    super.onResume()
    codeScanner.startPreview()

  }
  override fun onPause() {
    codeScanner.releaseResources()
    super.onPause()
  }
}