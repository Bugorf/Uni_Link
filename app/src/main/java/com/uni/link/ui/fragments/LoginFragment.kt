package com.uni.link.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.uni.link.R
import com.uni.link.data.Status
import com.uni.link.data.models.User
import com.uni.link.ui.base.BaseFragment
import com.uni.link.ui.viewmodels.UsersViewModel
import com.uni.link.utils.AppUtils
import com.uni.link.utils.AppUtils.getDrawable
import com.uni.link.utils.TimeFormatter
import com.uni.link.utils.replaceFragment
import com.uni.link.utils.setDrawable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
 *  Traiter les donnees des utilisateurs pour la connection
 **/


class LoginFragment : BaseFragment() {
    private var isLoggingIn = false
    private val usersViewModel: UsersViewModel by viewModel()
    private val googleSignInOptions: GoogleSignInOptions by inject()

    companion object {
        private const val GOOGLE_SIGN_IN = 123
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginEmail.setDrawable(getDrawable(activity!!, Ionicons.Icon.ion_ios_email, R.color.color_text_secondary, 18))
        loginPassword.setDrawable(getDrawable(activity!!, Ionicons.Icon.ion_android_lock, R.color.color_text_secondary, 18))

        loginRegister.setOnClickListener {
            if (!isLoggingIn)
                (activity as AppCompatActivity).replaceFragment(SignupFragment(), R.id.loginHolder)
            else
                activity!!.toast("Please wait...")
        }

        initLoginObserver()
        initUserObserver()
        initPasswordResetObserver()

        loginButton.setOnClickListener { login() }
        loginForgotPassword.setOnClickListener { if (!isLoggingIn) forgotPassword() else activity!!.toast("Please wait...")}
    }

    /**
     * Login with provided email and password
     */
    private fun login() {
        if (!AppUtils.validated(loginEmail, loginPassword)) return

        val email = loginEmail.text.toString().trim()
        val password = loginPassword.text.toString().trim()

        usersViewModel.loginUserWithEmailAndPassword(email, password)
    }


    /**
     * Initialize function to observer logging in with Email & Password LiveData
     */
    private fun initLoginObserver() {
        usersViewModel.authLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    isLoggingIn = true
                    loginButton.startAnimation()
                }

                Status.SUCCESS -> {
                    usersViewModel.fetchUser(it.user!!.uid)
                }

                Status.ERROR -> {
                    errorLoggingIn(it.error!!)
                }
            }
        })
    }


    /**
     * Initialize function to observe User LiveData
     */
    private fun initUserObserver() {
        usersViewModel.userLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Timber.e("Fetching User details")
                }

                Status.SUCCESS -> {
                    Timber.e("Logged in: ${it.user?.admin}")
                    proceedToMainActivity(it.user!!, loginButton)
                }

                Status.ERROR -> {
                    errorLoggingIn(it.error!!)
                }
            }
        })
    }

    /**
     * Initialize function to observer Password reset LiveData
     */
    private fun initPasswordResetObserver() {
        usersViewModel.genericResponseLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    toast("Sending password reset instructions")
                }

                Status.SUCCESS -> {
                    longToast("Instruction to reset your password sent successfully")
                }

                Status.ERROR -> {
                    toast("Error sending instructions to reset your password")
                }
            }
        })
    }

    /**
     * Handle error when login in
     * @param message - Message to display to User
     */
    private fun errorLoggingIn(message: String) {
        isLoggingIn = false
        hideLoading()
        loginButton.revertAnimation()
        toast(message)
    }

    /**
     * Function to send email with reset password instructions
     */
    private fun forgotPassword() {
        val editText = EditText(activity)
        val layout = FrameLayout(activity!!)
        layout.setPaddingRelative(45,15,45,0)
        layout.addView(editText)

        activity!!.alert("Enter email to send password reset instructions") {
            customView = layout

            positiveButton("SEND EMAIL") {
                if (!AppUtils.validated(editText)) {
                    activity!!.toast("Please enter a valid email")
                    return@positiveButton
                }

                usersViewModel.sendResetPasswordEmail(editText.text.toString().trim())
            }

            negativeButton("CANCEL"){}
        }.show()
    }


    // Check if user has initiated logging in process. If in process, disable back button
    fun backPressOkay(): Boolean = !isLoggingIn

    override fun onDestroy() {
        if (loginButton != null) loginButton.dispose()
        super.onDestroy()
    }

}
