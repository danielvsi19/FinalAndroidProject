package com.example.couplesnight

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.couplesnight.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        // If user is logged in, navigate to CodeGenerationFragment
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && navController.currentDestination?.id == R.id.loginFragment) {
            navController.navigate(R.id.codeGenerationFragment)
        }

        // Show/hide toolbar depending on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideToolbarOn = listOf(
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.editProfileFragment
            )
            supportActionBar?.apply {
                if (destination.id in hideToolbarOn) hide() else show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        val isLoginOrRegister = navController.currentDestination?.id == R.id.loginFragment ||
                navController.currentDestination?.id == R.id.registerFragment
        menu?.findItem(R.id.action_logout)?.isVisible = !isLoginOrRegister
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            R.id.action_edit_profile -> {
                navController.navigate(R.id.editProfileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}