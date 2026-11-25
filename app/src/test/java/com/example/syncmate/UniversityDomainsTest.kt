package com.example.syncmate

import com.example.syncmate.data.util.UniversityDomains
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UniversityDomainsTest {

    @Test
    fun `exact match for verified university domains returns true`() {
        assertTrue(UniversityDomains.isAllowedUniversityDomain("student@iitm.ac.in"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("researcher@annauniv.edu"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("alumni@bits-pilani.ac.in"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("scholar@vit.ac.in"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("campus@srmist.edu.in"))
    }

    @Test
    fun `subdomain match for verified university departments returns true`() {
        assertTrue(UniversityDomains.isAllowedUniversityDomain("student@smail.iitm.ac.in"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("faculty@cse.iitb.ac.in"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("hostel@pg.du.ac.in"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("user@dept.nitt.edu"))
    }

    @Test
    fun `case insensitivity check passes for uppercase and mixed case emails`() {
        assertTrue(UniversityDomains.isAllowedUniversityDomain("STUDENT@IITM.AC.IN"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("User@AnnaUniv.Edu"))
        assertTrue(UniversityDomains.isAllowedUniversityDomain("Scholar@BITS-PILANI.AC.IN"))
    }

    @Test
    fun `generic commercial email providers are strictly rejected`() {
        assertFalse(UniversityDomains.isAllowedUniversityDomain("user@gmail.com"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("user@yahoo.com"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("user@outlook.com"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("user@hotmail.com"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("user@proton.me"))
    }

    @Test
    fun `spoofed lookalike domains with university prefix are strictly rejected`() {
        assertFalse(UniversityDomains.isAllowedUniversityDomain("attacker@fakedu.ac.in.scam.com"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("phish@iitm.ac.in.badsite.org"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("scam@annauniv.edu.evil.net"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("fraud@vit.ac.in-phish.ru"))
    }

    @Test
    fun `malformed and empty email inputs return false`() {
        assertFalse(UniversityDomains.isAllowedUniversityDomain(""))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("   "))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("noatsign"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("@"))
        assertFalse(UniversityDomains.isAllowedUniversityDomain("@iitm.ac.in"))
    }
}
