package com.example.data

import kotlinx.coroutines.flow.Flow

class FocusRepository(private val focusDao: FocusDao) {
    val allSessions: Flow<List<FocusSession>> = focusDao.getAllSessions()

    suspend fun insertSession(session: FocusSession) {
        focusDao.insertSession(session)
    }

    suspend fun clearHistory() {
        focusDao.clearHistory()
    }
}
