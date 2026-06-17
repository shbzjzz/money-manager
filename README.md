# WealthFlow — Android Money Manager

Kotlin + Jetpack Compose + MVVM/Clean Architecture + Room (offline cache) + Hilt + Retrofit.
Backend: Cloudflare Worker + D1 (SQLite at the edge). UI matches the provided WealthFlow mockups (AED currency, green Material 3 theme).

## Project layout

```
app/        Android app (Kotlin/Compose)
worker/     Cloudflare Worker source + D1 schema (your backend)
.github/workflows/build-apk.yml   CI: builds debug APK on every push to main
```

## 1. Backend (Cloudflare Worker + D1)

```bash
cd worker
npm install -g wrangler        # if not installed
wrangler d1 create wealthflow-db
# copy the returned database_id into worker/wrangler.toml
wrangler d1 execute wealthflow-db --file=./schema.sql
wrangler deploy
```

Note the deployed URL (e.g. `https://wealthflow-api.<you>.workers.dev/`).

## 2. Point the Android app at your Worker

Edit `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://wealthflow-api.<you>.workers.dev/\"")
```

## 3. Build APK via GitHub Actions (already set up)

1. Push this whole folder to a new GitHub repo.
2. GitHub → Actions tab → "Build APK" workflow runs automatically on push to `main`, or trigger manually via "Run workflow".
3. When it finishes, open the run → download the `wealthflow-debug-apk` artifact (zip containing the `.apk`).

The workflow (`.github/workflows/build-apk.yml`) does: checkout → JDK 17 → Android SDK → `./gradlew assembleDebug` → uploads the APK as an artifact. No local Android Studio needed for this.

## 4. Build locally (optional)

```bash
./gradlew assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

Open the folder in Android Studio (File → Open) if you want to edit/run on an emulator directly — Gradle sync will pick up everything automatically.

## What's wired up

- Room database (`AppDatabase`) — Account, Transaction, Category, Budget, EMI entities — is the source of truth, app works fully offline.
- `FinanceRepository` writes to Room first, then fires the same change at the Worker (`POST /transactions`, etc.) so D1 stays in sync. Failed network calls don't block the UI — `isSynced` flag + `/sync/pull`/`/sync/push` endpoints are there for you to wire a retry/background sync worker.
- Screens implemented: Dashboard (hero balance, income/expense, recent transactions, FAB), Add Transaction (segmented type, amount, category/account picker sheets, note), Vaults/Accounts (grid + net worth card), Reports (income/expense + category pie via Compose Canvas), Budget (list + progress, hook up category-spend query to make the progress bars live).
- Default categories + a Cash account auto-seed on first launch.

## Next steps you'll likely want

- Wire `BudgetRow`'s placeholder progress to an actual "spend per category this month" query (the DAO already has `observeCategoryTotals`, just filter by categoryId).
- Add a WorkManager periodic job calling `sync/pull` + `sync/push` for real two-way sync instead of fire-and-forget.
- PIN/biometric app lock screen (androidx.biometric dependency already added).
- Receipt image capture + storage for transactions (`receiptImagePath` field already on the entity).
