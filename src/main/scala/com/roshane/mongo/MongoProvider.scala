package com.roshane.mongo

import com.mongodb.{ConnectionString, MongoClientSettings}
import org.mongodb.scala.MongoClient

trait MongoProvider extends AppCodecProvider {

  private val host: String = "localhost"
  protected val dbName: String = "crm"
  protected val customerCollName: String = "customers"

  protected val addressCollName: String = "addresses"

  protected val mongoClient: MongoClient = MongoClient(
    MongoClientSettings.builder()
      .applicationName("generic-mongo-repo")
      .codecRegistry(codecRegistry)
      .applyConnectionString(new ConnectionString(s"mongodb://$host:27017"))
      .build()
  )

}
