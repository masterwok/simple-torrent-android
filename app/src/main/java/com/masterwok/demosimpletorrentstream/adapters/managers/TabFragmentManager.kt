package com.masterwok.demosimpletorrentstream.adapters.managers

import android.support.v4.app.Fragment
import com.masterwok.demosimpletorrentstream.contracts.TabFragment


/**
 * This class is responsible for managing tab fragments. Specifically from the
 * parent [@see Activity]. When tabs are requested, they are created through the
 * corresponding factory methods provided to the [@see constructor]. The tabs can
 * be configured using some model of type [T]. The tabs will be added in the order
 * they were provided.
 */
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

    /**
     * Get the tab count.
     */
    fun getTabCount(): Int = tabFactoryArray.size

    /**
     * Get the tab at [position].
     */
    fun getTab(position: Int): TabFragment<T> = getTabFragment(position)

    /**
     * Get tab title at [position].
     */
    fun getTitle(position: Int): CharSequence? = getTabFragment(position)
            .getTitle()

    /**
     * Configure all fragments using the provided [model].
     */
    fun configure(model: T) = tabInstanceMapping.values.forEach {
        it.configure(model)
    }

}
