# Luka - BlueSnap Android SDK

[![Latest Version](https://img.shields.io/badge/Latest%20Version-1.0.0-blue.svg)](https://gitlab.com/luka-mobile/bluesnap-android-sdk/releases)

Luka's iOS SDK for Bluesnap enables you to integrate with Bluesnap SDK in swiftly manner. It's important to note that this SDK currently only supports Card payments through the BlueSnap SDK and no other payment method is supported.

# Installation

## Requirements
* Luka API credentials

## CocoaPods
To integrate Luka - BlueSnap SDK into your Android project just specify the dependency in your module level Gradle file:

```groovy

    implementation("com.luka.sdk.bluesnap:lukasdk:1.0.0")

```

Then, add this to your settings.gradle file: 

```groovy
    repositories {
        ...
        maven {
            url 'https://jitpack.io'
            credentials {
                username 'glpat-uKhPPXjgMnsjrodpmkPH'
            }
        }
    }


```
Lastly,  sync project with gradle files:

# Usage

## Initialization
Initialize LukaBluesnapSDK by setting up the configuration on your Application class:

```kotlin 
  LukaBluesnap.setUpConfig(
    config = Config(env = Environment.Sandbox, credentials = Credentials(username = "username", password = "password")),
    callbacks = object : Config.Callbacks {
        override fun onSuccess() {
            viewModel.getCards()
        }

        override fun onError() {

        }
    })
```

## Add a new card to a user

To add a Debit/Credit card to a user in your app you may use the addNewCard() method:

```kotlin 
  LukaBluesnap.addNewCard(context = context, email = "email@sample.com")
    .onSuccess {
        
    }.onError {
        
    }
    .onLoading {
        
    }
    .start()
```

The onSuccess callback function provides an AddCardResult class that contains information regarding the lukaId assign to the given email address, as well as Card information provided as a Card object.

```kotlin 
  data class AddCardResult(
    val lukaCustomerId: String,
    val card: LukaCard
  )
```

```kotlin 
  class LukaCard(
    val cardId: Int,
    val cardLast4: String,
    val cardProcessor: Processor,
    val cardSubType: SubType,
    val country: String,
    val expiryDate: String
  )
```

In case, the error is not on the previous available options within the AddCardError class, then a string describing the error will be provided as parameter of the error. To access a description of the error the property `errorDescription` may be used.

This method redirects you to a built-in screen within the SDK that provides with all the inputs needed to link a new card with a user.

The onSuccess callback function provides an AddCardResult class that contains information regarding the lukaId assign to the given email address, as well as Card information provided as a Card object.

This logic is consistent across all available methods within this SDK.

## List all available cards for a user

To list all Debit/Credit cards from a give user in your app you may use the getCards() method, as such:

```kotlin 
  LukaBluesnap.getCards(clientId = "e1555a98-881a-48a5-b958-fc1c6f37f258")
    .onSuccess { list ->
        // card list 
    }.start()
```

This method provides with a list of instances of the LukaCard class. This same class may be used later when processing a payment with a card. It's important to note that if any error occurs, then an empty array will be returned.

## Delete a user card

To be able to delete a user card you only need the user clientId and the cardId to delete:

```kotlin 
  LukaBluesnap.deleteCard(clientId = "e1555a98-881a-48a5-b958-fc1c6f37f258", cardId = 1234)
    .onSuccess { deleted ->
        // handle was deleted or not
    }.start()
```

## Process a payment

To be able to process a payment with a card that is already link to a user you may use the processPayment() method:

```kotlin 
  LukaBluesnap
      .processPayment(
            "e1555a98-881a-48a5-b958-fc1c6f37f258",
            card = it,
            amount = 10.0,
            email = "email@sample.com"
      )
    .onSuccess {}
    .onError {}
    .onLoading {}
    .start()
```

This method needs the previously provided LukaCard class, the amount in Double, the clientId and the email associated with the user. The onSuccess callback returns an instance of TransactionResult class, which contains relevant information regarding the payment.

```kotlin 
  data class TransactionResult(
    val id: Int,
    val merchantTransactionId: Int,
    val traceId: String,
    val amount: Double,
    val lukaClientId: String,
    val paymentNetwork: String
)
```