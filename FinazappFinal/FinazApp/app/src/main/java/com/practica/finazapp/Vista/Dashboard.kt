package com.practica.finazapp.Vista

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.practica.finazapp.ViewModelsApiRest.UserViewModel


class Dashboard : AppCompatActivity() {



    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var usuarioViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDashboard.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as
                NavHostFragment
        val navController = navHostFragment.navController
        // Definir los destinos de nivel superior del menú de hamburguesa
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_dashboard, R.id.nav_perfil, R.id.nav_ingresos, R.id.nav_reporte
        ), drawerLayout
        )

        val sharedPref = this.getSharedPreferences("MiApp", Context.MODE_PRIVATE)
        val usuarioId: Long = sharedPref.getLong("USUARIO_ID", -1)


        if (usuarioId == -1L) {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        Log.d("ActivityDashboard","IdUsuario: $usuarioId")
        val viewModel: SharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.setUsuarioId(usuarioId)
        setupActionBarWithNavController(navController,appBarConfiguration)
      navView.setupWithNavController(navController)

        usuarioViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        usuarioViewModel.ObtenerUsuario(usuarioId)
        usuarioViewModel.usuarioLiveData.observeOnce(this) { usuario ->

            val navHeader = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
            val nombreUsuarioTextView = navHeader.findViewById<TextView>(R.id.NombreUsuario)
            val usuarioTextView = navHeader.findViewById<TextView>(R.id.Usuario)
            usuario?.let {
                Log.d("DashboardActivity", "usuario encontrado")
                nombreUsuarioTextView.text = "${it.nombre} ${it.apellido}"
                usuarioTextView.text = it.username
            }
        }
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)
        // Opcionalmente, manejar la selección de ítems manualmente
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Dashboard -> {
                    navController.navigate(R.id.nav_dashboard)
                    true
                }
                R.id.graficos_Avanzados -> {
                    navController.navigate(R.id.graficos_Avanzados)
                    true
                }
                R.id.gastosAltos -> {
                    navController.navigate(R.id.gastosAltos)
                    true
                }
                else -> false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflar el menú
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {


                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                limpiarTokenYRedirigirALogin()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this) // Elimina el observador después de la primera actualización
                observer.onChanged(value)
            }
        })
    }

    private fun limpiarTokenYRedirigirALogin() {
        val sharedPref1 = this.getSharedPreferences("MiApp", Context.MODE_PRIVATE)
        // Limpiar el token en SharedPreferences
        with( sharedPref1.edit()) {
            remove("TOKEN")
            apply()
        }
}

}
