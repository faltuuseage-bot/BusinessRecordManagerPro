# Business Record Manager Pro — Android starter

This is a **starter project**, not the complete production application.

## Included
- Native Android app using Kotlin and Jetpack Compose
- Local Room database
- Add and view generic business records
- Record categories: Company, Client, Service, Job, Invoice, Payment, Expense, Other
- PKR amount field stored as integer paisa
- No hard-coded maximum record count in app logic
- GitHub Actions workflow to compile and upload a debug APK

## Not yet implemented
- Dedicated company/client/service/job/invoice/payment data models and screens
- Invoice numbering, financial allocations and PDF receipts
- Google sign-in, cloud sync, cloud file storage, backup/restore
- English/Urdu resource localization and full RTL verification
- Production security review, migration tests, release signing and QA

Do not use this starter to manage real financial records until backups, financial workflows, access control and recovery are implemented and tested.

## Build from a phone browser using GitHub Actions

1. Create/sign in to a GitHub account.
2. Create a new repository named `BusinessRecordManagerPro`.
3. Upload the contents of this ZIP to the repository root. Make sure `settings.gradle.kts`, `build.gradle.kts`, `app/`, and `.github/` are at the root.
4. Commit the files to the `main` branch.
5. Open the repository's **Actions** tab.
6. Select **Build Android APK** and tap **Run workflow**. If it does not appear, first commit the workflow file to `main`.
7. Open the completed workflow run.
8. Under **Artifacts**, download `Business-Record-Manager-Pro-debug-APK`.
9. Extract the downloaded artifact ZIP, then open `app-debug.apk` on your Android phone and allow installation from that source if Android asks.

GitHub Actions may have account usage limits or require billing details depending on your account and current plan. Do not add billing unless you understand the limits.

## Important
The debug APK is for testing, not a production release. Do not commit signing keys, passwords, service-account JSON files, API secrets, or real customer data to GitHub. Cloud services are not configured in this starter.
