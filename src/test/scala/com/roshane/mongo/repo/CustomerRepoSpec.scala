package com.roshane.mongo.repo

import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.roshane.mongo.MongoProvider
import com.roshane.mongo.util.FakeUtil._
import org.mongodb.scala.bson.collection.immutable.Document
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

class CustomerRepoSpec extends AnyFunSuite with MongoProvider with BeforeAndAfter {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val collectionName = "customers"
  private val db = mongoClient.getDatabase(dbName)
  private val collection = db.getCollection(collectionName)
  private val customerRepo = new CustomerRepo(mongoClient, dbName)

  before {
    val deleteFuture = mongoClient.getDatabase(dbName).getCollection(collectionName).deleteMany(Document()).toFuture()
    Await.result(deleteFuture, Duration(5, TimeUnit.SECONDS))
  }

  test("upsertAll should succeed") {
    val generatedCustomers = fakeCustomers(10)
    whenReady(customerRepo.upsertAll(generatedCustomers.map(kv => kv._1 -> kv._2).toMap)) { _ =>
      whenReady(collection.countDocuments().toFuture()) { result =>
        result shouldBe generatedCustomers.size
      }
    }
  }

  test("it should upsert successfully") {
    whenReady(collection.countDocuments().toFuture()) { beforeRes =>
      beforeRes shouldBe 0
      val fakeCustomer = fakeCustomers().head
      whenReady(customerRepo.upsert(fakeCustomer._1, fakeCustomer._2)) { _ =>
        whenReady(collection.countDocuments().toFuture()) { result =>
          result shouldBe 1
        }
      }
    }
  }

  test("it should find successfully") {
    val generatedCustomers = fakeCustomers(10)
    whenReady(customerRepo.upsertAll(generatedCustomers.map(kv => kv._1 -> kv._2).toMap)) { _ =>
      whenReady(customerRepo.find(generatedCustomers.head._1)) { result =>
        result.isDefined shouldBe true
        result.get shouldBe generatedCustomers.head._2
      }
    }
  }

  test("it should delete successfully") {
    whenReady(collection.countDocuments().toFuture()) { beforeCount =>
      beforeCount shouldBe 0
      val fakeCustomer = fakeCustomers().head
      whenReady(customerRepo.upsert(fakeCustomer._1, fakeCustomer._2)) { _ =>
        whenReady(collection.countDocuments().toFuture()) { afterCount =>
          afterCount shouldBe 1
          whenReady(customerRepo.remove(fakeCustomer._1)) { _ =>
            whenReady(collection.countDocuments().toFuture()) { result =>
              result shouldBe 0
            }
          }
        }
      }

    }
  }

}
