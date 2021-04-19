package com.sharkaboi.mediahub.modules.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.DrawableButton
import com.github.razir.progressbutton.showProgress
import com.sharkaboi.mediahub.BuildConfig
import com.sharkaboi.mediahub.R
import com.sharkaboi.mediahub.common.constants.AppConstants
import com.sharkaboi.mediahub.common.data.retrofit.AuthService
import com.sharkaboi.mediahub.common.extensions.showToast
import com.sharkaboi.mediahub.databinding.ActivityAuthBinding
import com.sharkaboi.mediahub.modules.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OAuthActivity : AppCompatActivity() {
    private val oAuthViewModel by viewModels<OAuthViewModel>()
    private var _binding: ActivityAuthBinding? = null
    private val binding: ActivityAuthBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        binding.btnRedirect.setOnClickListener {
            oAuthViewModel.redirectToAuth()
        }
    }

    private fun setObservers() {
        oAuthViewModel.oAuthState.observe(this) { state ->
            Log.d(TAG, "state : ${state.toString()}")
            if (state is OAuthState.Idle || state is OAuthState.OAuthFailure) {
                binding.btnRedirect.isEnabled = true
            } else {
                binding.btnRedirect.isEnabled = false
                binding.btnRedirect.showProgress {
                    buttonText = "Loading"
                    gravity = DrawableButton.GRAVITY_TEXT_START
                    progressColorRes = R.color.white
                }
            }
            when (state) {
                is OAuthState.RedirectToAuth -> {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            AuthService.getAuthTokenLink(
                                BuildConfig.clientId,
                                state.state,
                                state.codeChallenge
                            )
                        )
                    )
                    startActivity(intent)
                }
                is OAuthState.OAuthSuccess -> {
                    redirectToMainAppFlow()
                }
                is OAuthState.OAuthFailure -> {
                    showToast(state.message, Toast.LENGTH_LONG)
                }
                else -> Unit
            }
        }
    }

    private fun redirectToMainAppFlow() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        Log.d(TAG, "onNewIntent uri :${uri.toString()}")
        if (uri != null && uri.toString().startsWith(AppConstants.oAuthDeepLinkUri)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                oAuthViewModel.receivedAuthToken(code)
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        outState.putString(INTENT_DATA, intent.data.toString())
//        outPersistentState.putString(INTENT_DATA, intent.data.toString())
//        super.onSaveInstanceState(outState, outPersistentState)
//    }
//
//    override fun onRestoreInstanceState(
//        savedInstanceState: Bundle?,
//        persistentState: PersistableBundle?
//    ) {
//        val savedIntentData = savedInstanceState?.getString(INTENT_DATA)
//        val persistedIntentData = persistentState?.getString(INTENT_DATA)
//        if (intent.data == null && savedIntentData != null) {
//            intent.data = Uri.parse(savedIntentData)
//        } else if(intent.data == null && persistedIntentData != null){
//            intent.data = Uri.parse(persistedIntentData)
//        }
//        super.onRestoreInstanceState(savedInstanceState, persistentState)
//    }

    companion object {
        private const val TAG = "OAuthActivity"
        private const val INTENT_DATA = "intentData"
    }
}