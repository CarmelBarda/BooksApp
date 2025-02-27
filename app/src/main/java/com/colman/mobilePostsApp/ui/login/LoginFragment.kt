package com.example.onepicture.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.onepicture.databinding.FragmentLoginBinding
import com.example.onepicture.utils.safeNavigateWithArgs
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        userViewModel = ViewModelProvider(
            this,
            LoginViewModel.provideFactory(
                application = requireActivity().application,
                sharedPreferences = sharedPreferences
            )
        )[LoginViewModel::class.java]

        // Check if "Remember Me" was selected
        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            binding.etEmail.setText(savedEmail)
            binding.etPassword.setText(savedPassword)
            binding.cbRememberMe.isChecked = true
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val rememberMe = binding.cbRememberMe.isChecked

            userViewModel.loginUser(email, password, rememberMe,
                onSuccess = {
                    val action =
                        LoginFragmentDirections.actionSignInFragmentToPostsFragment()
                    findNavController().safeNavigateWithArgs(action)
                },
                onFailure = { errorMessage ->
                    Toast.makeText(
                        context,
                        "Sign In Failed: $errorMessage", Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        binding.btnSignUp.setOnClickListener {
            val action =
                LoginFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().safeNavigateWithArgs(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

