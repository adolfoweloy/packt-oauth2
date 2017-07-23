package oauth2provider

import oauth2.OAuth
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.parse

object AuthorizationServer {
  
  case class Token(access_token:String)

  implicit val formats = DefaultFormats
  val jsValue = parse(OAuth.getToken())
  val accessToken = "Bearer " + jsValue
    .extract[Token].access_token

  var validation = scenario("Validation of the access token")
      .exec(http("Validate access token through api usage")
        .get("/api/profile")
        .header("Authorization", accessToken)
      )
}
