package com.masterwok.demosimpletorrentandroid.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masterwok.demosimpletorrentandroid.R
import com.masterwok.demosimpletorrentandroid.adapters.TabFragmentPagerAdapter
import com.masterwok.demosimpletorrentandroid.adapters.TorrentPieceAdapter
import com.masterwok.demosimpletorrentandroid.extensions.setSupportsChangeAnimations
import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus


class TorrentPiecesFragment : Fragment(), TabFragmentPagerAdapter.TabFragment<TorrentSessionStatus> {

    private lateinit var recyclerView: RecyclerView

    private val torrentPieceAdapter: TorrentPieceAdapter = TorrentPieceAdapter()

    override fun onCreateView(
            inflater: LayoutInflater
            , container: ViewGroup?
            , savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
                R.layout.fragment_torrent_pieces
                , container
                , false
        )

        bindViewComponents(view)
        initPiecesRecyclerView()

        return view
    }

    private fun bindViewComponents(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_pieces)
    }

    private fun initPiecesRecyclerView() {
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 16)
            adapter = torrentPieceAdapter
            setSupportsChangeAnimations(false)
        }
    }

    override fun getTitle(): String = "Pieces"

    override fun configure(model: TorrentSessionStatus) {
        activity?.runOnUiThread {
            torrentPieceAdapter.configure(model.torrentSessionBuffer)

        }
    }
}
