package com.roshane.mongo.exception

case class MongoStoreException(msg: String,
                               throwable: Throwable = None.orNull) extends RuntimeException(msg, throwable)
