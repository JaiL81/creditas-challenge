import challenge.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// TODO extract magic numbers to constants
const val CREDIT_CARD_NUMBER = "43567890-987654367"
const val CUSTOMER_EMAIL = "order.custome@email.com"

internal class OrderProcessorTest {
    private val order = Order(Customer(CUSTOMER_EMAIL), Address())
    private val creditCard = CreditCard(CREDIT_CARD_NUMBER)

    @Nested
    inner class PaymentTests {

        @BeforeEach
        fun setUp() {
            val shirt = PhysicalItem("Flowered t-shirt", 35.00)
            val netflix = Subscription("Familiar plan", 29.90)
            val book = Book("The Hitchhiker's Guide to the Galaxy", 120.00)
            val music = DigitalMedia("Stairway to Heaven", 5.00)

            order.addProduct(shirt, 2)
            order.addProduct(netflix, 1)
            order.addProduct(book, 1)
            order.addProduct(music, 1)

            assertEquals(order.totalAmount, 224.90)
            assertNull(order.payment)
            assertNull(order.closedAt)
        }

        @Test
        fun `payment has been successful`() {
            order.pay(creditCard)
            assertNotNull(order.payment)
        }

        @Test
        fun `duplicate payments on the same order shouldn't be possible`() {
            order.pay(creditCard)
            assertNotNull(order.payment)
            val exception = assertThrows(Exception::class.java) {
                order.pay(creditCard)
            }

            assertEquals(exception.message, "The order has already been paid!")
        }

        @Test
        fun `empty orders shouldn't be paid`() {
            val emptyOrder = Order(Customer(CUSTOMER_EMAIL), Address())
            val exception = assertThrows(Exception::class.java) {
                emptyOrder.pay(creditCard)
            }

            assertEquals(exception.message, "Empty order can not be paid!")
        }
    }

    @Nested
    inner class SubmissionRulesTests {
        @Test
        fun `subscription product should be active after finishing order`() {
            val netflix = Subscription("Familiar plan", 29.90)

            order.addProduct(netflix, 1)
            order.pay(creditCard)
            order.finish()

            assertNotNull(order.closedAt)
            assertTrue(netflix.active)
        }

        @Test
        fun `digital media product should apply a discount voucher of 10$ after finishing order`() {
            val music = DigitalMedia("Stairway to Heaven", 5.00)

            order.addProduct(music, 1)
            order.pay(creditCard)
            order.finish()

            assertNotNull(order.closedAt)
            assertNotNull(order.customer.discounts)
            assertEquals(order.customer.discounts.size, 1)

            val discountVoucher = order.customer.discounts[0]

            assertEquals(discountVoucher.value, 10)
            assertEquals(discountVoucher.currency, "$")
            assertEquals(discountVoucher.payment, order.payment)
        }

        @Test
        fun `empty orders shouldn't be finished`() {

        }

        @Test
        fun `an already closed order shouldn't be finished again`() {

        }
    }
}