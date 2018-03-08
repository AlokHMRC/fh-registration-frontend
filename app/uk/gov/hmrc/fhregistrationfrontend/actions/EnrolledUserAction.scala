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

import play.api.Logger
import play.api.i18n.Messages
import play.api.mvc.{ActionRefiner, Result, Results, WrappedRequest}
import uk.gov.hmrc.fhregistrationfrontend.controllers.UnexpectedState

import scala.concurrent.Future

class EnrolledUserRequest[A](
  val registrationNumber: String,
  request    : UserRequest[A]
) extends WrappedRequest[A](request) {

  def userId: String = request.userId
  def email: Option[String] = request.email
}

object EnrolledUserAction {
  def apply()(implicit messages: Messages) = UserAction andThen new EnrolledUserAction

}

class EnrolledUserAction (implicit val messages: Messages)
  extends FrontendAction
    with ActionRefiner[UserRequest, EnrolledUserRequest] with UnexpectedState {

  override protected def refine[A](request: UserRequest[A]): Future[Either[Result, EnrolledUserRequest[A]]] = {
    implicit val r = request
    request.registrationNumber match {
      case Some(registrationNumber) ⇒
        Future successful Right(new EnrolledUserRequest[A](registrationNumber, request))
      case None ⇒
        Logger.error(s"Not found: registration number")
        Future successful Left(errorResultsPages(Results.NotFound))
    }
  }

}