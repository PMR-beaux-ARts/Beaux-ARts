package com.example.beaux_arts.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.beaux_arts.ChartFragment
import com.example.beaux_arts.ItineraireFragment
import com.example.beaux_arts.CollectionFragment
import com.example.beaux_arts.SettingsFragment


class TabPageAdapter (activity: FragmentActivity, private val tabCount: Int) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ItineraireFragment()
            1 -> CollectionFragment()
            2 -> ChartFragment()
            3 -> SettingsFragment()
            else -> ItineraireFragment()
        }

    }

}