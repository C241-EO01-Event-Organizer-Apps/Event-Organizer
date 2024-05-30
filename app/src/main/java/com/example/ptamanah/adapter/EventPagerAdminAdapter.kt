package com.example.ptamanah.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ptamanah.view.admin.events.ListEventsAdminFragment

class EventPagerAdminAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ListEventsAdminFragment()
        fragment.arguments = Bundle().apply {
            putInt(ListEventsAdminFragment.ARG_POSITION, position + 1)
        }
        return fragment
    }
}