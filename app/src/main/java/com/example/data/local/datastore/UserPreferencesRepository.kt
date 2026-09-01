package com.example.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aldaw_user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private object PreferencesKeys {
        val USER_ROLE = stringPreferencesKey("user_role")
        val WALLET_BALANCE = doublePreferencesKey("wallet_balance")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val REGION = stringPreferencesKey("user_region")
    }

    val userRoleFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_ROLE] ?: "CUSTOMER"
        }

    val walletBalanceFlow: Flow<Double> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.WALLET_BALANCE] ?: 150000.0
        }

    suspend fun setUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ROLE] = role
        }
    }

    suspend fun setWalletBalance(balance: Double) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WALLET_BALANCE] = balance
        }
    }
}
