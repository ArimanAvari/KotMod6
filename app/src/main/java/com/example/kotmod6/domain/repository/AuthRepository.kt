package com.example.kotmod6.domain.repository

import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.domain.model.LoginSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val tokenFlow: Flow<String?>

    suspend fun login(username: String, password: String): LoginSession
    suspend fun loadUsers(): List<AppUser>
    suspend fun loadUser(id: Int): AppUser
    suspend fun logout()
}
