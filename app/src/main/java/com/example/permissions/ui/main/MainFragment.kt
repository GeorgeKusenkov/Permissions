package com.example.permissions.ui.main

import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.ContactsContract
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
    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted)
            getContacts()
        else
            Toast.makeText(context, "permission is not Granted", Toast.LENGTH_SHORT).show()
    }

    private fun getContacts() {
        val contentUri = ContactsContract.Contacts.CONTENT_URI
        val contactProjection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
        )

        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val phoneProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?"

        val stringBuilder = StringBuilder()

        requireActivity().contentResolver.query(
            contentUri,
            contactProjection,
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val hasPhoneIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

            while (cursor.moveToNext()) {
                stringBuilder.append(": ")

                val hasPhone = cursor.getInt(hasPhoneIndex) > 0
                if (hasPhone) {

                    val contactId = cursor.getString(idIndex)
                    requireActivity().contentResolver. query(
                        phoneUri,
                        phoneProjection,
                        phoneSelection,
                        arrayOf(contactId),
                        null
                    )?.use {phoneCursor ->
                        val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        while (phoneCursor.moveToNext()) {
                            stringBuilder.append(phoneCursor.getString(numberIndex))
                                .append(", ")
                        }
                    }
                } else {
                    stringBuilder.append("no phone")
                }
                stringBuilder.append("\n")
            }
        }
        binding.textView.text = stringBuilder.toString()
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
        checkPermissions()
    }

    private fun checkPermissions() {
        return if (
            ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getContacts()
        } else {
            launcher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }

}