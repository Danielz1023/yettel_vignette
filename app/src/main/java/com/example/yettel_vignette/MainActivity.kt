package com.example.yettel_vignette

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.yettel_vignette.databinding.ActivityMainBinding
import com.example.yettel_vignette.fragments.VignetteFragment
import com.example.yettel_vignette.interfaces.VignetteActionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), VignetteActionListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupBackButton()
        updateNavbarTitle("E-matrica")
        replaceFragment(VignetteFragment())
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            handleBackButtonPress()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack()
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateNavbarTitle(title: String) {
        binding.titleText.text = title
    }

    private fun handleBackButtonPress() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        }
    }

    override fun showNavbar() {
        binding.navbar.visibility = View.VISIBLE
    }

    override fun hideNavbar() {
        binding.navbar.visibility = View.GONE
    }
}
