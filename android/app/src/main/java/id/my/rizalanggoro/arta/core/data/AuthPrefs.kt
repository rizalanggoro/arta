package id.my.rizalanggoro.arta.core.data

import android.content.Context
import id.my.rizalanggoro.arta.domain.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class AuthPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    init {
        val restored = getSession()
        if (restored != null) _currentSession.value = restored
    }

    fun saveSession(session: AuthSession) {
        val value = json.encodeToString(AuthSession.serializer(), session)
        prefs.edit().putString(KEY_SESSION, value).apply()
        _currentSession.value = session
    }

    fun setSession(session: AuthSession?) {
        if (session == null) {
            clear()
            _currentSession.value = null
            return
        }
        saveSession(session)
    }

    fun getSession(): AuthSession? {
        val raw = prefs.getString(KEY_SESSION, null) ?: return null
        return try {
            json.decodeFromString(AuthSession.serializer(), raw)
        } catch (t: Throwable) {
            null
        }
    }

    fun clear() {
        prefs.edit().remove(KEY_SESSION).apply()
        _currentSession.value = null
    }

    companion object {
        private const val KEY_SESSION = "auth_session"
    }
}
