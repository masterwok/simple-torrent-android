package com.masterwok.demosimpletorrentstream.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


/**
 * This fragment pager adapter is responsible for displaying and configuring tabs
 * that implement [@TabFragmentPagerAdapter.TabFragment]
 */
class TabFragmentPagerAdapter<T, M : Any>(
        fm: FragmentManager
        , private vararg val fragments: T
) : FragmentPagerAdapter(fm) where T : TabFragmentPagerAdapter.TabFragment<M> {

    /**
     * This interface is used by the [@see TabFragmentManager] to get tab
     * titles and to configure the tab for some model of type [T].
     */
    interface TabFragment<T> {

        /**
         * Get the title of the tab.
         */
        fun getTitle(): String

        /**
         * Configure all tabs with the provided [model].
         */
        fun configure(model: T)

    }

    override fun getItem(position: Int): Fragment = fragments[position] as Fragment

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = fragments[position].getTitle()

    fun configure(model: M) = fragments.forEach {
        it.configure(model)
    }
}
