package com.masterwok.demosimpletorrentstream.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.masterwok.demosimpletorrentstream.adapters.managers.TabFragmentManager
import com.masterwok.demosimpletorrentstream.contracts.TabFragment
import com.masterwok.demosimpletorrentstream.fragments.TorrentControlFragment
import com.masterwok.demosimpletorrentstream.fragments.TorrentPiecesFragment
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus


class TorrentSessionPagerAdapter(
        fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    private val tabFragmentManager = TabFragmentManager(
            TorrentPiecesFragment::class.java to fun(): TabFragment<TorrentSessionStatus> { return TorrentPiecesFragment() }
            , TorrentControlFragment::class.java to fun(): TabFragment<TorrentSessionStatus> { return TorrentControlFragment() }
    )

    override fun getCount(): Int = tabFragmentManager.getTabCount()

    override fun getItem(position: Int): Fragment = tabFragmentManager.getTab(position) as Fragment

    override fun getPageTitle(position: Int): CharSequence? =
            tabFragmentManager.getTitle(position)

    fun configure(torrentSessionStatus: TorrentSessionStatus) = tabFragmentManager.configure(torrentSessionStatus)
}
