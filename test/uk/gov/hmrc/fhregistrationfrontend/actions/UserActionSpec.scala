/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.fhregistrationfrontend.actions

import java.io.IOException

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.fhregistrationfrontend.teststubs.StubbedExternalUrls

import scala.concurrent.Future

class UserActionSpec extends ActionSpecBase {

  val mockAuthConnector = mock[AuthConnector]

  type RetrievalType = Option[String] ~ Option[String] ~ Enrolments

  val fakeRequest: Request[Any] = FakeRequest()
  lazy val action = new UserAction(StubbedExternalUrls)(mockAuthConnector)

  "UserAction" should {
    "Find the internal user id and email with no enrolments " in {

      setupAuthConnector()
      val refined = refinedRequest(action, fakeRequest)
      refined.userId shouldBe "id"
      refined.ggEmail shouldBe Some("email@test.com")
      refined.registrationNumber shouldBe None
      refined.userIsRegistered shouldBe false
    }

    "Find the fhdds Enrolment" in {
      val fhddsEnrolment = EnrolmentIdentifier("EtmpRegistrationNumber", "XZFH00000123456")
      val otherEnrolment = EnrolmentIdentifier("EtmpRegistrationNumber", "XZSDIL000123456")

      val enrolments = Set(
        new Enrolment("HMRC-OBTDS-ORG", Seq(otherEnrolment), "Active"),
        new Enrolment("HMRC-OBTDS-ORG", Seq(fhddsEnrolment), "Active")
      )

      setupAuthConnector(enrolments = enrolments)
      val refined = refinedRequest(action, fakeRequest)
      refined.registrationNumber shouldBe Some("XZFH00000123456")
      refined.userIsRegistered shouldBe true
    }

    "Redirect to gg if user is not logged in" in {
      setupAuthConnector(MissingBearerToken())

      val r = result(action, fakeRequest)
      status(r) shouldBe SEE_OTHER
      redirectLocation(r) shouldBe Some("company/authlogin/path?continue=%2Ffhdds&origin=FHDDS")
    }

    "Fail if user id is not defined" in {
      setupAuthConnector(id = None)

      status(result(action, fakeRequest)) shouldBe UNAUTHORIZED
    }

    "Return internal server error on failed connection" in {
      setupAuthConnector(new IOException())
      status(result(action, fakeRequest)) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  def setupAuthConnector(
    id        : Option[String] = Some("id"),
    email     : Option[String] = Some("email@test.com"),
    enrolments: Set[Enrolment] = Set.empty
  ) = {
    val authResult = Future successful (new ~(new ~(id, email), new Enrolments(enrolments)))
    when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn authResult
  }

  def setupAuthConnector(throwable: Throwable) = {
    when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed throwable)
  }


}
