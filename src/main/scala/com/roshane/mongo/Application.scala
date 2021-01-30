package com.roshane.mongo

import java.util.concurrent.Executors

import com.roshane.mongo.entity.Entities
import com.roshane.mongo.repo.CustomerRepo
import com.roshane.mongo.util.FakeUtil

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object Application extends App with MongoProvider {

  //  import scala.concurrent.ExecutionContext.Implicits.global

  private val executorService = Executors.newFixedThreadPool(4)
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executorService)

  val customerRepo = new CustomerRepo(mongoClient, "prod-crm")
  private val fakeCustomers: Seq[(Entities.CustomerKey, Entities.Customer)] = FakeUtil.fakeCustomers(100000)

  val customerEntities = fakeCustomers.map(kv => kv._1 -> kv._2).toMap
//  metered(customerRepo.upsertAll(customerEntities))
  metered(customerRepo.insertAll(customerEntities))
  executorService.shutdown()

  def metered(func: => Future[Unit]): Unit = {
    val start = System.currentTimeMillis()
    Await.result(func, Duration.Inf)
    val end = System.currentTimeMillis()
    println(s"Time taken ${end - start} ms")
  }
}

