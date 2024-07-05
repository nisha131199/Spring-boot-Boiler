package com.taskly.model


data class ApiResponse(
    var error: Boolean = false,
    var statusCode: Int = -1,
    var message: String = "",
    var serverError: String = "",
    var data: Any? = null
)

data class ApiPaginationResponse(
        var error: Boolean = false,
        var statusCode: Int = -1,
        var message: String = "",
        var serverError: String = "",
        var data: Any? = null,
        var totalSize: Int? = null,
        var nextPage: Int? = null
)

data class ApiPaginationForTransactionHistoryResponse(
        var error: Boolean = false,
        var statusCode: Int = -1,
        var message: String = "",
        var serverError: String = "",
        var data: Any? = null,
        var amountReceived: String? = null,
        var amountSent: String? = null,
        var amountHold: String? = null,
        var amountRefund: String? = null,
        var amountRevenue: String? = null,
        var totalSize: Int? = null,
        var nextPage: Int? = null
)

data class ApiStripeCreateBankUrlOrPaymentIntentResponse(
    var error: Boolean = false,
    var statusCode: Int = -1,
    var message: String = "",
    var serverError: String = "",
    var data: Any? = null,
    var url: String? = null,
    var paymentIntent: String? = null,
    var ephemeralKey: String? = null,
    var publishableKey: String? = null,
    var serviceFeeAmount: String? = null,
    var totalAmount: String? = null,
)



data class ApiLoginResponse(
    var error: Boolean = false,
    var statusCode: Int = -1,
    var message: String = "",
    var serverError: String = "",
    var data: Any? = null,
    var isNew: Boolean? = null
)
