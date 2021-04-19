package com.sharkaboi.mediahub.modules.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.sharkaboi.mediahub.R
import com.sharkaboi.mediahub.modules.MainActivity
import com.sharkaboi.mediahub.modules.auth.OAuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val splashViewModel by viewModels<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpObservers()
    }

    private fun setUpObservers() {
        splashViewModel.splashState.observe(this) { state ->
            when (state) {
                is SplashState.LoginExpired -> {
                    redirectToOAuthFlow()
                }
                is SplashState.FetchComplete -> {
                    Log.d("SplashActivity", state.toString())
                    if (state.isDarkMode) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    if (!state.isAccessTokenValid) {
                        redirectToOAuthFlow()
                    } else if (state.hasExpired) {
                        splashViewModel.refreshToken()
                    } else {
                        redirectToMainAppFlow()
                    }
                }
                else -> Unit
            }
        }
    }

    private fun redirectToMainAppFlow() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun redirectToOAuthFlow() {
        startActivity(Intent(this, OAuthActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}