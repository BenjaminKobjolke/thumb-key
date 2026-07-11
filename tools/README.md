# tools

Build + upload the debug APK to FTP, using the local [ftp-sync](../../ftp-sync) tool.

## Setup

1. Copy `ftp_sync_example.ini` → `ftp_sync.ini` and fill in the FTP password.
   (`ftp_sync.ini` is gitignored — it holds real credentials.)
2. ftp-sync must be installed at `D:\GIT\BenjaminKobjolke\ftp-sync` (run its `install.bat` once).

## Usage

- **`build_and_upload_debug.bat`** — builds the debug APK (`assembleDebug` via `run-gradle.bat`),
  stages it as `upload/thumbkey.apk`, then uploads via ftp-sync.
- **`upload_apk_to_ftp.bat`** — uploads the already-staged `upload/thumbkey.apk` only.

The APK is uploaded to `FTP_DIRECTORY` (`/downloads/`) with `NO_DELETE` so other remote files
are untouched. Public download: `https://kobjolke.com/apps/thumbkey.apk`.
