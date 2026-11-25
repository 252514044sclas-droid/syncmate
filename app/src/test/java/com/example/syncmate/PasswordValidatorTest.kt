package com.example.syncmate

import com.example.syncmate.data.util.PasswordValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordValidatorTest {

    @Test
    fun `empty password returns failure`() {
        val result = PasswordValidator.validate("")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("at least 8 characters") == true)
    }

    @Test
    fun `short password less than 8 characters returns failure`() {
        val result = PasswordValidator.validate("Ab1!xyz")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("at least 8 characters") == true)
    }

    @Test
    fun `password missing uppercase letter returns failure`() {
        val result = PasswordValidator.validate("weakpass123!")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("uppercase") == true)
    }

    @Test
    fun `password missing lowercase letter returns failure`() {
        val result = PasswordValidator.validate("WEAKPASS123!")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("lowercase") == true)
    }

    @Test
    fun `password missing digit returns failure`() {
        val result = PasswordValidator.validate("WeakPassword!")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("number") == true)
    }

    @Test
    fun `password missing special character returns failure`() {
        val result = PasswordValidator.validate("WeakPassword123")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("special character") == true)
    }

    @Test
    fun `strong valid password passes validation`() {
        val result = PasswordValidator.validate("SyncMate2026!#Secure")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `standard 8-character compliant password passes validation`() {
        val result = PasswordValidator.validate("Aa1@bcde")
        assertTrue(result.isSuccess)
    }
}
