package com.example.permissions.ui.main

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.permissions.R
import java.util.jar.Manifest

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        Toast.makeText(context, "permission is $isGranted", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "permission is Granted", Toast.LENGTH_SHORT).show()
        } else {
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    }

}