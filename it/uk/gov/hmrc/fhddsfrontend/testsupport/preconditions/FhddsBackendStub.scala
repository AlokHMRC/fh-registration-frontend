package uk.gov.hmrc.fhddsfrontend.testsupport.preconditions

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.libs.json.Json
import uk.gov.hmrc.fhddsfrontend.models.businessregistration.{Address, BusinessRegistrationDetails}

case class FhddsBackendStub()
  (implicit builder: PreconditionBuilder)
{
  import BusinessRegistrationDetails.formats


  private val aFakeAddress = Address(
    line1 = "line1",
    line2 = "line2",
    line3 = None,
    line4 = None,
    postcode = Some("NE98 1ZZ"),
    country = "GB")

  private def mkBusinessPartnerRecord() = {
    BusinessRegistrationDetails(businessName = "Real Business Inc",
      businessType = Some("corporate body"),
      businessAddress = aFakeAddress,
      sapNumber = "1234567890",
      safeId = "XE0001234567890",
      agentReferenceNumber = Some("JARN1234567"),
      firstName = None,
      lastName = None,
      utr = Some("1111111111"),
      identification = None)
  }

  def hasBusinessDetails() = {
    stubFor(
      put(urlPathEqualTo(
        s"/fhdds/submission-extra-data/some-id/fhdds-limited-company/businessRegistrationDetails")
      )
      .willReturn(ok(
        Json.toJson(mkBusinessPartnerRecord()).toString()
      ))
    )
    builder
  }

}
