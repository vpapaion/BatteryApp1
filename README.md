# Battery Display App

Simple Android app that shows:
- Current battery percentage in very large font
- Current time
- Charging status
- Red background under 20%, yellow from 20% to 69%, green from 70% and above
- Live polling every 1 second
- Keeps the screen on while the app is visible

## Build APK with GitHub Actions

1. Upload this whole project to a GitHub repository.
2. Go to Actions.
3. Run "Build Android APK".
4. Download the artifact named `BatteryDisplay-debug-apk`.
5. Extract the ZIP and install `app-debug.apk` on your phone.
