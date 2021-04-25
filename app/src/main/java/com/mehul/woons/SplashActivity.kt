package com.mehul.woons

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mehul.woons.viewmodels.LibraryViewModel

class SplashActivity : AppCompatActivity() {
    val libraryViewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        libraryViewModel.updateLibraryInfo()
        // Wait till initial loading (updating saved data) is done!
        libraryViewModel.initialLoading.observe(this) {
            if (!it) {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }
}