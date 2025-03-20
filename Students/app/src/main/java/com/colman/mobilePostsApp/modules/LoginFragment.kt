package com.colman.mobilePostsApp.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.MainActivity
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.databinding.FragmentLoginBinding
import com.colman.mobilePostsApp.viewModels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.layoutEmailAddress.editText?.text.toString().trim()
            val password = binding.layoutPassword.editText?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            userProfileViewModel.refreshUserData()
                            findNavController().navigate(R.id.action_loginFragment_to_postsContainerFragment)
                            (activity as? MainActivity)?.setBottomNavSelectedItem(R.id.nav_home)

                            Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Login Failed.", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
