package sync

data class SyncServiceConfig(val enabled: Boolean, val serverUrl: String?, val userCredential: UserCredential?)