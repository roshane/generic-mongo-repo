package com.roshane.mongo.repo

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

import com.mongodb.client.model.UpdateOptions
import com.roshane.mongo.BaseCodecProvider
import com.roshane.mongo.entity.Entities._
import com.roshane.mongo.exception.MongoStoreException
import org.bson.{BsonDocumentReader, BsonDocumentWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Indexes
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

abstract class GenericKVRepo[K <: Key, V <: Value](implicit ec: ExecutionContext) extends BaseCodecProvider {

  protected case class MongoEntry(key: K, value: V)

  protected def db: MongoDatabase

  protected def collectionName: String

  protected implicit val keyCodec: Codec[K]

  protected implicit val valueCode: Codec[V]

  protected def keyFieldName: String

  protected def valueFiledName: String

  protected def collection: MongoCollection[BsonDocument] = ensureIndexes(db.getCollection(collectionName))

  private val encoderContext: EncoderContext = EncoderContext.builder().build()

  private val decoderContext: DecoderContext = DecoderContext.builder().build()

  private val delay: Duration = 5.seconds

  private val updateOptions = new UpdateOptions().upsert(true)

  def upsert(key: K, value: V): Future[Unit] = {
    //    logger.debug("Storing key {} and value {} into MongoDB {}", key, value, repoName: Any)
    val selector = equal(keyFieldName, keyToBson(key))
    val modifier = set(valueFiledName, BsonDocument((valueFiledName, valueToBson(value))))
    collection.updateOne(selector, modifier, updateOptions).toFuture().flatMap(result => {
      if (result.wasAcknowledged()) {
        Future.unit
      } else {
        val errMsg = s"Error storing $key and $value into MongoDB"
        Future.failed(MongoStoreException(errMsg))
      }
    })
  }

  def upsertAll(map: Map[K, V]): Future[Unit] = {
//    logger.debug("Storing {} into MongoDB {}", map, repoName: Any)
    Future.sequence(map.map { case (k, v) => upsert(k, v) }).map(_ => ())
  }

  def remove(key: K): Future[Unit] = {
    //    logger.debug("Deleting {} from MongoDB {}", key, repoName: Any)
    val selector = equal(keyFieldName, keyToBson(key))
    collection.deleteOne(selector).toFuture().map(_ => ())
  }

  def removeAll(keys: Iterable[K]): Future[Unit] = {
//    logger.debug("Deleting multiple keys from MongoDB {}", repoName: Any)
    Future.sequence(keys.map(remove)).map(_ => ())
  }

  def find(key: K): Future[Option[V]] = {
    //    logger.debug("Loading single key {} from MongoDB {}", key, repoName: Any)
    val query = equal(keyFieldName, keyToBson(key))
    collection.find(query)
      .first().map(bsonToMongoEntry).map(_.value).toFuture().map(_.headOption)
  }

  def findAll(keys: Iterable[K]): Future[Map[K, V]] = {
    //    logger.debug("Loading multiple keys from MongoDB {}", repoName: Any)
    val query = in(keyFieldName, keys.map(keyToBson))
    collection.find(query).map(bsonToMongoEntry)
      .map(entry => entry.key -> entry.value)
      .toFuture().map(_.toMap)
  }

  def findAllKeys(): Future[Iterable[K]] = {
//    logger.debug("Loading all keys from MongoDB {}", repoName: Any)
    collection.find().map(bsonToMongoEntry).map(_.key).toFuture()
  }


  private def keyToBson(entity: K)(implicit codec: Codec[K]): BsonDocument = {
    val doc = BsonDocument()
    val writer = new BsonDocumentWriter(doc)
    codec.encode(writer, entity, encoderContext)
    doc
  }

  private def valueToBson(entity: V)(implicit codec: Codec[V]): BsonDocument = {
    val doc = BsonDocument()
    val writer = new BsonDocumentWriter(doc)
    codec.encode(writer, entity, encoderContext)
    doc
  }

  private def bsonToEntity[TEntity](bson: BsonDocument)(implicit codec: Codec[TEntity]): TEntity = {
    val reader = new BsonDocumentReader(bson)
    codec.decode(reader, decoderContext)
  }

  private def bsonToMongoEntry(bson: BsonDocument): MongoEntry = {
    val decodedKey = bsonToEntity[K](bson.get(keyFieldName).asDocument())
    val decodedValue = bsonToEntity[V](bson.get(valueFiledName).asDocument())

    MongoEntry(decodedKey, decodedValue)
  }

  private def mongoEntryToBson(mongoEntry: MongoEntry): BsonDocument = {
    BsonDocument(Seq(
      (keyFieldName, keyToBson(mongoEntry.key)),
      (valueFiledName, valueToBson(mongoEntry.value))
    ))
  }

  private def ensureIndexes(col: MongoCollection[BsonDocument]): MongoCollection[BsonDocument] =
    Await.result(col.createIndex(Indexes.ascending(keyFieldName)).toFuture().map(_ => col), delay)

}
