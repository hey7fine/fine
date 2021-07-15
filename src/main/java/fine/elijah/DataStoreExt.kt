package fine.elijah

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> DataStore<Preferences>.getPreference(key: Preferences.Key<T>): Flow<T?> = data.map { it[key] }

suspend fun <T> DataStore<Preferences>.savePreference(key:Preferences.Key<T>,value:T) = edit {
    it[key] = value
}