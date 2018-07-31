package com.masterwok.demosimpletorrentandroid.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


/**
 * This fragment pager adapter is responsible for displaying and configuring tabs
 * that implement [@TabFragmentPagerAdapter.TabFragment]
 */
class TabFragmentPagerAdapter<T, M : Any> constructor(
        fm: FragmentManager
) : FragmentPagerAdapter(fm) where T : TabFragmentPagerAdapter.TabFragment<M> {

    private var fragments: ArrayList<T> = ArrayList()

    constructor(
            fm: FragmentManager
            , vararg fragments: T
    ) : this(fm) {
        this.fragments = ArrayList(fragments.asList())
    }

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

    fun addTab(tab: T) {
        fragments.add(tab)

        notifyDataSetChanged()
    }

    fun configure(model: M) = fragments.forEach {
        it.configure(model)
    }
}
