package dev.abdujabbor.chatappsimple.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.databinding.FragmentPhoneVerifyBinding
import dev.abdujabbor.chatappsimple.utils.MyData
import dev.abdujabbor.chatappsimple.utils.MyData.storedVerificationId


class PhoneVerifyFragment : Fragment() {
    lateinit var auth: FirebaseAuth

    val binding by lazy { FragmentPhoneVerifyBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        auth= FirebaseAuth.getInstance()
        binding.verifyBtn.setOnClickListener{
            var otp=binding.idOtp.text.toString().trim()
            if(!otp.isEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId, otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(requireContext(),"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                   findNavController().navigate(R.id.loginForEmailAndPhonrFragment)
                } else {
// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        Toast.makeText(requireContext(),"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}