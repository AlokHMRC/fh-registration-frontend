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

package uk.gov.hmrc.fhregistrationfrontend.controllers

import uk.gov.hmrc.auth.otac.{Authorised, OtacAuthConnector, OtacFailureThrowable}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

/** TODO remove this when private beta ends*/
trait Whitelisting {

  def otacAuthConnector: OtacAuthConnector

  def usewhiteListing: Boolean

  def withVerifiedPasscode[T](serviceName: String, otacToken: Option[String])(body: => Future[T])
    (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[T] = {

    if (usewhiteListing)

      otacAuthConnector.authorise(serviceName, headerCarrier, otacToken).flatMap {
        case Authorised  => body
        case otherResult => Future.failed(OtacFailureThrowable(otherResult))
      }
    else
      body
  }

}