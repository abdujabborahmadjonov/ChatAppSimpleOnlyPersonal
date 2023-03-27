package dev.abdujabbor.chatappsimple.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.databinding.DialogLoginEmailBinding
import dev.abdujabbor.chatappsimple.databinding.FragmentAuthMainBinding
import dev.abdujabbor.chatappsimple.models.MyPerson
import dev.abdujabbor.chatappsimple.utils.MyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AuthMainFragment : Fragment() {

    private val binding by lazy { FragmentAuthMainBinding.inflate(layoutInflater) }
    lateinit var database: FirebaseDatabase
    lateinit var dataRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userlist: ArrayList<MyPerson>
    lateinit var storedVerificationId: String
    lateinit var dialog: AlertDialog
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dataRef = database.getReference("akkauntlar")

        if (auth.currentUser != null) {
            findNavController().navigate(R.id.allUsersFragment)
        }
        binding.loginWithemail.setOnClickListener {
            loginregisteremail()
        }
        loadData()
        binding.loginBtn.setOnClickListener {
            login()
        }
        binding.signingoogel.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(requireActivity(), options)
            signInClient.signInIntent.also {
                startActivityForResult(it, 0)
            }

        }
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                findNavController().navigate(R.id.allUsersFragment)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                MyData.storedVerificationId = storedVerificationId
                findNavController().navigate(R.id.phoneVerifyFragment)
            }
        }

        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        try {
            auth.signInWithCredential(credentials)
            Toast.makeText(requireContext(), "Successfully logged in", Toast.LENGTH_LONG)
                .show()
            add()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }

    fun add()= CoroutineScope(Dispatchers.IO).launch {
        val name = auth.currentUser?.displayName.toString()
        val photourl = auth.currentUser?.photoUrl.toString()
        val uid = auth.uid
        val key = dataRef.push().key
        for (i in userlist) {
            if (i.displayName != name && photourl != "null") {
                val person = MyPerson(uid, photourl, name)
                dataRef.child(key!!).setValue(person).await()
                break
            }
        }


    }

    private fun login() {
        var number = binding.phoneNumber.text.toString()
        if (!number.isEmpty()) {
            number = "+998$number"
            sendVerificationcode(number)
        } else {
            Toast.makeText(requireContext(), "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    fun loadData() {
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist = java.util.ArrayList<MyPerson>()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(MyPerson::class.java)
                    if (value != null) {
                        userlist.add(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun loginregisteremail() {
        val dialogBinding = DialogLoginEmailBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialogBinding.apply {
            registerBtn.setOnClickListener {
                val email = etEmailLogin.text.toString()
                val password = etPasswordLogin.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            auth.createUserWithEmailAndPassword(email, password)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Succesfully registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: java.lang.Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }
                }
            }

            loginBtn.setOnClickListener {
                val email = etEmailLogin.text.toString()
                val password = etPasswordLogin.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            auth.signInWithEmailAndPassword(email, password)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Succesfully logged in",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.loginForEmailAndPhonrFragment)
                            }

                        } catch (e: java.lang.Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }
                }
            }
        }
        dialog.show()

    }

}