package com.roshane.mongo.repo

import com.roshane.mongo.entity.Entities.{Customer, CustomerKey}
import org.mongodb.scala.{MongoClient, MongoDatabase}

import scala.concurrent.ExecutionContext

class CustomerRepo(client: MongoClient, dbName: String)(implicit ec: ExecutionContext)
  extends GenericKVRepo[CustomerKey, Customer]("customerKey", "customer") {

  override protected def db: MongoDatabase = client.getDatabase(dbName)

  override protected def collectionName: String = "customers"
}
