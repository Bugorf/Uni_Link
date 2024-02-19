package com.uni.link.ui.activities

import android.os.Bundle
import android.os.Handler
import com.uni.link.R
import com.uni.link.ui.base.BaseActivity
import com.uni.link.ui.fragments.LoginFragment
import com.uni.link.ui.fragments.SignupFragment
import com.uni.link.utils.addFragment
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : BaseActivity() {
    private var doubleBackToExit = false
    private lateinit var signUpFragment: SignupFragment
    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signUpFragment = SignupFragment()
        loginFragment = LoginFragment()

        addFragment(loginFragment, loginHolder.id)
    }

    override fun onBackPressed() {
        if (!signUpFragment.backPressOkay() || !loginFragment.backPressOkay()) {
            toast("Please wait...")

        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()

        } else {
            if (doubleBackToExit) {
                super.onBackPressed()
            } else {
                toast("Tap back again to exit")

                doubleBackToExit = true
                Handler().postDelayed({doubleBackToExit = false}, 1500)
            }
        }
    }

}
