package com.masterwok.demosimpletorrentstream.activities

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import com.masterwok.demosimpletorrentstream.R
import com.masterwok.demosimpletorrentstream.adapters.TabFragmentPagerAdapter
import com.masterwok.demosimpletorrentstream.fragments.TorrentFragment
import com.masterwok.simpletorrentstream.TorrentSessionOptions
import com.masterwok.simpletorrentstream.extensions.appCompatRequestPermissions
import com.masterwok.simpletorrentstream.extensions.isPermissionGranted
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var buttonAddTorrent: AppCompatButton

    private val magnets = arrayOf(
            "https://webtorrent.io/torrents/sintel.torrent"
            , "magnet:?xt=urn:btih:1815a467da2820aea936b622c09966abed626c9c&dn=Alien.1979.Directors.Cut.1080p.BluRay.H264.AAC-RARBG&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
            , "magnet:?xt=urn:btih:d9d9785105166a3a93da6e1f09bd062142a2e2f4&dn=The+Edge+%281997%29+720p+BrRip+x264+-+600MB+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
            , "magnet:?xt=urn:btih:2d6354d22bbda47b22ab65066b8736d9851bb493&dn=Grandmas+Boy+UNRATED+2006+720p+WEB-DL+x264+AAC+-+Ozlem&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
            , "magnet:?xt=urn:btih:608c9e4070398f02757492cf3817783ee93fa32d&dn=Hackers+%281995%29+720p+BrRip+x264+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
    )

    private val torrentSessionOptions = TorrentSessionOptions
            .Builder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
            .setAnonymousMode(true)
            .build()

    private val torrentSessionPagerAdapter = TabFragmentPagerAdapter<TorrentFragment, TorrentSessionStatus>(
            supportFragmentManager
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViewComponents()
        subscribeToViewComponents()
        initTabLayout()

        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            appCompatRequestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    , 0
            )

            return
        }
    }

    private fun initTabLayout() {
        viewPager.adapter = torrentSessionPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun subscribeToViewComponents() {
        buttonAddTorrent.setOnClickListener {
            val tabFragment = TorrentFragment.newInstance(
                    torrentSessionPagerAdapter.count + 1
                    , magnets[torrentSessionPagerAdapter.count]
                    , torrentSessionOptions
            )

            torrentSessionPagerAdapter.addTab(tabFragment)
        }
    }

    private fun bindViewComponents() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        buttonAddTorrent = findViewById(R.id.button_add_torrent)
    }

}
