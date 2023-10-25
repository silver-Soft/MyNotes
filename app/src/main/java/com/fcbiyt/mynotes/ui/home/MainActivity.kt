package com.fcbiyt.mynotes.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fcbiyt.mynotes.R
import com.fcbiyt.mynotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}