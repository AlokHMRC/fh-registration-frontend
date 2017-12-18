package uk.gov.hmrc.fhddsfrontend.controllers

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.WsScalaTestClient
import play.api.http.HeaderNames
import play.api.test.WsTestClient
import uk.gov.hmrc.fhddsfrontend.testsupport.TestedApplication

class ApplicationControllerIntegrationSpec
  extends WordSpec
    with OptionValues
    with WsScalaTestClient
    with TestedApplication
    with WordSpecLike
    with Matchers
    with ScalaFutures {


  "Application" should {
    val baseUrl = s"http://localhost:$port/fhdds"
    "be reachable" in {
      given()
        .audit.writesAuditOrMerged()
      WsTestClient.withClient { client ⇒
        whenReady(client.url(s"http://localhost:$port/ping/ping").get()) {result ⇒
          result.status shouldBe 200
        }
      }
    }

//    //This test is used only useful when whitelisting is enabled
//    //TODO remove togheter with whitelisting
//    "not authorize w/o the whitelisting token" in {
//      when()
//        .audit.writesAuditOrMerged()
//      WsTestClient.withClient { client ⇒
//        whenReady(client.url(s"http://localhost:$port/fhdds").get()) { res ⇒
//          res.status shouldBe 401
//        }
//      }
//    }


    "/ redirects to the login page if the user is not logged in" in {
      given()
        .audit.writesAuditOrMerged()
        .user.isNotAuthorised()

      WsTestClient.withClient { client ⇒
        whenReady(client.url(s"http://localhost:$port/fhdds/whitelisted?p=123124").withFollowRedirects(false).get()) { res ⇒
          res.status shouldBe 303
          res.header(HeaderNames.LOCATION).get shouldBe "http://localhost:9025/gg/sign-in?continue=http%3A%2F%2Flocalhost%3A1118%2Fwhitelisted%3Fp%3D123124&origin=fhdds-frontend"
        }
      }
    }

    "/whitelisted redirects to the verification FE if logged in" in {
      given()
        .audit.writesAuditOrMerged()
        .user.isAuthorised()

      WsTestClient.withClient { client ⇒
        whenReady(client.url(s"http://localhost:$port/fhdds/whitelisted?p=123124").withFollowRedirects(false).get()) { res ⇒
          res.status shouldBe 303
          res.header(HeaderNames.LOCATION).get shouldBe "http://localhost:9227/verification/otac/login?p=123124"
        }
      }
    }

    "/ route will redirect to business customer FE when authorised" in {
      given()
        .audit.writesAuditOrMerged()
        .user.isAuthorised()

      WsTestClient.withClient { client ⇒
        val result = client.url(s"$baseUrl")
          .withFollowRedirects(false)
          .get()
        whenReady(result) { res ⇒
          res.status shouldBe 303
          res.header(HeaderNames.LOCATION) shouldBe Some("http://localhost:9923/business-customer/FHDDS?backLinkUrl=http://localhost:1118/fhdds/continue")
        }
      }
    }

    "/continue will redirect to dfs frontend when the user has a correct BPR" in {
      given()
        .audit.writesAuditOrMerged()
        .user.isAuthorised()
        .fhddsBackend.hasBusinessDetails()
        .businessCustomerFrontend.hasBusinessPartnerRecord()


      WsTestClient.withClient { client ⇒
        val result = client.url(s"$baseUrl/continue")
          .withFollowRedirects(false)
          .get()

        whenReady(result) { res ⇒
          res.status shouldBe 303
          res.header(HeaderNames.LOCATION) shouldBe Some(s"http://$wiremockHost:$wiremockPort/fhdds-forms/forms/form/fhdds-limited-company/new")
        }
      }


    }

  }

}
