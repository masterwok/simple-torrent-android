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
            TorrentControlFragment::class.java to fun(): TabFragment<TorrentSessionStatus> { return TorrentControlFragment() }
            , TorrentPiecesFragment::class.java to fun(): TabFragment<TorrentSessionStatus> { return TorrentPiecesFragment() }
    )

    override fun getCount(): Int = tabFragmentManager.getCount()

    override fun getItem(position: Int): Fragment = tabFragmentManager.getItem(position) as Fragment

    override fun getPageTitle(position: Int): CharSequence? =
            tabFragmentManager.getPageTitle(position)

    fun configure(torrentSessionStatus: TorrentSessionStatus) = tabFragmentManager.configure(torrentSessionStatus)
}
