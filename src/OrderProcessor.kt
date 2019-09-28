package challenge

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
        val productAlreadyAdded = items.any { it.product == product }
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
    }

    fun finish() {
        if (payment == null) {
            throw Exception("The order can't be finished if it has not been paid")
        }
        if (closedAt != null) {
            throw Exception("The order has already been closed")
        }
        items.forEach { item: OrderItem -> item.product.finish(this.payment!!) }
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
    val amount = order.totalAmount
    val invoice = Invoice(order)
}

data class CreditCard(val number: String) : PaymentMethod

interface PaymentMethod

data class Invoice(val order: Order) {
    val billingAddress: Address = order.address
    val shippingAddress: Address = order.address
}

fun printShipmentLabel(address: Address, additionalText: String = "") {}
fun sendEmail(email: String, text: String) {}

abstract class Product(open val name: String, open val price: Double) {
    abstract fun finish(payment: Payment)
}

class PhysicalItem(override val name: String, override val price: Double) : Product(name, price) {
    override fun finish(payment: Payment) {
        printShipmentLabel(payment.order.address)
    }
}

class Subscription(override val name: String, override val price: Double) : Product(name, price) {
    var active: Boolean = false
    override fun finish(payment: Payment) {
        active = true;
        sendEmail(payment.order.customer.email, "Your subscription to $name has been activated")
    }
}

class Book(override val name: String, override val price: Double) : Product(name, price) {
    override fun finish(payment: Payment) {
        printShipmentLabel(payment.order.address, "This is a tax exempt item")
    }
}

class DigitalMedia(override val name: String, override val price: Double) : Product(name, price) {
    override fun finish(payment: Payment) {
        val currency = "$"
        sendEmail(
            payment.order.customer.email,
            "You have purchased a subscription to $name with a price of $price$currency"
        )
        payment.order.customer.discounts.add(DiscountVoucher(10, currency, payment))
    }
}

class Address
class DiscountVoucher(val quantity: Int, val currency: String, val payment: Payment)
class Customer(val email: String) {
    var discounts = mutableListOf<DiscountVoucher>()
}

fun main(args: Array<String>) {
    val shirt = PhysicalItem("Flowered t-shirt", 35.00)
    val netflix = Subscription("Familiar plan", 29.90)
    val book = Book("The Hitchhiker's Guide to the Galaxy", 120.00)
    val music = DigitalMedia("Stairway to Heaven", 5.00)

    val order = Order(Customer("order.custome@email.comr"), Address())

    order.addProduct(shirt, 2)
    order.addProduct(netflix, 1)
    order.addProduct(book, 1)
    order.addProduct(music, 1)

    order.pay(CreditCard("43567890-987654367"))
    // now, how to deal with shipping rules then?
    println(order.totalAmount)

}