package com.example.onepicture.ui.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onepicture.R
import com.example.onepicture.utils.safeNavigateWithArgs

class PermissionFragment : Fragment() {

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            if (allPermissionsGranted) {
                // All required permissions are granted, navigate to main content
                navigateToMainContent()
            } else {
                // Permissions denied, show a message to the user
                Toast.makeText(
                    requireContext(),
                    "Permissions are required to use the app",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check for camera and storage permissions
        if (arePermissionsGranted()) {
            // Permissions are already granted, navigate to the main content
            navigateToMainContent()
        } else {
            // Request permissions if not granted
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )

            navigateToMainContent()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val cameraPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun navigateToMainContent() {
        val action =
            PermissionFragmentDirections.actionPermissionFragmentToLoginFragment()
        findNavController().safeNavigateWithArgs(action)
    }
}
