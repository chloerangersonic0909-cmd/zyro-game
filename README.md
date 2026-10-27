# Zyro Wallet APK 💙

Android wallet interface prototype with a blue-and-white design, plus an AI/security API integration starter.

## Included in this repository
- Wallet home screen and clearly labelled demo balance
- Demo transfer history only; no funds move
- AI cybersecurity Q&A screen wired to a backend endpoint
- URL reputation lookup screen wired to a backend endpoint
- Backend starter under `backend/`
- GitHub Actions builds a debug APK

## Important: not a live financial product
Real money transfers, deposits, withdrawals, account balances, and payment QR settlement are **not enabled**. No licensed payment provider has been selected or connected. A real deployment needs a provider-approved merchant account, provider-specific server-side integration, verified webhooks, authentication, a ledger, reconciliation, fraud monitoring, and security/privacy review. Never trust payment status from the Android client and never place secret API keys in the APK or GitHub.

## AI and URL reputation
The Android app currently uses a placeholder backend URL. Deploy `backend/server.mjs` on a server with HTTPS, set private server environment variables, then set `API_BASE_URL` in `MainActivity.java` to that HTTPS origin and rebuild. Until that is done, the AI and URL scan buttons will explicitly report that they are not connected.

The VirusTotal integration submits URLs to VirusTotal; submitted indicators may be shared with its security dataset. Do not submit private or sensitive links. Check the provider's terms and access level before commercial use. URL reputation does not scan the Android device and does not guarantee safety. Keep Google Play Protect enabled.

## Build and download
1. Open the Actions tab.
2. Select **Build Zyro Wallet APK**.
3. Open a successful run.
4. Under Artifacts, download **Zyro-Wallet-APK** and extract the ZIP.
5. Install `app-debug.apk` for testing.

This is a debug build, not a production financial app or a Play Store release.
