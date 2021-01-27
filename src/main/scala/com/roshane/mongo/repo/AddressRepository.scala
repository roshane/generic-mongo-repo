package com.roshane.mongo.repo

import com.roshane.mongo.entity.Entities.{Address, AddressKey}
import org.mongodb.scala.{MongoClient, MongoDatabase}

import scala.concurrent.ExecutionContext

class AddressRepository(client: MongoClient, dbName: String)(implicit ec: ExecutionContext)
  extends GenericKVRepo[AddressKey, Address]("addressKey", "address") {

  override protected def db: MongoDatabase = client.getDatabase(dbName)

  override protected def collectionName: String = "addresses"
}
