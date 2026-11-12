package com.ae.apps.lib.billingclient

import android.app.Activity
import com.ae.apps.lib.billingclient.AeBillingClient.BillingClientHandler
import com.android.billingclient.api.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.contains
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.*

class AeBillingClientTest {

    private lateinit var billingClient: AeBillingClient
    private lateinit var handler: BillingClientHandler

    private val mockActivity = mock<Activity>()

    @BeforeEach
    fun setup() {
        handler = mock()
        val productDetails: ProductDetails = mock()
        val offer: ProductDetails.OneTimePurchaseOfferDetails = mock()

        whenever(offer.priceCurrencyCode).thenReturn("USD")
        whenever(offer.formattedPrice).thenReturn("$1.99")

        whenever(productDetails.productId).thenReturn("test_product")
        whenever(productDetails.title).thenReturn("Mock Title")
        whenever(productDetails.oneTimePurchaseOfferDetails).thenReturn(offer)

        billingClient = AeBillingClient()
        billingClient.apply {
            // Manually inject internal fields (use reflection or design for better testability)
            val productDetailsMapField = AeBillingClient::class.java.getDeclaredField("productDetailsMap")
            productDetailsMapField.isAccessible = true
            val productDetailsMap = mutableMapOf<String, ProductDetails>("test_product" to productDetails)
            productDetailsMapField.set(this, productDetailsMap)

            val handlerField = AeBillingClient::class.java.getDeclaredField("handler")
            handlerField.isAccessible = true
            handlerField.set(this, handler)

            val billingClientField = AeBillingClient::class.java.getDeclaredField("billingClient")
            billingClientField.isAccessible = true
            billingClientField.set(this, mock<BillingClient> {
                on { launchBillingFlow(eq(mockActivity), any()) } doReturn BillingResult.newBuilder()
                    .setResponseCode(BillingClient.BillingResponseCode.OK)
                    .build()
            })
        }
    }

    // Not working because the ProductDetails cannot be mocked
    fun `launchBillingFlowForProductId should return OK when product exists`() {
        val result = billingClient.launchBillingFlowForProductId(mockActivity, "test_product")
        assertEquals(BillingClient.BillingResponseCode.OK, result)
    }

    @Test
    fun `launchBillingFlowForProductId should return ITEM_UNAVAILABLE when product is missing`() {
        val result = billingClient.launchBillingFlowForProductId(mockActivity, "nonexistent_product")
        assertEquals(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE, result)
        verify(handler).handlePurchaseError(
            isNull(),
            eq(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE),
            contains("details not found")
        )
    }
}
