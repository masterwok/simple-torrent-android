package com.masterwok.simpletorrentstream

import com.frostwire.jlibtorrent.SessionParams
import com.frostwire.jlibtorrent.SettingsPack
import java.io.File

data class TorrentSessionOptions(
        var downloadLocation: File
) {
    private val settingsPack = SettingsPack()

    internal fun build(): SessionParams {
        return SessionParams(settingsPack)
    }

    class Builder {
        private lateinit var downloadLocation: File

        fun build(): TorrentSessionOptions = TorrentSessionOptions(
                downloadLocation
        )

        fun setDownloadLocation(downloadLocation: File): Builder {
            this.downloadLocation = downloadLocation
            return this
        }
    }
}

