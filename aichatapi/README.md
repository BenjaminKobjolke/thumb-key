# aichatapi

**Source of truth:** `Intern/ai-chat-api-client-android` on Gitea (`xida.me:3030`), checked out at
`D:\GIT\Intern\android\ai-chat-api-client-android`. This folder is a copy that stays until it is
replaced by a git submodule of that repo, so fixes go to the new repo first.

Native Kotlin/Android client for the XIDA AI chat API (`https://ai.xida.de/api/v11`). It is a plain
`com.android.library` module with no dependencies beyond the Android platform (`HttpURLConnection`
and `org.json`), so another project can copy the folder and `include ':aichatapi'`. The module's
manifest brings the `INTERNET` permission with it.

A consumer needs three calls. All of them block, so run them off the main thread, and all of them
throw `XidaAiException` when the API answers `success: false` or the network fails:

```kotlin
val client = XidaAiClient(software = "de.xida.example", appVersion = "1.0.0")

// 1. Sign in: open registerCode.url in the browser, then poll check() until it stops throwing
val registerCode = client.auth.createRegisterCode()
val credentials = client.auth.check(registerCode.code)

// 2. Upload the recording
val id = client.services.transcribe(credentials, file, language = "de")

// 3. Poll until status is TranscriptionStatus.COMPLETE (text is set) or ERROR (message is set)
val status = client.services.info(credentials, id)
```

`software` must exist in the API's `apps` table, otherwise sign-in fails with `error_login`. Free
accounts get a truncated transcript (first and last ten words); that is server policy.
