package challenge

import java.util.Date

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
        items.forEach { item: OrderItem -> item.product.applySubmissionRules(this.payment!!) }
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

class Address
class DiscountVoucher(val value: Int, val currency: String, val payment: Payment)
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
}