package com.roshane.mongo.entity

object Entities {

  sealed class Key

  sealed class Value

  case class MongoEntry(key: Key, value: Value)

  case class AddressKey(id: String, postalCode: String) extends Key

  case class CustomerKey(id: String, email: String) extends Key

  case class Customer(id: String, name: String, age: Int, email: String) extends Value

  case class Address(id: String, street: String, postalCode: String, unit: String) extends Value

}
