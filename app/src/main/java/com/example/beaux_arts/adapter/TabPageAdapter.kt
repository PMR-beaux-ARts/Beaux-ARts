package com.example.beaux_arts.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.beaux_arts.*


class TabPageAdapter (activity: FragmentActivity, private val tabCount: Int) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ItineraireFragment()
            1 -> CollectionFragment()
            2 -> InfoFragment()
            3 -> ShopFragment()
            else -> ItineraireFragment()
        }

    }

}