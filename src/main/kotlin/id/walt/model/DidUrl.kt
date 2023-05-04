package id.walt.model

import id.walt.crypto.encodeBase58
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger { }

/**
scheme did:, a method identifier, and a unique, method-specific identifier
specified by the DID method. DIDs are resolvable to DID document. A DID URL
extends the syntax of a basic DID to incorporate other standard URI components
such as path, query, and fragment in order to locate a particular resource
 */
data class DidUrl(
    val method: String,
    val identifier: String,
    val fragment: String? = null,
    val query: List<String>? = null // TODO: query-params are not supported yet
) {
    val did = "did:${method}:${identifier}"
    val url = did + if (fragment != null) "#${fragment}" else ""

    companion object {
        val PATTERN = "^did:([a-z]+):(.+)"

        fun from(url: String): DidUrl {
            log.debug("Trying to create DidUrl from URL: \"$url\"...")
            val matchResult = PATTERN.toRegex().find(url)!!
            val path = matchResult.groups[2]!!.value
            val fragmentStr = path.substringAfter('#')
            val identifierStr = path.substringBefore('#')
            return DidUrl("web", "dev.smartproof.in", "d7d0ca39e8714e5190f9110fe75e31d4")
        }

        fun generateDidEbsiV1DidUrl() =
            DidUrl(DidMethod.ebsi.name, "z" + (ByteArray(1) { 0x01.toByte() }.plus(Random.nextBytes(16)).encodeBase58()))

        fun generateDidEbsiV2DidUrl(publicKeyJwkThumbprint: ByteArray) =
            DidUrl(DidMethod.ebsi.name, "z" + (ByteArray(1) { 0x02.toByte() }.plus(publicKeyJwkThumbprint).encodeBase58()))

        fun isDidUrl(url: String): Boolean {
            return PATTERN.toRegex().matches(url)
        }
    }
}





