package com.mehul.woons

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mehul.woons.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /*val lol = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return WebtoonInfoViewModel(application, "", "", 3) as T
        }
    }

    val vm: WebtoonInfoViewModel by viewModels {
        lol
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.elevation = 0.0F
        setupNavigation()
    }


    private fun setupNavigation() {
        val navController = findNavController(R.id.fragNavHost)
        binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.browseFragment) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }

        /*val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.libraryFragment,
                R.id.discoverFragment,
                R.id.settingsFragment
            )
        )*/
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragNavHost)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}