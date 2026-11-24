package com.example.syncmate.data.util

object PasswordValidator {
    /**
     * Enforces password-strength validation:
     * - Minimum 8 characters
     * - At least one uppercase letter (A-Z)
     * - At least one lowercase letter (a-z)
     * - At least one digit (0-9)
     */
    fun validate(password: String): Result<Unit> {
        if (password.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters long."))
        }
        if (!password.any { it.isUpperCase() }) {
            return Result.failure(IllegalArgumentException("Password must contain at least one uppercase letter (A-Z)."))
        }
        if (!password.any { it.isLowerCase() }) {
            return Result.failure(IllegalArgumentException("Password must contain at least one lowercase letter (a-z)."))
        }
        if (!password.any { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("Password must contain at least one number (0-9)."))
        }
        return Result.success(Unit)
    }
}
