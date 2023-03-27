package dev.abdujabbor.chatappsimple.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import dev.abdujabbor.chatappsimple.adapters.RvClick
import dev.abdujabbor.chatappsimple.adapters.UsersAdapter
import dev.abdujabbor.chatappsimple.databinding.FragmentAllUsersBinding
import dev.abdujabbor.chatappsimple.databinding.HeaderLayoutBinding
import dev.abdujabbor.chatappsimple.models.MyPerson
import dev.abdujabbor.chatappsimple.utils.MyData
import dev.abdujabbor.chatappsimple.utils.MyData.iuusernAme
import dev.abdujabbor.chatappsimple.utils.MyData.recieveruid
import dev.abdujabbor.chatappsimple.utils.MyData.username
import dev.abdujabbor.chatappsimple.utils.MyData.usernameiu
import java.util.*
import kotlin.collections.ArrayList


class AllUsersFragment : Fragment() ,RvClick{
    private val serverKey = ""
    val binding by lazy { FragmentAllUsersBinding.inflate(layoutInflater) }
    private lateinit var database: FirebaseDatabase
    private lateinit var rvAdapter: UsersAdapter
    lateinit var userlist: ArrayList<MyPerson>
    private lateinit var reference: DatabaseReference
    lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userlist = ArrayList()

        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("akkauntlar")
        loadData()
        loadheader()
        add()












        binding.toolbar.setOnLongClickListener{
            FirebaseAuth.getInstance().signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(dev.abdujabbor.chatappsimple.R.string.default_web_client_id))
                .requestEmail().build()

            val googleSigningClient = GoogleSignIn.getClient(requireActivity(), gso)
            googleSigningClient.signOut()

            Toast.makeText(requireContext(), "sdg", Toast.LENGTH_SHORT).show()
            true
        }
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerlayout.openDrawer(GravityCompat.START)
            val vibratorService = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(60)
        }
        val searchView = binding.toolbar.menu.getItem(0).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }


            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 == null || p0.trim().isEmpty()) {
                    resetSearch()
                    return false
                }

                val none = ArrayList(userlist)
                for (value in userlist) {
                    if (!value.displayName!!.lowercase(Locale.getDefault())
                            .contains(p0.lowercase())
                    ) {
                        none.remove(value)
                    }
                }
                rvAdapter = UsersAdapter(none, requireContext(),this@AllUsersFragment)
                binding.recyclerview.adapter = rvAdapter
                return false
            }
        })
        return binding.root
    }

    fun loadData() {
        rvAdapter = UsersAdapter(userlist, requireContext(),this)
        binding.recyclerview.adapter = rvAdapter
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist = java.util.ArrayList<MyPerson>()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(MyPerson::class.java)
                    if (value != null) {
                        userlist.add(value)
                    }
                    for ( i in userlist){
                        if (i.uid==auth.uid){
                            i.displayName="Saved Messeges"
                            i.photoUrl="https://static10.tgstat.ru/channels/_0/31/31324acfc838ed6e6add64460e46148d.jpg"
                        }
                    }
                }

                rvAdapter.list.clear()
                rvAdapter.list.addAll(userlist)
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun resetSearch() {

        rvAdapter = UsersAdapter(userlist, requireContext(),this)

        binding.recyclerview.adapter = rvAdapter
    }
    fun loadheader(){
        val bindingheader by lazy { HeaderLayoutBinding.inflate(layoutInflater) }
        val header = binding.navView.getHeaderView(0)
        val userimage = header.findViewById<ImageView>(R.id.icon)
        val usernamr = header.findViewById<TextView>(dev.abdujabbor.chatappsimple.R.id.headername)

        Toast.makeText(requireContext(), auth.currentUser?.photoUrl.toString(), Toast.LENGTH_SHORT).show()
        Log.d("salam", auth.currentUser?.displayName.toString())
         Glide.with(requireActivity()).load(auth.currentUser?.photoUrl).into(userimage)
        usernamr.text="${auth.currentUser?.displayName}  ${auth.currentUser?.photoUrl} ${auth.currentUser?.uid}"
    }
    fun add() {
        val name = auth.currentUser?.displayName.toString()
        val photourl = auth.currentUser?.photoUrl.toString()
        val uid = auth.uid
        val key = reference.push().key
        for (i in userlist) {
            if (i.uid != uid&&photourl!="null") {
                val person = MyPerson(uid, photourl, name)
                reference.child(key!!).setValue(person)
                break
            }
        }


    }

    override fun click(moview: MyPerson, position: Int) {
        username = auth.currentUser?.displayName.toString()
        recieveruid=moview.uid!!
        usernameiu=moview.uid!!+auth.uid
        iuusernAme=auth.uid+moview.uid
        MyData.userall=moview
        findNavController().navigate(dev.abdujabbor.chatappsimple.R.id.chatFragment)

    }
}