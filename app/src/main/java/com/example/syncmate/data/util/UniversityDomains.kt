package com.example.syncmate.data.util

object UniversityDomains {
    /**
     * Verified university domain allow-list.
     * Matches exact domain or university subdomains (e.g. smail.iitm.ac.in -> iitm.ac.in).
     */
    val ALLOWED_DOMAINS = setOf(
        // Indian Universities & Premier Institutes
        "iitm.ac.in",
        "iitb.ac.in",
        "iitd.ac.in",
        "iitk.ac.in",
        "iitkgp.ac.in",
        "iitr.ac.in",
        "iitg.ac.in",
        "iith.ac.in",
        "annauniv.edu",
        "srmist.edu.in",
        "vit.ac.in",
        "vitstudent.ac.in",
        "du.ac.in",
        "jnu.ac.in",
        "nitt.edu",
        "nitk.edu.in",
        "nitw.ac.in",
        "manipal.edu",
        "bits-pilani.ac.in",
        "amrita.edu",
        "ssn.edu.in",
        "psgtech.edu",
        "sastra.edu",
        "unom.ac.in",
        "iiit.ac.in",
        // Universal Academic / Student TLDs
        "edu",
        "ac.in",
        "edu.in"
    )

    fun isAllowedUniversityDomain(email: String): Boolean {
        val domain = email.substringAfter("@", "").trim().lowercase()
        if (domain.isBlank()) return false
        if (ALLOWED_DOMAINS.contains(domain)) return true
        return ALLOWED_DOMAINS.any { allowed ->
            domain == allowed || domain.endsWith(".$allowed")
        }
    }
}
