package com.kinandcarta.create.proxytoggle.tile

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.kinandcarta.create.proxytoggle.core.android.DeviceSettingsManager
import com.kinandcarta.create.proxytoggle.core.intent.getAppLaunchIntent
import com.kinandcarta.create.proxytoggle.core.settings.AppSettings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
@AndroidEntryPoint
class ProxyTileService : TileService() {

    @Inject
    lateinit var deviceSettingsManager: DeviceSettingsManager

    @Inject
    lateinit var appSettings: AppSettings

    override fun onClick() {
        toggleProxy()
    }

    override fun onStartListening() {
        updateTile()
    }

    private fun toggleProxy() {
        val proxy = deviceSettingsManager.proxySetting.value
        if (proxy.isEnabled) {
            deviceSettingsManager.disableProxy()
        } else {
            val lastUsedProxy = appSettings.lastUsedProxy
            if (lastUsedProxy.isEnabled) {
                deviceSettingsManager.enableProxy(lastUsedProxy)
            } else {
                // There is no last used Proxy, prompt the user to create one
                getAppLaunchIntent(baseContext)?.let { startActivityAndCollapse(it) }
            }
        }
        updateTile()
    }

    private fun updateTile() {
        val proxy = deviceSettingsManager.proxySetting.value
        if (proxy.isEnabled) {
            qsTile.apply {
                label = proxy.toString()
                state = Tile.STATE_ACTIVE
                updateTile()
            }
        } else {
            qsTile.apply {
                label = getString(R.string.no_proxy_tile)
                state = Tile.STATE_INACTIVE
                updateTile()
            }
        }
    }
}
