package com.uni.link.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.uni.link.R
import com.uni.link.ui.activities.ProfileActivity
import com.uni.link.ui.adapters.NotificationsAdapter
import com.uni.link.ui.callbacks.NotificationsCallback
import com.uni.link.ui.base.BaseFragment
import com.uni.link.data.models.Notification
import com.uni.link.databinding.FragmentMessagerieBinding
import com.uni.link.databinding.FragmentNotificationsBinding
import com.uni.link.ui.activities.MemeActivity
import com.uni.link.ui.adapters.ScreenSlidePagerAdapter
import com.uni.link.ui.viewmodels.NotificationsViewModel
import com.uni.link.utils.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsFragment : BaseFragment() {
    private lateinit var notificationsAdapter: NotificationsAdapter
    private val notificationsViewModel: NotificationsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentMessagerieBinding>(inflater, R.layout.fragment_messagerie, container, false)
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
    }

    private fun initViewPager() {

        val viewPager: ViewPager2 = view?.findViewById(R.id.vp_main) ?: return
        viewPager.adapter = ScreenSlidePagerAdapter(requireActivity())

        val tabs: TabLayout = view?.findViewById(R.id.tl_main) ?: return

        TabLayoutMediator(
            tabs, viewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "CHATS"
                }
                1 -> {
                    tab.text = "PEOPLE"
                }
            }
        }.attach()

    }


}

