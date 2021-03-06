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

import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.fhregistrationfrontend.teststubs.StubbedErrorHandler

class EnrolledUserActionSpec extends ActionSpecBase {

  lazy val action = new EnrolledUserAction()(StubbedErrorHandler)

  "Enrolled user action" should {
    "Fail when no registration number" in {
      val userRequest = new UserRequest(testUserId, None, registrationNumber = None, FakeRequest())

      status(result(action, userRequest)) shouldBe BAD_REQUEST
    }

    "Work for user with registration number" in {
      val userRequest = new UserRequest(testUserId, None, Some(registrationNumber), FakeRequest())

      val refined = refinedRequest(action, userRequest)

      refined.userId shouldBe testUserId
      refined.registrationNumber shouldBe registrationNumber


    }
  }
}
