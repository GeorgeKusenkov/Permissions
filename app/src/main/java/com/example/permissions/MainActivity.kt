package com.example.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.permissions.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

//    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//        Toast.makeText(this, "permission is $isGranted", Toast.LENGTH_SHORT).show()
//    }
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

//    private fun checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "permission is Granted", Toast.LENGTH_SHORT).show()
//        } else {
//            launcher.launch(Manifest.permission.CAMERA)
//        }
//    }
}