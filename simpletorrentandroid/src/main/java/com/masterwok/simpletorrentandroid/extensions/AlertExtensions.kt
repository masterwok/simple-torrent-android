package com.masterwok.simpletorrentandroid.extensions

import com.frostwire.jlibtorrent.alerts.Alert
import com.frostwire.jlibtorrent.alerts.TorrentAlert

/**
 * Check if the [Alert] is of type [TorrentAlert].
 */
internal fun Alert<*>.isTorrentAlert() = this is TorrentAlert


/**
 * Check if the [Alert] has a valid [@see TorrentHandle].
 */
internal fun Alert<*>.hasValidTorrentHandle() = (this as? TorrentAlert)
        ?.handle()
        ?.isValid
        ?: false

