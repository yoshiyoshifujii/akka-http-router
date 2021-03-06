package akkahttp_router

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ FlatSpec, Matchers }

class RoutersSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  val categoryId = LongNumber

  val productName = Remaining

  val router = Router(
    route(POST,   "category",                                         complete("ok")),
    route(GET,    "category" / categoryId,                            (cid: Long) => complete(cid.toString)),
    route(DELETE, "category" / categoryId / "products" / productName, (t: (Long, String)) => complete(t.toString))
  )

  val target: Route = router.route
  val targetWithPrefix: Route = pathPrefix("test")(router.route)

  "The route" should "return ok for POST request to the /category" in {
    Post("/category") ~> target ~> check {
      responseAs[String] should be("ok")
    }
  }

  it should "return url parameter for GET request to the /category/categoryId" in {
    val expected = 1L
    Get(s"/category/$expected") ~> target ~> check {
      responseAs[String] should be(expected.toString)
    }
  }

  it should "return two url parameters for DELETE request to the /category/categoryId/products/productName" in {
    val expected = (1L, "Curry")
    Delete(s"/category/${expected._1}/products/${expected._2}") ~> target ~> check {
      responseAs[String] should be(expected.toString)
    }
  }

  "The route with pathPrefix" should "return ok for POST request to the /test/category" in {
    Post("/test/category") ~> targetWithPrefix ~> check {
      responseAs[String] should be("ok")
    }
  }

  it should "return url parameter for GET request to the /test/category/categoryId" in {
    val expected = 1L
    Get(s"/test/category/$expected") ~> targetWithPrefix ~> check {
      responseAs[String] should be(expected.toString)
    }
  }

  it should "return two url parameters for DELETE request to the /test/category/categoryId/products/productName" in {
    val expected = (1L, "Curry")
    Delete(s"/test/category/${expected._1}/products/${expected._2}") ~> targetWithPrefix ~> check {
      responseAs[String] should be(expected.toString)
    }
  }

}
