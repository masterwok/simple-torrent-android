package com.masterwok.demosimpletorrentstream.adapters.managers

import android.support.v4.app.Fragment
import com.masterwok.demosimpletorrentstream.contracts.TabFragment


class TabFragmentManager<T : Any> constructor(
        private vararg val tabFactoryArray: Pair<Class<out Fragment>, () -> TabFragment<T>>
) {
    private val tabInstanceMapping = HashMap<Class<out Fragment>, TabFragment<T>>()

    private fun getTabFragment(position: Int): TabFragment<T> {
        val tabFactoryPair = tabFactoryArray[position]
        val fragmentClass = tabFactoryPair.first
        var tabFragment = tabInstanceMapping[fragmentClass]

        if (tabFragment == null) {
            tabFragment = tabFactoryPair.second()

            tabInstanceMapping[fragmentClass] = tabFragment
        }

        return tabFragment
    }

    fun getCount(): Int = tabFactoryArray.size

    fun getItem(position: Int): TabFragment<T> = getTabFragment(position)

    fun getPageTitle(position: Int): CharSequence? = getTabFragment(position)
            .getTitle()

    fun configure(model: T) = tabInstanceMapping.values.forEach {
        it.configure(model)
    }

}
