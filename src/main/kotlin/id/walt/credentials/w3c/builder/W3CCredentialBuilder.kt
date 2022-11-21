package id.walt.credentials.w3c.builder

import id.walt.credentials.w3c.*
import kotlinx.serialization.json.*
import java.time.Instant
import java.time.format.DateTimeFormatterBuilder

class W3CCredentialBuilder(type: List<String> = listOf("VerifiableCredential")):
  AbstractW3CCredentialBuilder<W3CCredential, W3CCredentialBuilder>(type, W3CCredential) {
    companion object {
      fun fromPartial(partialCredential: W3CCredential) = W3CCredentialBuilder(listOf()).setFromJsonObject(partialCredential.toJsonObject())
      fun fromPartial(partialJson: String) = fromPartial(W3CCredential.fromJson(partialJson))
    }
  }

abstract class AbstractW3CCredentialBuilder<C: W3CCredential, B: AbstractW3CCredentialBuilder<C, B>>(
  type: List<String>,
  val credentialFactory: CredentialFactory<C>
)
    : BasicBuilder<C, B>() {

  init {
    setProperty("type", type)
    setProperty("@context", listOf("https://www.w3.org/2018/credentials/v1"))
  }
  protected val dateFormat = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .appendInstant(0)
    .toFormatter()

  protected val subjectBuilder = SubjectBuilder()

  fun addContext(contextItem: W3CContext): B {
    setProperty("@context", properties["@context"]!!.jsonArray.plus(contextItem.toJsonElement()))
    return this as B
  }
  fun setId(id: String) = setProperty("id", id)
  fun setIssuer(issuer: String) = setProperty("issuer", issuer)
  fun setIssuer(issuer: W3CIssuer) = setProperty("issuer", issuer.toJsonElement())
  fun setIssuerId(issuerId: String) = setIssuer(properties["issuer"]?.let {
    W3CIssuer.fromJsonElement(it).apply {
      id = issuerId
    } } ?: W3CIssuer(issuerId))
  fun setIssuanceDate(date: Instant) = setProperty("issuanceDate", dateFormat.format(date))
  fun setValidFrom(date: Instant) = setProperty("validFrom", dateFormat.format(date))
  fun setExpirationDate(date: Instant) = setProperty("expirationDate", dateFormat.format(date))
  fun setCredentialSchema(schema: W3CCredentialSchema) = setProperty("credentialSchema", schema.toJsonObject() as JsonElement)
  fun setSubjectId(id: String) = buildSubject { setId(id) }
  fun buildSubject(builderAction: SubjectBuilder.() -> Unit): B {
    subjectBuilder.builderAction()
    return this as B
  }
  fun setProof(proof: W3CProof) = setProperty("proof", proof.toJsonObject() as JsonElement)

  override fun setFromJsonObject(jsonObject: JsonObject): B {
    super.setFromJsonObject(jsonObject)
    properties["credentialSubject"]?.let { subjectBuilder.setFromJsonObject(it.jsonObject) }
    return this as B
  }

  override fun build(): C {
    setProperty("credentialSubject", subjectBuilder.build().toJsonObject() as JsonElement)
    return credentialFactory.fromJsonObject(JsonObject(properties))
  }
}
