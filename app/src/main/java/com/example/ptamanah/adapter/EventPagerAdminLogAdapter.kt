package com.example.ptamanah.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ptamanah.view.admin.logcheck.EventAdminFragment

class EventPagerAdminLogAdapter(fragmentManager: FragmentManager,lifesycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager,lifesycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 ->{
                EventAdminFragment()
            }
            1 ->{
                EventAdminBayarFragment()
            }
            else ->{
                EventAdminManualFragment()
            }
        }
    }
}