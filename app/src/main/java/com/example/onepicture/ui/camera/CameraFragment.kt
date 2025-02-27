package com.example.onepicture.ui.camera

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.onepicture.databinding.FragmentCameraBinding
import com.example.onepicture.utils.safeNavigateWithArgs

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var cameraViewModel: CameraViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraViewModel = ViewModelProvider(this)[CameraViewModel::class.java]
        cameraViewModel.startCamera(binding.previewView, viewLifecycleOwner)
        startCountdown()

        binding.shutterButton.setOnClickListener {
            cameraViewModel.captureImage(requireContext()) { imagePath ->
                val bundle = Bundle()
                bundle.putString("imagePath", imagePath)

                val action =
                    CameraFragmentDirections.actionCameraFragmentToPreviewFragment(imagePath)
                findNavController().safeNavigateWithArgs(action, bundle)
            }
        }
    }


    private fun startCountdown() {
        countdownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                binding.timerTextView.text = String.format("%d:%02d", minutes, seconds % 60)
            }

            override fun onFinish() {
                findNavController().popBackStack()
            }
        }.start()
    }


    override fun onDestroyView() {
        countdownTimer.cancel()
        super.onDestroyView()
    }
}