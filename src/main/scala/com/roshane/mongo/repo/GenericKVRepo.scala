package com.roshane.mongo.repo

import com.roshane.mongo.entity.Entities.{Key, MongoEntry, Value}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import org.mongodb.scala.{MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

abstract class GenericKVRepo[K <: Key, V <: Value](keyFieldName: String = "key",
                                                   valueFiledName: String = "value")
                                                  (implicit ec: ExecutionContext) {

  protected def db: MongoDatabase

  protected def collectionName: String

  protected def collection: MongoCollection[MongoEntry] = db.getCollection(collectionName)

  def insert(key: K, value: V): Future[InsertOneResult] =
    collection.insertOne(MongoEntry(key, value)).toFuture()

  def delete(key: K): Future[DeleteResult] =
    collection.deleteOne(equal(keyFieldName, key)).toFuture()

  def find(key: K): Future[Option[V]] =
    collection.find(equal(keyFieldName, key)).first().map(_.value).toFuture().map(_.headOption)

  def findAll(): Future[Seq[V]] =
    collection.find().map(_.value).toFuture()

  def update(key: K, value: V): Future[Option[Value]] =
    collection.findOneAndUpdate(equal(keyFieldName, key), set(valueFiledName, value)).map(_.value).toFuture().map(_.headOption)
}
