package uk.gov.hmrc.fhregistrationfrontend.testsupport.preconditions

class PreconditionBuilder {
  implicit val builder: PreconditionBuilder = this

  def audit = AuditStub()
  def user = UserStub()
  def businessCustomerFrontend = BusinessCustomerFrontendStub()
  def fhddsBackend = FhddsBackendStub()

}