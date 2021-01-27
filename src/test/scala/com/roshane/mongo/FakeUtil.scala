package com.roshane.mongo

import com.github.javafaker.Faker
import com.roshane.mongo.entity.Entities._

import scala.util.Random

object FakeUtil {

  private val faker = new Faker()

  def fakeCustomers(count: Int = 1): Seq[(CustomerKey, Customer)] = 1.to(count).map(_ => {
    val id = faker.number().digits(7)
    val email = faker.internet().emailAddress()
    val key = CustomerKey(id, email)
    val cus = Customer(id, faker.name().firstName(), Random.nextInt(90), email)
    (key, cus)
  })

  def fakeAddress(count: Int = 1): Seq[(AddressKey, Address)] = 1.to(count).map(_ => {
    val id = faker.number().digits(5)
    val zip = faker.address().zipCode()
    val key = AddressKey(id, zip)
    val add = Address(id, faker.address().streetName(), zip, faker.address().buildingNumber())
    (key, add)
  })

}
