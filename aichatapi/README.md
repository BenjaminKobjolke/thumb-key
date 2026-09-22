# aichatapi

**Source of truth:** `Intern/ai-chat-api-client-android` on Gitea (`xida.me:3030`), checked out at
`D:\GIT\Intern\android\ai-chat-api-client-android`. This folder is a copy that stays until it is
replaced by a git submodule of that repo, so fixes go to the new repo first.

Native Kotlin/Android client for the XIDA AI chat API (`https://ai.xida.de/api/v11`). It is a plain
`com.android.library` module with no dependencies beyond the Android platform (`HttpURLConnection`
and `org.json`), so another project can copy the folder and `include ':aichatapi'`. The module's
manifest brings the `INTERNET` permission with it.

A consumer needs three calls. All of them block, so run them off the main thread, and all of them
throw `XidaAiException` when the API answers `success: false`, the network fails or the response is
malformed:

```kotlin
val client = XidaAiClient(software = "de.xida.example", appVersion = "1.0.0")

// 1. Sign in: open registerCode.url in the browser, then poll check() until it stops throwing.
//    redirectUri is optional and must be registered for the app on the server.
val registerCode = client.auth.createRegisterCode(redirectUri = "myapp://login")
val credentials = client.auth.check(registerCode.code)

// 2. Upload the recording. prompt is optional: one AI instruction the server runs over the transcript
val id = client.services.transcribe(credentials, file, language = "de", prompt = "correct spelling and grammar errors")

// 3. Poll while !status.isFinished. With a prompt the status passes PENDING_AI / ACTIVE_AI before COMPLETE
val status = client.services.info(credentials, id)
// COMPLETE: status.text is the raw transcript, status.promptResult the prompt answer
//           (null and status.promptFailed = true when the AI step failed; the transcript is still there)
// ERROR:    status.message says why
```

Past transcriptions, newest first, with paging (`limit` is capped at 100 by the server):

```kotlin
val page = client.services.list(credentials, start = 0, limit = 20)
page.total; page.items.forEach { it.id; it.status; it.createdAt; it.text; it.prompt; it.promptResult }
```

Saved prompts live in the user setting `transcriptionPrompts` (contract key). The app creates ids and
timestamps and picks which text to send as `prompt`; the server never applies one on its own:

```kotlin
val prompts = client.settings.getTranscriptionPrompts(credentials)
client.settings.saveTranscriptionPrompts(credentials, prompts + TranscriptionPrompt(id, text, createdAt, updatedAt))
```

Pass `logger = { line -> Log.d("aichatapi", line) }` to `XidaAiClient` to see every request and
response; tokens, passwords and register codes are redacted before they reach the logger.

`software` must exist in the API's `apps` table, otherwise sign-in fails with `error_login`. Free
accounts get a truncated transcript (first and last ten words); that is server policy.
