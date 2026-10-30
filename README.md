# Parikshamanch (परीक्षा मंच)

Parikshamanch is a comprehensive exam preparation, test series, and admin management Android application developed using **Jetpack Compose**, **Material 3**, and **Firebase (Auth & Firestore)**.

---

## 🌟 Key Features

1. **Student Panel (परीक्षार्थी पोर्टल)**:
   - Browse and take mock test series (Rajasthan GK, Patwar, REET, Police, etc.).
   - Instant scoring, timer, and detailed solutions.
   - AI Tutor doubt clearing and explanations.
   - Dark mode / Light mode toggle.
   - One-tap switch to Admin Control Center for authorized administrators.

2. **Admin Control Center (व्यवस्थापक कंट्रोल सेंटर)**:
   - **Primary Admin**: `youngindianews2@gmail.com`
   - **Manual Password Entry**: For maximum security, auto-save and auto-fill are strictly disabled. The administrator enters the password manually each session.
   - **Gmail Password Recovery**:
     - Send official reset links directly to `youngindianews2@gmail.com` with quick "Open Gmail App" shortcut.
     - 6-digit in-app Gmail verification code (OTP) option for fast credential updates.
   - Manage tests, create questions, monitor student test submissions, and configure platform settings.

---

## 🚀 Building the Debug APK Locally

```bash
# Ensure execution permissions for the Gradle wrapper
chmod +x ./gradlew

# Build the Debug APK
./gradlew assembleDebug
```

The generated APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🤖 Automatic GitHub Release & APK Publishing

A pre-configured GitHub Actions workflow is available at `.github/workflows/build-and-release-debug-apk.yml`.

### How to Publish a Release:
1. **Push a Git Tag**:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. **Or Manual Trigger (Actions Tab)**:
   - Go to your repository on GitHub.
   - Click on the **Actions** tab.
   - Select **Build & Publish Debug APK**.
   - Click **Run workflow**, enter your release tag (e.g., `v1.0.0`), and run.

The workflow will compile the project, package `Parikshamanch-debug.apk`, and attach it to a new GitHub Release with download links and install instructions.
