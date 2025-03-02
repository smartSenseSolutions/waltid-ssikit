package id.walt.signatory

import id.walt.common.createBaseToken
import id.walt.common.deriveRevocationToken
import id.walt.servicematrix.ServiceMatrix
import id.walt.signatory.rest.SignatoryRestAPI
import id.walt.signatory.revocation.SimpleCredentialStatus2022Service
import id.walt.test.RESOURCES_PATH
import io.kotest.core.spec.style.AnnotationSpec

class RevocationClientTest : AnnotationSpec() {

    init {
        ServiceMatrix("$RESOURCES_PATH/service-matrix.properties")
        SimpleCredentialStatus2022Service.clearRevocations()
    }

    private val SIGNATORY_API_HOST = "localhost"
    private val SIGNATORY_API_PORT = 7001
    private val SIGNATORY_API_URL = "http://$SIGNATORY_API_HOST:$SIGNATORY_API_PORT"

    @BeforeClass
    fun startServer() {
        SignatoryRestAPI.start(SIGNATORY_API_PORT)
    }

    @AfterClass
    fun teardown() {
        SignatoryRestAPI.stop()
    }

//    @Test TODO: fix
    fun test() {
        val revocationsBase = "$SIGNATORY_API_URL/v1/revocations"

        val rs = RevocationClientService.getService()

        val baseToken = createBaseToken()
        println(baseToken)

        val revocationToken = deriveRevocationToken(baseToken)
        println(revocationToken)

        var result = rs.checkRevoked("$revocationsBase/$revocationToken")
        println(result)

        rs.revoke("$revocationsBase/$baseToken")

        result = rs.checkRevoked("$revocationsBase/$revocationToken")
        println(result)

    }
}
