# "Exchange" Coding Challenge

## Intro

Design an Android app to work with the limitations of a free account on CurrencyLayer and allow for currency conversions in all available currencies.  

## Libraries used

- Moshi (Json handling)
- Retrofit (Communication with APIs - uses OkHttp under the hood)
- ReactiveX (Observing LiveData and updating the UI accordingly)
- JUnit

## Technical Details

### Build Variants

#### debug 

This will connect to the CurrencyLayer servers and pull live data (not more than once every 30 mins).

#### localdata

If for some reason debug does not work (such as an issue with the API key) then this build flavor will use locally saved JSON data.

### How to Run Tests

Please find the "AllTests.kt" and run this.

## TODOs

- Perform long calculations on a background thread and don't block the UI.
- Save scroll position after rotation.