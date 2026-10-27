# Zyro Wallet 💙

Zyro Wallet is a blue-and-white React Native (Expo) Android app with a JavaScript/Node.js backend starter.

## Stack
- Mobile app: React Native + Expo (JavaScript)
- API backend: Node.js, native HTTP server in backend/server.mjs
- Android APK: generated with Expo Prebuild and Gradle in GitHub Actions

## Included in the app
- Wallet-style home screen and clearly labelled demo balance
- Demo transaction history only; no funds move
- AI cybersecurity Q&A connected to POST /api/assistant
- URL reputation lookup connected to POST /api/security/url
- Warning when the backend URL is not configured
- GitHub Actions workflow uploads a debug APK artifact

## Build APK
1. Open the repository Actions tab.
2. Select Build Zyro Wallet APK and run it, or push a change to app files.
3. Open the completed successful run.
4. Download the Zyro-Wallet-APK artifact and extract app-debug.apk.

The workflow generates the native Android project from Expo each run; generated Android files are not committed.

## Configure the API URL
Deploy the backend on a trusted HTTPS host first. In GitHub repository Settings → Secrets and variables → Actions → Variables, create a repository variable named EXPO_PUBLIC_API_BASE_URL with the HTTPS origin of your API (for example, https://api.example.com, without a trailing slash). Re-run the APK workflow. This variable is public app configuration, not a secret. Never place API keys in this variable or the APK.

## Backend
See backend/README.md. Start locally from the backend directory with npm start.

Set OPENAI_API_KEY and VIRUSTOTAL_API_KEY only as private environment variables on the backend host. Do not commit real keys to GitHub or embed them in the Android app.

## Important financial and security limitations
This is a prototype, not a live financial product. Deposits, transfers, withdrawals, payment QR settlement, and real account balances are disabled. Real payments require an approved provider/merchant account, server-side provider integration, verified webhooks, user authentication, a secure ledger, reconciliation, fraud controls, and an independent security/privacy review. Do not use this prototype to store or transfer real money.

The backend starter is not production-ready; it does not yet include user authentication, database persistence, rate limiting, or abuse monitoring. Do not expose it publicly before implementing those controls. VirusTotal submissions may be shared with its security dataset; never submit private links. URL reputation checks do not scan the whole phone or guarantee safety. Keep Google Play Protect enabled.
