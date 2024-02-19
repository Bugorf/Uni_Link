package com.uni.link.ui.activities

import am.appwise.components.ni.NoInternetDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.uni.link.R
import com.uni.link.data.Status
import com.uni.link.data.events.ScrollingEvent
import com.uni.link.ui.base.BaseActivity
import com.uni.link.ui.fragments.HomeFragment
import com.uni.link.ui.fragments.FavesFragment
import com.uni.link.ui.fragments.NotificationsFragment
import com.uni.link.ui.fragments.ProfileFragment
import com.uni.link.ui.viewmodels.UsersViewModel
import com.uni.link.utils.*
import com.uni.link.utils.AppUtils.getDrawable
import com.gelostech.pageradapter.PagerAdapter
import com.google.firebase.messaging.FirebaseMessaging
import com.mikepenz.ionicons_typeface_library.Ionicons
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_layout.*
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MainActivity : BaseActivity(), ViewPager.OnPageChangeListener {

    private var doubleBackToExit = false
    private var editProfile: MenuItem? = null
    private lateinit var slidingDrawer: SlidingRootNav
    private lateinit var favesFragment: FavesFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var notifFragment: NotificationsFragment
    private lateinit var noInternetDialog: NoInternetDialog
    private val usersViewModel: UsersViewModel by viewModel()

    private lateinit var APP_NAME: String
    private lateinit var HOME: String
    private lateinit var FAVES: String
    private lateinit var PROFILE: String
    private lateinit var NOTIFICATIONS: String
    private lateinit var PLAY_STORE_LINK: String
    private var lastSelectedMenu = 0 // Bottom navigation menu

    override fun onStart() {
        super.onStart()
        com.uni.link.UL.updateNotificationToken(this)
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationUtils(this).clearNotifications()

        profileFragment = ProfileFragment()
        favesFragment = FavesFragment()
        homeFragment = HomeFragment()
        notifFragment = NotificationsFragment()

        APP_NAME = getString(R.string.app_name)
        HOME = getString(R.string.fragment_home)
        FAVES = getString(R.string.fragment_faves)
        PROFILE = getString(R.string.fragment_profile)
        NOTIFICATIONS = getString(R.string.fragment_notifications)

        setupToolbar()
        setupViewPager()
        setupDrawer()

        initNoInternet()
        initLogoutObserver()
        AppUtils.requestNotificationPermissions(this) {
            Timber.i("Notification permissions granted: $it")
        }
    }

    //Setup the main toolbar
    private fun setupToolbar() {
        setSupportActionBar(mainToolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbarTitle.text = getString(R.string.app_name)
    }

    //Setup the main view pager
    private fun setupViewPager() {
        val adapter = PagerAdapter(supportFragmentManager, this)

        // Add empty fragment and title at the center to accommodate for the new meme FAB
        adapter.addAllFragments(homeFragment, favesFragment, Fragment(), notifFragment, profileFragment)
        adapter.addAllTitles(HOME, FAVES,"", NOTIFICATIONS, PROFILE)

        mainViewPager.adapter = adapter
        mainViewPager.addOnPageChangeListener(this)
        mainViewPager.offscreenPageLimit = 4

        bottomNavigation.setupWithViewPager(mainViewPager)

        addMeme.setOnClickListener {
            startActivity(Intent(this, PostMemeActivity::class.java))
            AppUtils.slideRight(this)
        }
    }

    //Setup drawer
    private fun setupDrawer() {
        slidingDrawer = SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.drawer_layout)
                .withDragDistance(150)
                .withToolbarMenuToggle(mainToolbar)
                .inject()

        mainRoot.setOnClickListener {
            if (slidingDrawer.isMenuOpened) slidingDrawer.closeMenu(true)
        }

        if (sessionManager.getAdminStatus() == Constants.SUPER_ADMIN) {
            adminActions.showView()
        }

        setupDrawerIcons()
        drawerClickListeners()

        drawerName.text = sessionManager.getUsername()
        drawerEmail.text = sessionManager.getEmail()

        val themeIcon = when (sessionManager.isDarkMode()) {
            true -> getDrawable(this, Ionicons.Icon.ion_ios_sunny, R.color.color_primary, 25)
            false -> getDrawable(this, Ionicons.Icon.ion_ios_moon, R.color.color_primary, 22)
        }
        themeSwitch.setImageDrawable(themeIcon)
    }

    private fun setupDrawerIcons() {
        drawerShare.setDrawable(getDrawable(this, Ionicons.Icon.ion_android_share, R.color.color_primary, 18))
        drawerFeedback.setDrawable(getDrawable(this, Ionicons.Icon.ion_ios_email, R.color.color_primary, 18))
        drawerTerms.setDrawable(getDrawable(this, Ionicons.Icon.ion_clipboard, R.color.color_primary, 18))
        drawerPolicy.setDrawable(getDrawable(this, Ionicons.Icon.ion_ios_list, R.color.color_primary, 18))
        drawerLogout.setDrawable(getDrawable(this, Ionicons.Icon.ion_log_out, R.color.color_primary, 18))
    }

    private fun drawerClickListeners() {

        drawerAction(drawerShare) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, APP_NAME)
            val message = getString(R.string.label_invite_body) + "\n\n" + PLAY_STORE_LINK
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(intent, getString(R.string.intent_invite_pals)))
        }

        drawerAction(drawerFeedback) {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(getString(R.string.intent_dev_email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, APP_NAME)
            startActivity(Intent.createChooser(emailIntent, getString(R.string.intent_send_feedback)))
        }

        drawerAction(drawerTerms) { Toast.makeText(this,"Votre satisfaction, c'est notre engagement !",Toast.LENGTH_SHORT).show() }

        drawerAction(drawerPolicy) { Toast.makeText(this,"NoBody care the privacy on the intenet",Toast.LENGTH_SHORT).show() }


        drawerAction(drawerPostMemes) {
            startActivity(Intent(this, PendingMemesActivity::class.java))
            AppUtils.slideRight(this)
        }

        drawerAction(drawerReports) {
            startActivity(Intent(this, ReportsActivity::class.java))
            AppUtils.slideRight(this)
        }

        drawerAction(drawerLogout) {
            alert("Vous voulez vraiment quitter ?") {
                title = "Quitter"
                positiveButton("Quitter") { usersViewModel.logout() }
                negativeButton("Annuler") {}
            }.show()
        }

        drawerAction(themeSwitch) {
            if (sessionManager.isDarkMode()) {
                sessionManager.setDarkMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                sessionManager.setDarkMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            com.uni.link.UL.updateTheme(this)
        }
    }

    private fun drawerAction(view: View, action: () -> Unit) {
        view.setOnClickListener {
            slidingDrawer.closeMenu(true)
            runDelayed(300, action)
        }
    }

    private fun openLink(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun initNoInternet() {
        noInternetDialog = NoInternetDialog.Builder(this)
                .setCancelable(true)
                .build()
    }

    private fun initLogoutObserver() {
        usersViewModel.genericResponseLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> showLoading("Logging out...")

                Status.SUCCESS -> {
                    runDelayed(500) {
                        hideLoading()
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.TOPIC_GLOBAL)
                        sessionManager.clearSession()

                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        AppUtils.slideLeft(this)
                        finish()
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    longToast("${it.error}. Please try again")
                }
            }
        })
    }

    override fun onPageSelected(position: Int) {
        editProfile?.isVisible = position == 4

        when(position) {
            0 -> toolbarTitle.text = APP_NAME
            1 -> toolbarTitle.text = FAVES
            3 -> toolbarTitle.text = NOTIFICATIONS
            4 -> toolbarTitle.text = PROFILE
        }

        lastSelectedMenu = position
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        editProfile = menu?.findItem(R.id.menu_edit_profile)

        editProfile?.icon = getDrawable(this, Ionicons.Icon.ion_edit, R.color.color_text_secondary, 16)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_edit_profile -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
                AppUtils.slideRight(this)
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        drawerName.text = sessionManager.getUsername()
    }

    override fun onBackPressed() {
        if (slidingDrawer.isMenuOpened) {
            slidingDrawer.closeMenu(true)

        } else if (bottomNavigation.currentItem != 0) {
            bottomNavigation.currentItem = 0

        } else {
            if (doubleBackToExit) {
                finishAffinity()

            } else {
                toast("Tap back again to exit")
                doubleBackToExit = true

                Handler().postDelayed({doubleBackToExit = false}, 1500)
            }
        }
    }

    override fun onDestroy() {
        noInternetDialog.onDestroy()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScrollingEvent(event: ScrollingEvent) {
        when (event.showActionButton) {
            true -> addMeme.show()
            else -> addMeme.hide()
        }
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    // Unused methods
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

}