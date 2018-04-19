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

package uk.gov.hmrc.fhregistrationfrontend.config

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val exitSurveyUrl: String
  val phaseBannerFeedback: String
  val phaseBannerFeedbackUnauth: String
  val appName: String
}

@Singleton
class FrontendAppConfig @Inject()(configuration: play.api.Configuration) extends AppConfig with ServicesConfig {

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

//  private val contactHost = configuration.getString(s"contact-frontend.host").getOrElse("")

  lazy val contactFrontend: String = getConfString("contact-frontend-url-base", "")

  override lazy val appName: String = loadConfig("appName")

  private val contactFormServiceIdentifier = appName
  override lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  override lazy val analyticsHost: String = loadConfig(s"google-analytics.host")
  override lazy val reportAProblemPartialUrl = s"$contactFrontend/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactFrontend/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override lazy val exitSurveyUrl: String = s"$contactFrontend/contact/beta-feedback?service=$contactFormServiceIdentifier"

  override lazy val phaseBannerFeedback: String = s"$contactFrontend/contact/beta-feedback?service=$contactFormServiceIdentifier"
  override lazy val phaseBannerFeedbackUnauth: String = s"$contactFrontend/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"
}

object FrontendAppConfig {
  lazy val config = new FrontendAppConfig(Configuration.load(play.Environment.simple().underlying()))
}