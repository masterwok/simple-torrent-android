package com.masterwok.demosimpletorrentstream.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masterwok.demosimpletorrentstream.R
import com.masterwok.demosimpletorrentstream.adapters.TabFragmentPagerAdapter
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus

class TorrentControlFragment : Fragment(), TabFragmentPagerAdapter.TabFragment<TorrentSessionStatus> {

    override fun onCreateView(
            inflater: LayoutInflater
            , container: ViewGroup?
            , savedInstanceState: Bundle?
    ): View = inflater.inflate(
            R.layout.fragment_torrent_control
            , container
            , false
    )

    override fun configure(model: TorrentSessionStatus) {
    }

    override fun getTitle(): String = "Control"

}