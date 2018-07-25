package com.masterwok.simpletorrentstream

import com.frostwire.jlibtorrent.SessionParams
import com.frostwire.jlibtorrent.SettingsPack
import java.io.File

data class StreamOptions(
        var downloadLocation: File
) {
    private val settingsPack = SettingsPack()

    internal fun build(): SessionParams {
        return SessionParams(settingsPack)
    }
}

