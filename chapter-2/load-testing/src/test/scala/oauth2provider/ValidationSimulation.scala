package oauth2provider

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ValidationSimulation extends Simulation {

  val httpConf = http.baseURL("http://localhost:8080")

  val authorizationScenarios = List(
    AuthorizationServer.validation.inject(rampUsers(1000) over(10 seconds)))

  setUp(authorizationScenarios).protocols(httpConf)

}
