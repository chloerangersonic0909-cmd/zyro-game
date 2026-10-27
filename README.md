# Zyro Wallet APK 💙

Zyro Wallet is an Android wallet UI prototype with a blue-and-white design.

## Current prototype features
- Wallet home screen with a clearly labelled test balance
- Service shortcuts for scan/pay, transfer, top up, and receive
- Local in-memory demo transfer history
- Android APK built by GitHub Actions

## Important: real money is not enabled
This build is a UI prototype only. It does not connect to a bank, payment gateway, PromptPay, or any licensed payment provider. The balance is not real money and demo transfers do not move funds.

To enable real payments, the project needs a selected licensed provider, an approved merchant/account agreement, a secure server-side integration, verified payment webhooks, authentication, transaction records, and provider sandbox testing. Do not place provider secret keys in the Android app or commit secrets to Git.

## Build and download
1. Open the Actions tab.
2. Select **Build Zyro Wallet APK**.
3. Open the successful run.
4. Under Artifacts, download **Zyro-Wallet-APK** and extract the ZIP.
5. Install `app-debug.apk` on your Android phone for testing.

This is a debug APK for testing, not a production or Play Store release.
