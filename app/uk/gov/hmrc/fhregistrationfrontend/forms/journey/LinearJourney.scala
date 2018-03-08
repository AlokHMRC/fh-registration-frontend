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

package uk.gov.hmrc.fhregistrationfrontend.forms.journey
import uk.gov.hmrc.fhregistrationfrontend.forms.journey.Page.AnyPage
import uk.gov.hmrc.fhregistrationfrontend.forms.navigation.{FormPage, Navigation}

class LinearJourney(val journeyPages: JourneyPages) extends JourneyNavigation {

  val pages = journeyPages.pages

  override def next[_](pageId: String): Option[AnyPage] = {
    pages dropWhile (_.id != pageId) match {
      case page :: next :: rest ⇒ Some(next)
      case _                    ⇒ None
    }
  }

  override def previous(pageId: String): Option[AnyPage] = {
    pages takeWhile (_.id != pageId) lastOption
  }

  override def navigation(lastUpdateTime: Long, pageId: String): Navigation = {
    Navigation(lastUpdateTime, previous(pageId) map {page ⇒ FormPage(page.id)})
  }
}