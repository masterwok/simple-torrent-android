package com.masterwok.demosimpletorrentstream.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.masterwok.demosimpletorrentstream.contracts.TabbedFragment
import com.masterwok.demosimpletorrentstream.fragments.TorrentControlFragment
import com.masterwok.demosimpletorrentstream.fragments.TorrentPiecesFragment

class TorrentSessionFragmentPagerAdapter(
        fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    private val fragmentFactoryArray: Array<Pair<Class<out Fragment>, () -> Fragment>> = arrayOf(
            TorrentControlFragment::class.java to fun(): Fragment { return TorrentControlFragment() }
            , TorrentPiecesFragment::class.java to fun(): Fragment { return TorrentPiecesFragment() }
    )

    private val fragmentInstanceMapping = HashMap<Class<out Fragment>, Fragment>()

    override fun getItem(position: Int): Fragment {
        val fragmentFactoryPair = fragmentFactoryArray[position]
        var fragment: Fragment? = fragmentInstanceMapping[fragmentFactoryPair.first]

        if (fragment == null) {
            fragment = fragmentFactoryPair.second()
            fragmentInstanceMapping[fragmentFactoryPair.first] = fragment
        }

        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val fragment = getItem(position) as? TabbedFragment<*>

        return fragment?.getTabTitle() ?: position.toString()
    }

    override fun getCount(): Int = fragmentFactoryArray.size

}