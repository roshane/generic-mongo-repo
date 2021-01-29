package com.roshane.mongo

import com.roshane.mongo.entity.Entities._
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

trait BaseCodecProvider {

  protected val baseCodecRegistry: CodecRegistry = fromRegistries(
    fromProviders(classOf[Key], classOf[Value]),
    DEFAULT_CODEC_REGISTRY
  )

}
