package com.roshane.mongo.repo

import org.mongodb.scala.bson.annotations.BsonProperty
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import org.mongodb.scala.{MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

abstract class GenericKVRepo[K, V](keyFieldName: String = "key", valueFiledName: String = "value")
                                  (implicit ec: ExecutionContext) {

  case class MongoEntry(@BsonProperty(keyFieldName) key: K,
                        @BsonProperty(valueFiledName) value: V)

  protected def db: MongoDatabase

  protected def collectionName: String

  protected def collection: MongoCollection[MongoEntry] = db.getCollection(collectionName)

  def insert(key: K, value: V): Future[InsertOneResult] =
    collection.insertOne(MongoEntry(key, value)).toFuture()

  def delete(key: K): Future[DeleteResult] =
    collection.deleteOne(equal(keyFieldName, key)).toFuture()

  def find(key: K): Future[Option[V]] =
    collection.find(equal(keyFieldName, key)).map(_.value).toFuture().map(_.headOption)

  def findAll(): Future[Seq[V]] =
    collection.find().map(_.value).toFuture()

  def update(key: K, value: V): Future[Option[V]] =
    collection.findOneAndUpdate(equal(keyFieldName, key), set(valueFiledName, value)).map(_.value).toFuture().map(_.headOption)
}
