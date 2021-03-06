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

import javax.inject.Inject

import uk.gov.hmrc.fhregistrationfrontend.actions.Actions
import uk.gov.hmrc.fhregistrationfrontend.forms.journey.Journeys
import uk.gov.hmrc.fhregistrationfrontend.forms.models.BusinessType
import uk.gov.hmrc.fhregistrationfrontend.services.Save4LaterService


@Inject
class SummaryController @Inject()(
  ds                   : CommonPlayDependencies,
  actions: Actions
) extends AppController(ds)  with SummaryFunctions {

  import actions._
  def summary() = summaryAction { implicit request ⇒

    val application = request.businessType match {
      case BusinessType.CorporateBody ⇒
        Journeys ltdApplication request
      case BusinessType.SoleTrader ⇒
        Journeys soleTraderApplication request
      case BusinessType.Partnership ⇒
        Journeys partnershipApplication request
    }
    Ok(getSummaryHtml(application, request.bpr, request.verifiedEmail, summaryPageParams(request.journeyRequest)))
  }

}
