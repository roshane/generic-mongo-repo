package com.roshane.mongo.repo

import java.util.concurrent.TimeUnit

import com.roshane.mongo.MongoProvider
import com.roshane.mongo.util.FakeUtil.fakeAddress
import org.mongodb.scala.bson.collection.immutable.Document
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AddressRepoSpec extends AnyFunSuite with MongoProvider with BeforeAndAfter {


  import scala.concurrent.ExecutionContext.Implicits.global

  private val collectionName = "addresses"
  private val db = mongoClient.getDatabase(dbName)
  private val collection = db.getCollection(collectionName)
  private val addressRepo = new AddressRepo(mongoClient, dbName)

  before {
    val deleteFuture = mongoClient.getDatabase(dbName).getCollection(collectionName).deleteMany(Document()).toFuture()
    Await.result(deleteFuture, Duration(5, TimeUnit.SECONDS))
  }


  test("upsertAll should succeed") {
    val generatedAddresses = fakeAddress(10)
    whenReady(addressRepo.upsertAll(generatedAddresses.map(kv => kv._1 -> kv._2).toMap)) { _ =>
      whenReady(collection.countDocuments().toFuture()) { result =>
        result shouldBe generatedAddresses.size
      }
    }
  }
}
