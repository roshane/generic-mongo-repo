package com.roshane.mongo

import com.roshane.mongo.entity.Entities.{Key, Value}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

trait AppCodecProvider {

  protected val codecRegistry: CodecRegistry = fromRegistries(
    fromProviders(classOf[Key], classOf[Value],classOf[com.roshane.mongo.repo.GenericKVRepo.MongoEntry]),
    DEFAULT_CODEC_REGISTRY
  )
}