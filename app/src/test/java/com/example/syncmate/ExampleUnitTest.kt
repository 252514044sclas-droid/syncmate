package com.example.syncmate

import com.example.syncmate.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCurrencyFormatting() {
        assertEquals("₹850", CurrencyFormatter.formatInr(850))
        assertEquals("₹8,500", CurrencyFormatter.formatInr(8500))
        assertEquals("₹15,000", CurrencyFormatter.formatInr(15000))
        assertEquals("₹15,000/mo", CurrencyFormatter.formatMonthlyRent(15000))
        assertEquals("₹15,000/month", CurrencyFormatter.formatMonthlyRentFull(15000))
        assertEquals("₹1,25,000", CurrencyFormatter.formatInr(125000))
        assertEquals("₹12,000 - ₹16,500", CurrencyFormatter.formatInrRange(12000, 16500))
    }
}

