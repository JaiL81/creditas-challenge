import challenge.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested

// TODO extract magic numbers to constants
const val CREDIT_CARD_NUMBER = "43567890-987654367"
internal class OrderProcessorTest {

    @Nested
    inner class PaymentTests {
        private val order = Order(Customer("order.custome@email.comr"), Address())

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
            order.pay(CreditCard(CREDIT_CARD_NUMBER))
            assertNotNull(order.payment)
        }
        @Test
        fun `duplicate payments on the same order shouldn't be possible`() {
            order.pay(CreditCard(CREDIT_CARD_NUMBER))
            assertNotNull(order.payment)
            val exception = assertThrows(Exception::class.java) {
                order.pay(CreditCard(CREDIT_CARD_NUMBER))
            }

            assertEquals(exception.message, "The order has already been paid!")
        }
        @Test
        fun `empty orders shouldn't be paid`() {
            val emptyOrder = Order(Customer("order.custome@email.comr"), Address())
            val exception = assertThrows(Exception::class.java) {
                emptyOrder.pay(CreditCard(CREDIT_CARD_NUMBER))
            }

            assertEquals(exception.message, "Empty order can not be paid!")
        }
    }

    @Nested
    inner class SubmissionRulesTests {
        @Test
        fun `finish order should have been successful`() {

        }
    }
}