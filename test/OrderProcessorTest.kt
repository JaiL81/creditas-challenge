import challenge.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

// TODO extract magic numbers to constants
internal class OrderProcessorTest {
    private val order = Order(Customer(), Address())

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
        order.pay(CreditCard("43567890-987654367"))
        assertNotNull(order.closedAt)
        assertNotNull(order.payment)
    }
}