package com.roshane.mongo.repo

import scala.concurrent.ExecutionContext

import com.roshane.mongo.entity.Entities.{Customer, CustomerKey}
import org.bson.codecs.Codec
import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.mongodb.scala.bson.annotations.BsonProperty

class CustomerRepo(client: MongoClient, dbName: String)(implicit ec: ExecutionContext)
  extends GenericKVRepo[CustomerKey, Customer] {

  protected class CustomerEntry(@BsonProperty("customerKey") key: CustomerKey,
                                @BsonProperty("customer") value: Customer)

  override protected def keyFieldName: String = "customerKey"

  override protected def valueFiledName: String = "customer"

  override protected def db: MongoDatabase = client.getDatabase(dbName)

  override protected def collectionName: String = "customers"

  override protected implicit val keyCodec: Codec[CustomerKey] = baseCodecRegistry.get(classOf[CustomerKey])

  override protected implicit val valueCode: Codec[Customer] = baseCodecRegistry.get(classOf[Customer])

}
