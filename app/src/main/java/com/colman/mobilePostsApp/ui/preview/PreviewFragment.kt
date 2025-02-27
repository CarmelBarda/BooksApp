package com.example.onepicture.ui.preview

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.onepicture.databinding.FragmentPreviewBinding
import com.example.onepicture.utils.safeNavigateWithArgs
import java.io.File

class PreviewFragment : Fragment() {
    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var previewViewModel: PreviewViewModel
    private lateinit var locationViewModel: LocationViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        val locationAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            emptyList()
        )

        binding.autoCompleteTextView.setAdapter(locationAdapter)

        locationViewModel.cities.observe(viewLifecycleOwner) { cities ->
            locationAdapter.clear()
            locationAdapter.addAll(cities)
            locationAdapter.notifyDataSetChanged()
        }

        binding.autoCompleteTextView.addTextChangedListener { text ->
            text?.let {
                locationViewModel.fetchCities()
            }
        }

        previewViewModel = ViewModelProvider(this)[PreviewViewModel::class.java]

        val imagePath = arguments?.getString("imagePath")
        imagePath?.let {
            val file = File(it)
            val uri = Uri.fromFile(file)
            binding.imagePreview.setImageURI(uri)
        }

        binding.postButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            val location = binding.autoCompleteTextView.text.toString()
            previewViewModel.postImageWithDescription(imagePath!!, description, location)

            previewViewModel.postSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    // Navigate to PostsFragment
                    val action = PreviewFragmentDirections.actionPreviewFragmentToPostsFragment()
                    findNavController().safeNavigateWithArgs(action)
                } else {
                    Log.d("PreviewFragment", "error saving post")
                }
            }
        }
    }
}
