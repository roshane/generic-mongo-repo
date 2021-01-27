package com.roshane.mongo.repo

import com.roshane.mongo.FakeUtil.fakeCustomers
import com.roshane.mongo.MongoProvider
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

class CustomerRepoSpec extends AnyFunSuite with MongoProvider {

  import scala.concurrent.ExecutionContext.Implicits.global

  val customerRepo = new CustomerRepo(mongoClient, dbName)

  test("it should insert successfully") {
    whenReady(customerRepo.findAll()) { beforeRes =>
      beforeRes.isEmpty shouldBe true
      val fakeCustomer = fakeCustomers(1).head
      whenReady(customerRepo.insert(fakeCustomer._1, fakeCustomer._2)) { _ =>
        whenReady(customerRepo.findAll()) { result =>
          result.size shouldBe 1
          result.head shouldBe fakeCustomer._2
        }
      }
    }
  }

}
