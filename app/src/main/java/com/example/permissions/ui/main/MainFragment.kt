package com.example.permissions.ui.main

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.permissions.R
import com.example.permissions.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.jar.Manifest


private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"
class MainFragment : Fragment() {

//    companion object {
//        fun newInstance() = MainFragment()
//    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
    private var imageCapture: ImageCapture? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var executor: Executor
    private val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        if (map.values.all { it })
            startCamera()
        else
            Toast.makeText(context, "permission is not Granted", Toast.LENGTH_SHORT).show()
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
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())
        binding.takePhotoButton.setOnClickListener {
            takePhoto()
        }
        checkPermissions()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireActivity().contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "Photo Saved on: ${outputFileResults.savedUri}", Toast.LENGTH_SHORT).show()

                    Glide.with(requireActivity())
                        .load(outputFileResults.savedUri)
                        .circleCrop()
                        .into(binding.imagePreview)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Photo FAILED: ${exception.message}", Toast.LENGTH_SHORT).show()
                    exception.printStackTrace()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                requireActivity(),
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all {permission ->
            ContextCompat.checkSelfPermission(requireContext(),
                permission) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            startCamera()
            Toast.makeText(context, "permission is Granted", Toast.LENGTH_SHORT).show()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(android.Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

}