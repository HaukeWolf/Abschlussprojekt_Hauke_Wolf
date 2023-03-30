package com.cgi.abschlussprojekt_hauke_wolf

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cgi.abschlussprojekt_hauke_wolf.databinding.ActivityMainBinding
import com.cgi.kspAnnotations.FunctionTemp
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.logging.Logger

@Target(AnnotationTarget.FUNCTION)
annotation class FunctionTempFunc(val name: String)

 @FunctionTemp(name = "FromMain")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
     val LOGS = Logger.getLogger(this.javaClass.name)

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         LOGS.warning("WO KOMMT DAS AN")


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}