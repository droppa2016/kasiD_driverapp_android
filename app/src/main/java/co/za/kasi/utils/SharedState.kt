package co.za.kasi.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Extension to get DataStore instance
private val Context.dataStore by preferencesDataStore(name = "sync_data_store")

object SharedState {
    private val _awaitingSync = MutableStateFlow(0)
    val awaitingSync = _awaitingSync.asStateFlow()

    private val _syncedWaybills = MutableStateFlow(0)
    val syncedWaybills = _syncedWaybills.asStateFlow()

    private val _syncRequired = MutableStateFlow(false)
    val syncRequired = _syncRequired.asStateFlow()

    private val _percentage = MutableStateFlow(0)
    val percentage = _percentage.asStateFlow()

    private val _outOfText = MutableStateFlow("0/0")
    val outOfText = _outOfText.asStateFlow()

    fun observeDataStore(owner: LifecycleOwner, context: Context) {
        owner.lifecycleScope.launch {
            SyncDataStore.awaitingSyncFlow(context).collect {
                _awaitingSync.value = it
                updateProgress(_syncedWaybills.value, it)
            }
        }

        owner.lifecycleScope.launch {
            SyncDataStore.syncedWaybillsFlow(context).collect {
                _syncedWaybills.value = it
                updateProgress(it, _awaitingSync.value)
            }
        }

        owner.lifecycleScope.launch {
            SyncDataStore.syncRequiredFlow(context).collect {
                _syncRequired.value = it
            }
        }
    }

    suspend fun updateAwaitingSync(context: Context, value: Int) {
        SyncDataStore.saveAwaitingSync(context, value)
    }

    suspend fun updateSyncedWaybills(context: Context, value: Int) {
        SyncDataStore.saveSyncedWaybills(context, value)
    }

    suspend fun updateSyncRequired(context: Context, value: Boolean) {
        SyncDataStore.saveSyncRequired(context, value)
    }

    internal fun updateProgress(current: Int, total: Int) {
        _percentage.value = if (total == 0) 0 else ((current.toDouble() / total) * 100).toInt()
        _outOfText.value = "$current/$total"
    }
}

object SyncDataStore {
    private val AWAITING_SYNC = intPreferencesKey("awaiting_sync")
    private val SYNCED_WAYBILLS = intPreferencesKey("synced_waybills")
    private val SYNC_REQUIRED = booleanPreferencesKey("sync_required")

    suspend fun saveAwaitingSync(context: Context, value: Int) {
        context.dataStore.edit { it[AWAITING_SYNC] = value }
    }

    suspend fun saveSyncedWaybills(context: Context, value: Int) {
        context.dataStore.edit { it[SYNCED_WAYBILLS] = value }
    }

    suspend fun saveSyncRequired(context: Context, value: Boolean) {
        context.dataStore.edit { it[SYNC_REQUIRED] = value }
    }

    fun awaitingSyncFlow(context: Context): Flow<Int> =
        context.dataStore.data.map { it[AWAITING_SYNC] ?: 0 }

    fun syncedWaybillsFlow(context: Context): Flow<Int> =
        context.dataStore.data.map { it[SYNCED_WAYBILLS] ?: 0 }

    fun syncRequiredFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[SYNC_REQUIRED] ?: false
        }
}