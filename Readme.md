Technologies used : Spring boot 
Database : MySql

## TODO : MS1

## HELP
Find nearby location
https://gautamsuraj.medium.com/haversine-formula-for-spring-data-jpa-db6a53516dc9

#ssh -i "name.pem" ubuntu@your_ec2_url










//STRIPE


********** CREATE ACCOUNT **********

-> create account with enable charges and payout once the onboarding is finished
https://docs.stripe.com/api/accounts/create

********** ACCOUNT link **********

-> create account link once the above account is created
https://docs.stripe.com/api/account_links/create

********** WEHBOOK CALLBACKS **********

--> webhook callback for connected accounts
(Listen to **Events on connected account**)
account.external_account.created
account.external_account.updated
account.external_account.deleted
account.updated

you'll get account.chargesEnabled & account.payoutsEnabled if the user have filled all the data correctly.

********** CREATE PAYMENT INTENT **********

*** CREATE PAYMENT  INTENT ***
-> create payment intent to send amount in platform account
https://docs.stripe.com/api/payment_intents/create
paymentIntentParams
.setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
.addPaymentMethodType("card")  //additional info

*** PAYMENT INTENT WITH TRANSFER (CONNECTED ACCOUNT) ***
https://docs.stripe.com/connect/destination-charges

if you want to directly send the amount to connected account then pass
.setTransferData(
PaymentIntentCreateParams.TransferData.builder()
.setAmount(amount_to_be_transfered)
.setDestination(connected_account_id)
.build()
)
.setApplicationFeeAmount(serviceFeeAmount.toLong())

*** MANUAL CAPTURE ***
paymentIntentParams
.setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)


*** DIRECT PAYMENT WITH PAYMENT METHOD ***
https://docs.stripe.com/api/payment_intents/confirm

********** WEBHOOK CALLBACKS **********

--> webhook callback handle
(Listen to **Events on your account**)
payment_intent.succeeded
https://docs.stripe.com/payments/handling-payment-events
payment_intent.amount_capturable_updated
https://docs.stripe.com/api/payment_intents/capture
********** DISTRIBUTION OF MONEY AND PAYOUTS **********

*** TRANSFER ***
--> send/distribute/transfer amount to connected accounts
https://docs.stripe.com/api/transfers/create
*** PAYOUT TO PLATFORM BANK ACCOUNT ***
https://docs.stripe.com/api/payouts/create
(payout the rest fees in platform bank account)

********** WEBHOOK CALLBACK **********

-> webhook events for that
(Listen to **Events on your account**)
transfer.canceled
transfer.created (when transfer is done)
transfer.reversed
transfer.updated

payout.created
payout.failed
payout.paid (successfully done)
payout.updated


************REFUND AMOUNT***********
https://docs.stripe.com/api/refunds/create

************ATTACH PAYMENT METHOD********
https://docs.stripe.com/api/payment_methods/attach

************DEATTACH PAYMENT METHOD********
https://docs.stripe.com/api/payment_methods/detach

************LIST OUT PAYMENT METHODS********
https://docs.stripe.com/api/payment_methods/customer_list


retrieve net amount from charge
https://docs.stripe.com/api/balance_transactions/retrieve?shell=true&api=true&resource=balance_transactions&action=retrieve
