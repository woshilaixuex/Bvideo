package com.elyric.bredio

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.elyric.bredio.databinding.ActivityMainBinding
import com.elyric.bredio.view.component.navigation.NavGraphBuilder
import com.elyric.bredio.view.component.player.GlobalPlayerViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController }
    private val globalPlayerViewModel: GlobalPlayerViewModel by viewModels()
    private var currentBottomTabOrder = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        globalPlayerViewModel

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setupNavigationUI()
    }

    private fun setupNavigationUI() {
        NavGraphBuilder.build(navController, this)
        currentBottomTabOrder = binding.btNavigation.menu.findItem(binding.btNavigation.selectedItemId)?.order ?: 0

        binding.btNavigation.setOnItemSelectedListener { item ->
            navigateFromBottomBar(item)
        }
        binding.btNavigation.setOnItemReselectedListener {
            // Keep the current tab stable on repeated taps.
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menuItem = binding.btNavigation.menu.findItem(destination.id) ?: return@addOnDestinationChangedListener
            if (!menuItem.isChecked) {
                menuItem.isChecked = true
            }
            currentBottomTabOrder = menuItem.order
        }
    }

    private fun navigateFromBottomBar(item: MenuItem): Boolean {
        if (item.itemId == navController.currentDestination?.id) {
            return true
        }

        val targetOrder = item.order
        val navigatingForward = targetOrder > currentBottomTabOrder
        val navOptions = navOptions {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            anim {
                enter = if (navigatingForward) R.anim.nav_slide_in_right else R.anim.nav_slide_in_left
                exit = if (navigatingForward) R.anim.nav_slide_out_left else R.anim.nav_slide_out_right
                popEnter = if (navigatingForward) R.anim.nav_slide_in_left else R.anim.nav_slide_in_right
                popExit = if (navigatingForward) R.anim.nav_slide_out_right else R.anim.nav_slide_out_left
            }
        }

        return runCatching {
            navController.navigate(item.itemId, null, navOptions)
            currentBottomTabOrder = targetOrder
        }.isSuccess
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
