package com.roshane.mongo.repo

import scala.concurrent.ExecutionContext

import com.roshane.mongo.entity.Entities.{Address, AddressKey}
import org.bson.codecs.Codec
import org.mongodb.scala.{MongoClient, MongoDatabase}

class AddressRepo(client: MongoClient, dbName: String)(implicit ec: ExecutionContext)
  extends GenericKVRepo[AddressKey, Address] {

  override protected def KeyFieldName: String = "addressKey"

  override protected def ValueFiledName: String = "address"

  override protected def db: MongoDatabase = client.getDatabase(dbName)

  override protected def collectionName: String = "addresses"

  override protected implicit val keyCodec: Codec[AddressKey] = baseCodecRegistry.get(classOf[AddressKey])

  override protected implicit val valueCode: Codec[Address] = baseCodecRegistry.get(classOf[Address])
}
