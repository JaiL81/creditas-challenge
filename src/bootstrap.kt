package challenge

import java.lang.Exception
import java.util.*

class Order(val customer: Customer, val address: Address) {
    private val items = mutableListOf<OrderItem>()
    var closedAt: Date? = null
        private set
    var payment: Payment? = null
        private set
    val totalAmount
        get() = items.sumByDouble { it.total }

    fun addProduct(product: Product, quantity: Int) {
        var productAlreadyAdded = items.any { it.product == product }
        if (productAlreadyAdded)
            throw Exception("The product have already been added. Change the amount if you want more.")

        items.add(OrderItem(product, quantity))
    }

    fun pay(method: PaymentMethod) {
        if (payment != null)
            throw Exception("The order has already been paid!")

        if (items.count() == 0)
            throw Exception("Empty order can not be paid!")

        payment = Payment(this, method)

        close()
    }

    private fun close() {
        closedAt = Date()
    }
}

data class OrderItem(val product: Product, val quantity: Int) {
    val total get() = product.price * quantity
}

data class Payment(val order: Order, val paymentMethod: PaymentMethod) {
    val paidAt = Date()
    val authorizationNumber = paidAt.time
    val am1ount = order.totalAmount
    val invoice = Invoice(order)
}

data class CreditCard(val number: String) : PaymentMethod

interface PaymentMethod

data class Invoice(val order: Order) {
    val billingAddress: Address = order.address
    val shippingAddress: Address = order.address
}

open class Product(open val name: String, open val price: Double)
class PhysicalItem(override val name: String, override val price: Double): Product(name, price)
class Subscription(override val name: String, override val price: Double): Product(name, price)
class Book(override val name: String, override val price: Double): Product(name, price)
class DigitalMedia(override val name: String, override val price: Double): Product(name, price)

enum class ProductType {
    PHYSICAL,
    BOOK,
    DIGITAL,
    MEMBERSHIP
}

class Address

class Customer

fun main(args : Array<String>) {
    val shirt = Product("Flowered t-shirt", 35.00)
    val netflix = Product("Familiar plan", 29.90)
    val book = Product("The Hitchhiker's Guide to the Galaxy", 120.00)
    val music = Product("Stairway to Heaven", 5.00)

    val order = Order(Customer(), Address())

    order.addProduct(shirt, 2)
    order.addProduct(netflix, 1)
    order.addProduct(book, 1)
    order.addProduct(music, 1)

    order.pay(CreditCard("43567890-987654367"))
    // now, how to deal with shipping rules then?
    println(order.totalAmount)

}