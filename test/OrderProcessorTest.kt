import challenge.*
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

const val CREDIT_CARD_NUMBER = "43567890-987654367"
const val CUSTOMER_EMAIL = "order.customer@email.com"

internal class OrderProcessorTest {
    private val order = Order(Customer(CUSTOMER_EMAIL), Address())
    private val creditCard = CreditCard(CREDIT_CARD_NUMBER)

    @Nested
    inner class PaymentTests {

        @Test
        fun `payment has been successful`() {
            createMultiProductOrder()
            order.pay(creditCard)

            assertNotNull(order.payment)
        }

        @Test
        fun `duplicate payments on the same order shouldn't be possible`() {
            createMultiProductOrder()
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

        private fun createMultiProductOrder() {
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
    }

    @Nested
    inner class SubmissionRulesTests {
        @BeforeEach
        fun setUp() {
            mockkStatic("challenge.ProductKt")
        }

        @Test
        fun `physical item product should print a shipment label after finishing order`() {
            val shirt = PhysicalItem("Valencia CF century T-shirt", 35.00)

            completeOrder(shirt)

            verify { printShipmentLabel(order.address) }
        }

        @Test
        fun `book product should print a shipment label with tax exemption message after finishing order`() {
            val book = Book("The Expanse: the Leviathan wakes", 20.00)

            completeOrder(book)

            verify { printShipmentLabel(order.address, "This is a tax exempt item") }
        }

        @Test
        fun `subscription product should be active after finishing order`() {
            val subscriptionName = "Amazon Prime Video"
            val amazonPrimeVideo = Subscription(subscriptionName, 29.90)

            completeOrder(amazonPrimeVideo)

            assertTrue(amazonPrimeVideo.active)
            verify { sendEmail(CUSTOMER_EMAIL, "Your subscription to $subscriptionName has been activated") }
        }

        @Test
        fun `digital media product should apply a discount voucher of 10$ after finishing order`() {
            val digitalMediaName = "With or without you"
            val digitalMediaPrice = 5.00
            val currency = "$"
            val music = DigitalMedia(digitalMediaName, digitalMediaPrice)

            completeOrder(music)

            assertNotNull(order.customer.discounts)
            assertEquals(order.customer.discounts.size, 1)

            val discountVoucher = order.customer.discounts[0]

            assertEquals(discountVoucher.value, 10)
            assertEquals(discountVoucher.currency, currency)
            assertEquals(discountVoucher.payment, order.payment)

            verify {
                sendEmail(
                    CUSTOMER_EMAIL,
                    "You have purchased a subscription to $digitalMediaName with a price of $digitalMediaPrice$currency"
                )
            }
        }

        @Test
        fun `not paid orders shouldn't be finished`() {
            val exception = assertThrows(Exception::class.java) {
                order.finish()
            }

            assertEquals(exception.message, "The order can't be finished if it has not been paid")
        }

        @Test
        fun `an already closed order shouldn't be finished again`() {
            val movie = DigitalMedia("Star Wars", 5.00)

            completeOrder(movie)

            val exception = assertThrows(Exception::class.java) {
                order.finish()
            }

            assertEquals(exception.message, "The order has already been closed")
        }

        private fun completeOrder(product: Product) {
            order.addProduct(product, 1)
            order.pay(creditCard)
            order.finish()

            assertNotNull(order.closedAt)
        }
    }
}