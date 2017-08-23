/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.fhddsfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import play.api.http.Status
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.fhddsfrontend.AppUnitGenerator

import scala.concurrent.Future

class SoleTraderControllerSpec extends AppUnitGenerator {

  object soleTraderController extends Application(ds, mockFhddsConnector) {

    val fakeEnrolment = Set(
      Enrolment("", Seq(EnrolmentIdentifier("", "")), confidenceLevel = ConfidenceLevel.L200,
        state = "", delegatedAuthRule = Some(""))
    )

    override val authConnector: core.AuthConnector = mockAuthConnector

    def authorisedForUserMock(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
      when(authConnector.authorise(any(), any[Retrieval[~[Option[AffinityGroup], Enrolments]]]())(any()))
        .thenReturn(Future.successful(new ~[Option[AffinityGroup], Enrolments](Some(AffinityGroup.Agent), Enrolments(fakeEnrolment))))
  }

  //TODO more tests needed
  "SoleTraderController" should {
    soleTraderController.authorisedForUserMock()
    "information return 400" in {
      val result = csrfAddToken(soleTraderController.information("Individual"))(request)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "submitCheckResult return 400" in {
      val result = csrfAddToken(soleTraderController.showForm("Individual"))(request)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}