package com.roshane.mongo

import com.roshane.mongo.entity.Entities.CustomerKey
import com.roshane.mongo.util.FakeUtil
import org.bson.BsonDocumentWriter
import org.bson.codecs.EncoderContext
import org.mongodb.scala.bson.BsonDocument

object Application extends App with BaseCodecProvider {



}


//val encoderContext: EncoderContext = EncoderContext.builder().build()
//
//val doc = BsonDocument()
//
//val writer = new BsonDocumentWriter(doc)
//
//val customerRecord = FakeUtil.fakeCustomers(1).head
//val codec = codecRegistry.get(classOf[CustomerKey])
//
//codec.encode(writer,customerRecord._1,encoderContext)
//
//println(doc)