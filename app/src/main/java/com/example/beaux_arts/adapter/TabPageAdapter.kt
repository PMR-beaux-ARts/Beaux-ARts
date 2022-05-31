package com.example.beaux_arts.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.beaux_arts.ChartFragment
import com.example.beaux_arts.HomeFragment
import com.example.beaux_arts.ListFragment
import com.example.beaux_arts.SettingsFragment


class TabPageAdapter (activity: FragmentActivity, private val tabCount: Int) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1 -> ListFragment()
            2 -> ChartFragment()
            3 -> SettingsFragment()
            else -> HomeFragment()
        }

    }

}