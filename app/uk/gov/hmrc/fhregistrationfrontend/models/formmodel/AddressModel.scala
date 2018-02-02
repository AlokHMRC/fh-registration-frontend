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

package uk.gov.hmrc.fhregistrationfrontend.models.formmodel

import play.api.data.Forms.{mapping, nonEmptyText, optional}
import play.api.data.Mapping
import play.api.libs.json.{Json, OFormat}

case class AddressModel(
  addressLine1: String,
  addressLine2: String,
  addressLine3: Option[String] = None,
  addressLine4: Option[String] = None,
  postcode: String,
  countryCode: Option[String] = Some("GB")
)

object AddressModel {
  implicit val addressFormat: OFormat[AddressModel] = Json.format[AddressModel]

  def addressMapping: Mapping[AddressModel] =
    mapping(
      "addressLine1" -> nonEmptyText,
      "addressLine2" -> nonEmptyText,
      "addressLine3" -> optional(nonEmptyText),
      "addressLine4" -> optional(nonEmptyText),
      "postcode" -> nonEmptyText,
      "countryCode" -> optional(nonEmptyText)
    )(AddressModel.apply)(AddressModel.unapply)

}