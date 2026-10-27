# Zyro Wallet API backend

This backend keeps provider API keys off the Android APK.

## Endpoints
- GET /health — health check
- POST /api/assistant — Thai defensive cybersecurity Q&A using the OpenAI API
- POST /api/security/url — submits a URL to VirusTotal and returns the current analysis state
- GET /api/payments/status — explicitly reports payments as not configured

## Run
Requires Node.js 20 or newer:
```sh
node server.mjs
```
Set `OPENAI_API_KEY` and `VIRUSTOTAL_API_KEY` as private environment variables in your hosting provider. Do not put them in Android code or commit them to GitHub. Configure HTTPS in production.

## Important limitations
- This is an API starter, not a production-ready financial system. It has no user authentication, database, rate limiting, abuse monitoring, or payment ledger yet. Do not expose it publicly until those controls are implemented.
- Real payments are intentionally disabled. Choose a licensed payment provider, obtain the required approved merchant account and terms, then implement and test that provider's server-side payment creation, verified webhooks, reconciliation, refunds, fraud controls, and audit trail. Never trust a payment success reported by the Android client.
- VirusTotal submissions may be shared with its security dataset. Do not submit private URLs, password-reset links, internal systems, personal data, or confidential information. The public API terms restrict commercial use; check the provider's current terms and obtain suitable access before commercial deployment.
- URL reputation scanning does not scan the phone and cannot guarantee a URL is safe. Keep Android/Google Play Protect and a reputable security product enabled.
- Do not send passwords, PINs, OTPs, API keys, or payment credentials to the AI assistant.
