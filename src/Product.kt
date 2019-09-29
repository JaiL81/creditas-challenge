package challenge

abstract class Product(open val name: String, open val price: Double) {
    abstract fun applySubmissionRules(payment: Payment)
}

class PhysicalItem(override val name: String, override val price: Double) : Product(name, price) {
    override fun applySubmissionRules(payment: Payment) {
        printShipmentLabel(payment.order.address)
    }
}

class Subscription(override val name: String, override val price: Double) : Product(name, price) {
    var active: Boolean = false
    override fun applySubmissionRules(payment: Payment) {
        active = true
        sendEmail(payment.order.customer.email, "Your subscription to $name has been activated")
    }
}

class Book(override val name: String, override val price: Double) : Product(name, price) {
    override fun applySubmissionRules(payment: Payment) {
        printShipmentLabel(payment.order.address, "This is a tax exempt item")
    }
}

class DigitalMedia(override val name: String, override val price: Double) : Product(name, price) {
    override fun applySubmissionRules(payment: Payment) {
        val currency = "$"
        sendEmail(
            payment.order.customer.email,
            "You have purchased a subscription to $name with a price of $price$currency"
        )
        payment.order.customer.discounts.add(DiscountVoucher(10, currency, payment))
    }
}

fun printShipmentLabel(address: Address, additionalText: String = "") {
    // TODO here should be placed the print shipment label logic
}

fun sendEmail(email: String, text: String) {
    // TODO here should be placed the sending email logic
}