package com.mehul.woons

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mehul.woons.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.elevation = 0.0F
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.fragNavHost)
        binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.browseFragment ||
                destination.id == R.id.infoFragment ||
                destination.id == R.id.readerFragment
            ) {
                binding.toolbar.toolbar.title = "Loading"
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }

            when (destination.id) {
                R.id.libraryFragment -> {
                    binding.toolbar.toolbar.title = "Library"
                }
                R.id.discoverFragment -> {
                    binding.toolbar.toolbar.title = "Discover"
                }
                R.id.settingsFragment -> {
                    binding.toolbar.toolbar.title = "Settings"
                }

            }
        }

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.libraryFragment,
                R.id.discoverFragment,
                R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragNavHost)
        supportActionBar?.subtitle = ""
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}