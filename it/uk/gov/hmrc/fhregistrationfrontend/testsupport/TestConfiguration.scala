package uk.gov.hmrc.fhregistrationfrontend.testsupport

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{configureFor, reset, resetAllScenarios}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.concurrent.{IntegrationPatience, PatienceConfiguration}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.play.it.Port

import scala.collection.JavaConversions._

trait TestConfiguration
  extends GuiceOneServerPerSuite
    with IntegrationPatience
    with PatienceConfiguration
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  me: Suite with TestSuite ⇒

  val wiremockHost: String = "localhost"
  val wiremockPort: Int = Port.randomAvailable

  override lazy val port: Int = Port.randomAvailable

  val baseUrl = s"http://localhost:$port/fhdds"

  abstract override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(
      timeout = Span(4, Seconds),
      interval = Span(50, Millis))

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .configure(replaceWithWiremock(Seq(
      "auth",
      "auth.company-auth",
      "fhdds",
      "business-customer-frontend",
      "cachable.short-lived-cache",
      "cachable.session-cache"
    )))
    .build()


  private def replaceWithWiremock(services: Seq[String]) =
    services.foldLeft(Map.empty[String, Any]) { (configMap, service) =>
      configMap + (
        s"microservice.services.$service.host" -> wiremockHost,
        s"microservice.services.$service.port" -> wiremockPort,
        s"microservice.services.cachable.short-lived-cache.domain" -> "save4later",
        s"microservice.services.cachable.session-cache.domain" -> "keystore",
        s"play.filters.csrf.header.bypassHeaders.X-Requested-With" -> "*",
        s"play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
        s"json.encryption.key" -> "fqpLDZ4sumDsekHkeEBlCA==",
        s"json.encryption.previousKeys" -> List.empty
      )
    } +
      (s"auditing.consumer.baseUri.host" -> wiremockHost, s"auditing.consumer.baseUri.port" -> wiremockPort)

  val wireMockServer = new WireMockServer(wireMockConfig().port(wiremockPort))

  override def beforeAll() = {
    wireMockServer.stop()
    wireMockServer.start()
    configureFor(wiremockHost, wiremockPort)
  }

  override def beforeEach() = {
    resetAllScenarios()
    reset()
  }

  override protected def afterAll(): Unit = {
    wireMockServer.stop()
  }

  override def afterEach(): Unit = {
    println("===== REQUESTS =====")
    wireMockServer.getAllServeEvents.toList
      .sortBy(_.getRequest.getLoggedDate)
      .map(_.getRequest).map(r => s"${r.getLoggedDate.toInstant.toEpochMilli}\t${r.getMethod}\t${r.getUrl}")
      .foreach(println)
    println("===== END =====")
  }


}