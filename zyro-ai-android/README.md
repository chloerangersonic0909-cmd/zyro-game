# Zyro AI Android

A simple Android WebView chat app with a Thai-first interface. The user supplies their own OpenRouter or OpenAI API key and model name.

## Build APK
1. Open the repository's **Actions** tab.
2. Run **Build Zyro AI APK** with **Run workflow**, or push a change under `zyro-ai-android/`.
3. Open the successful run and download the `Zyro-AI-APK` artifact.
4. Extract `app-debug.apk` and install it on Android.

## Notes
- This app does not bypass provider limits or safety rules. Usage and rate limits depend on the selected AI provider and model.
- API usage may cost money. The API key is saved in this app's local storage on the device. Do not share the device with untrusted people while the key is saved.
- This is a prototype, not a Play Store release. The debug APK is not a signed production release.
