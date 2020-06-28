# Bunny - Android PayStack UI Library

### Screenshots
[Entry page](screenshots/selection_page.PNG)
[Entry page](screenshots/bank_page.PNG)
[Entry page](screenshots/card_page.PNG)
[Entry page](screenshots/saved_cards.PNG)
[Entry page](screenshots/scan_page.PNG)

### What is Bunny ? 
Bunny is a beautiful, easy-to-use, and highly customisable Paystack UI library for Android. It helps with quick integeration into your PayStack Android project.

### Why Bunny?
Bunny is a  UI library that was built for faster integeration into your PayStack Android projects. With bunny basic setup, you are good to go. Not just that, it also support some highly functional usage like card scan support, automatic input validation, account details persistence, etc which is a great deal for usage and development experience.

###  Why PayStack?
PayStack is currently one of the easiest and fastest way to receive payments. With their easy to use Android SDK, it gets your development experience easy and fast.

### PayStack Setup
To use PayStack in your project, kindly follow this link [https://github.com/PaystackHQ/paystack-android](https://github.com/PaystackHQ/paystack-android)

## Installation
Add this to your root `build.gradle` of your project
```
Code goes here
```
### Gradle
```
Code goes here
```
### Maven
```
Code goes here
```
## Usage
Once imported in your project, a basic usage will be to add this  in your layout file: 
```
<com.weylar.bunny.PayView  
  android:id="@+id/pay_view"  
  android:layout_width="match_parent"  
  android:layout_height="match_parent"/>
``` 
Then you will have to populate list of banks to be displayed when using bank payment option by fetching from PayStack endpoint: [https://api.paystack.co/bank?gateway=emandate&pay_with_bank=true](https://api.paystack.co/bank?gateway=emandate&pay_with_bank=true)
```
val payView = findViewById(R.id.pay_view)
payView.setBanks(listOf("AccessBank", "Uba"))
```
That's pretty much all you need to use bunny. Bunny handles everything else you need to do manually including validating the user input, saving and retreiving saved account details and card scan.

### Retrieving Card and Bank Input Data
Get the input values from this callback when the user clicks on pay button or select from the list of previous card or banks. You don't have to worry about validation since this is being handled under the hood. The appropriate error message is displayed to the user. This callback only gets triggered if validation is successful, and you can be sure that you are getting only validated data.
```
payView.onPayClickListener(object : PayView.OnPayListener {  
    override fun onBankPayListener(bankPayViewData: BankPayViewData) { 
	    // Retrieve bank account details. Gets triggered for both pay button and previous bank click. 
        Log.i(LOG, bankPayViewData.accountNumber)  
        Log.i(LOG, bankPayViewData.bankName)  
        Log.i(LOG, bankPayViewData.dateOfBirth)  
    }  
  
    override fun onCardPayListener(cardPayDataView: CardPayViewData) {  
	    // Retrive card details. Get triggered for both pay button and previous card click.
      Log.i(LOG, cardPayDataView.cardNumber)  
        Log.i(LOG, cardPayDataView.cardExpiryMonth)  
        Log.i(LOG, cardPayDataView.cardExpiryYear) 
        Log.i(LOG, cardPayDataView.cardCVV)
    }  
})
```

## Customization
### Usage - XML
- `app:amount = (Float)` - Amount to be displayed on the payment page.

-  `app:amountColor= (Color)` - Defines the amount text color.

- `app:amountDescription= (String)` - Amount description text just above the amount.

- `app:bunnyTheme = (Color)` -  This determines the entire view theme. Automatically adjust the view colors to match with the specified in color. Note that if this is set, it overrides any color property set from xml on any view item. 

-  `app:cardBackground = (Drawable|Color)` -  Defines the credit card view background. This can either be a solid color or a custom drawable background.

- `app:cardContentColor = (Color)` - Defines the color of the view contents in the credit card view.

- `app:enableDetailSave = (Boolean)` - Determines whether to show save card or bank details option. Default value is true.

-  `app:payButtonBackground = (Drawable|Color)` - This defines the background property of the pay button. Can either be a custom drawable background or solid color.

- `app:payButtonText = (String)` - Defines text to be shown on pay button.
- `app:payButtonTextColor =(Color)` - Defines the  pay button text color property.



### Usage - Kotlin | Java
#### Setters
- `enableCardScan(Activity, Boolean)` -  This enables or disables card scan option. Default value is false.

- `setBunnyTheme(Color)` - This determines the entire view theme. Automatically adjust the view colors to match with the specified in color. Note that if this is set, it overrides any color property set from xml on any view item. 

- `setBanks(List)` - This is a compulsory field. It is used to attach list of bank names on spinner used during bank payment.

- `setAmount(Float)` - Amount to be displayed on the payment page.
- `setAmountColor(Color)` - Defines the amount text color.
- `setPayButtonBackground(Drawable|Color)` - This defines the background property of the pay button. Can either be a custom drawable background or solid color.
- `setPayButtonTextColor(Color)` - Defines the  pay button text color property.
-  `setPayButtonText(String)` - Defines text to show on pay button.
-  `setCardBackground(Drawable|Color)` -  Defines the credit card view background. This can either be a solid color or a custom drawable background.
-  `setCardContentColor(Color)` -  Defines the color of the view contents in the credit card view.
-  `enableDetailSave(Boolean)` -  Determines whether to show save card or bank details option. Default value is true.
- `setCardNumber(String)` - Sets card number value on card number edit text view.
- `setBankAccountNumber(String)` - Sets  account number value on edit text view.
- `setDob(String)` -  Sets date of birth value on edit text view.
- `setCardExpiryDate(String)` -  Sets expiry date value on edit text view.
- `setCardCVV(String)` - Sets card value cvv on edit text view.
-  `setCardHolderName(String)` - Sets card holder name value on edit text view.




#### Getters
Bunny is very customizable, therefore you can get reference to any view item and its content.

##### Card Payment

###### Views
- `cardNumberEditText` - Card number edit text view.
-  `cardExpiryEditText` - Card expiry edit text view.
-  `cardHolder` - Card holder edit text view.
-  `cardCVV` - Card CVV edit text view.
- 
###### Views Content
-  `getCardExpiryYear()` - Returns card expiry year.
-  `getCardExpiryMonth()` -  Returns card expiry month.
-  `getCardCVV()` - Returns input card CVV.
-  `getCardNumber()` -  Returns card number input value.
- `getCardHolderName()` - Returns input card holder name.

##### Bank Payment

###### Views
-  `bankSpinner` - Bank spinner view.
- `bankAccountEditText` - Bank account edit text view.
- `bankDobEditText` Bank date of birth edit text view.
- 
###### View Content
- `getDob()` Returns input date of birth.
-  `getBankName()` - Returns selected bank name.
- `getBankAccountNumber()` - Returns bank account number input value.



### Progress Indicator Support
Bunny supports indicator for your transaction progress.
- `showLoadingSpinner()` - This can be called to show a loading indicator when transaction is processing.
-  `hideLoadingSpinner()` - This can be called to hide loading indicator when transaction is completed or interrupted.

### Card Scan Support
By default, user can not use the card scan. To enable it, you have to invoke
```
enableCardScan(Activity, Boolean)
```
Passing in the present activity and a true or false representing whether to enable it or not.

This option triggers a new activity, because of this, you need to explicitly retrieve the response data from your`onActivityResult()` callback in your activity. Example, in this case we are setting the response on our editText which is most likely what you might do too.
```
override fun onActivityResult(requestCode: Int,
 resultCode: Int, 
data: Intent?) {  
  
    when (requestCode) {  
        SCAN_REQUEST_CODE -> {  
            if (resultCode == Activity.RESULT_OK) { 
         // Sets responce on our card number edit text. 
         payView.setCardNumber(data!!.getStringExtra(CARD_NUMBER))  
		//Sets response on our card expiry edit text.
		 payView.setCardExpiryDate(data.getStringExtra(CARD_EXPIRY_DATE))  
  
  
            }  
        }  
    }  
    super.onActivityResult(requestCode, resultCode, data)  
}
```
### Card and Bank Details Persistent Support
Bunny uses `Encrypted SharedPreference` to persist its saved details on the device. Although this is only supported on `API 23` and above, device lower than that uses the default `SharedPrefernce`  for its persistence. To get this fireup, you don't need any configuration, bunny does that for you by default, but to disable it you can invoke this method.
```
enableDetailSave(Boolean)
```
Passing in `false`, `true` will otherwise enable it.

## Important Stuff To Note
- When you set `bunnyTheme` ,  note that this will override any other color or background configuration in `XML`. To avoid this, you need to apply other changes through your code. Example: 
```
app:bunnyTheme="@color/black"
app:payButtonColor="@color/red"
``` 
Pay  button color won't be effected except you do it in your code like this:
```
setPayButtonColor(Color.RED)
```
This works.
- Although most of the configuration can be done using `XML` but it's advice to do the configuration programmatically. This is because you have more control over the view at runtime as to using `XML`.

## Using this Library?
You can give me a shout out on [Twitter](https://twitter.com/weylar_) 😉✨




