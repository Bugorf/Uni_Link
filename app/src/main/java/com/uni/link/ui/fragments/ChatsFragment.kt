package com.uni.link.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.uni.link.R
import com.uni.link.data.models.Inbox
import com.uni.link.ui.activities.ChatActivity
import com.uni.link.ui.viewmodels.ChatViewHolder
import kotlinx.android.synthetic.main.fragment_chats.empty
import kotlinx.android.synthetic.main.fragment_chats.rv_main

/**
 *  Etabli de communication entre deux utilisateurs
 **/


class ChatsFragment : Fragment() {

    private lateinit var mAdapter: FirebaseRecyclerAdapter<Inbox, ChatViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager



    private val mDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewManager = LinearLayoutManager(requireContext())
        setupAdapter()
        return inflater.inflate(R.layout.fragment_chats, container, false)

    }

    private fun setupAdapter() {
        val baseQuery: Query =
            mDatabase.reference.child("chats").child(auth.uid!!)

        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, Inbox::class.java)
            .build()
        // Instantiate Paging Adapter
        mAdapter = object : FirebaseRecyclerAdapter<Inbox, ChatViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val inflater = layoutInflater
                return ChatViewHolder(inflater.inflate(R.layout.list_item_users, parent, false))
            }

            override fun onBindViewHolder(
                viewHolder: ChatViewHolder,
                position: Int,
                inbox: Inbox
            ) {

                viewHolder.bind(inbox) { name: String, photo: String, id: String ->
                    startActivity(
                        ChatActivity.createChatActivity(
                            requireContext(),
                            id,
                            name,
                            photo
                        )
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView()!!.findViewById<RecyclerView>(R.id.rv_main).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter

            /**
             *   Afficher différent layout selon existence du chat
             **/

            val userChatsRef = mDatabase.reference.child("chats").child(auth.uid!!)
            userChatsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        empty.visibility = View.VISIBLE
                        rv_main.visibility = View.GONE
                    } else {
                        empty.visibility = View.GONE
                        rv_main.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        }
    }
}