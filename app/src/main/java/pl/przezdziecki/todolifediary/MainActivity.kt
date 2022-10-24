package pl.przezdziecki.todolifediary

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.przezdziecki.todolifediary.databinding.ActivityMainBinding

private var TAG: String = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navView: BottomNavigationView = binding.bottomNavigation
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            Log.d(TAG, "binding Bottom menu click: " + item.toString())
            when (item.itemId) {
                R.id.homeFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.homeFragment)
                    true
                }
                R.id.calendarFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.calendarFragment)
                    true
                }
                R.id.wikiFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.wikiFragment)
                    true
                }
                R.id.contactsFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.contactsFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_tags -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.tagsFragment)
                true
            }
        }
        return (super.onOptionsItemSelected(item));
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}