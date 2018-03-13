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

package uk.gov.hmrc.fhregistrationfrontend.connectors

import uk.gov.hmrc.play.config.{RunMode, ServicesConfig}

object ExternalUrls extends RunMode with ServicesConfig {

  val companyAuthUrl: String = getConfString("auth.company-auth.url", throw new RuntimeException("Company auth url required"))
  val loginCallback: String = getConfString("auth.login-callback.url", "")
  val loginPath: String = getConfString("auth.login_path", "")

  val logoutCallback: String = getConfString("auth.logout-callback.url", "")
  val logoutPath: String = getConfString("auth.logout_path", "")

  val ggLoginUrl = s"$companyAuthUrl$loginPath"
  val continueUrl = s"$loginCallback"

  val ggLogoutUrl = s"$companyAuthUrl$logoutPath"
  val logoutContinueUrl = s"$logoutCallback"

  val surveyRedirectUrl = getConfString("services.surveyRedirectUrl", "feedback-survey?origin=fhdds")

}
