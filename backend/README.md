# Zyro Wallet API backend

This is the JavaScript/Node.js backend for the React Native app. It keeps provider API keys on the server, not inside the APK.

## Endpoints
- GET /health — health check
- POST /api/assistant — Thai cybersecurity Q&A using the OpenAI API
- POST /api/security/url — submits a URL to VirusTotal and returns its current analysis state
- GET /api/payments/status — explicitly reports payments as not configured

## Run
Requires Node.js 20 or newer. From this directory, run npm start. Set OPENAI_API_KEY and VIRUSTOTAL_API_KEY as private environment variables in your hosting provider. Do not put them in the mobile app or commit them to GitHub. Use HTTPS in production.

## Important limitations
- This is an API starter, not a production-ready financial system. It has no user authentication, database, rate limiting, or abuse monitoring. Do not expose it publicly until those controls are implemented.
- Real payments are intentionally disabled. A real integration requires an approved merchant account, provider-specific server-side payment creation, verified webhooks, reconciliation, refunds, fraud controls, and audit trails. Never trust payment success reported by the mobile client.
- VirusTotal submissions may be shared with its security dataset. Do not submit private URLs, password-reset links, internal systems, or confidential information. Check provider terms and obtain suitable access before commercial deployment.
- URL reputation does not scan the phone and cannot guarantee a URL is safe. Keep Google Play Protect enabled.
- Never send passwords, PINs, OTPs, API keys, or payment credentials to the AI assistant.
