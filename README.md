# Smart Notes App — Java + XML (Android Studio)

A college Android project built **entirely using Java for logic and XML for the UI**. Uses Android's built-in `SQLiteOpenHelper` to store notes offline. **No Kotlin, Jetpack Compose, Room or cloud API.**

## Features
- Create, edit, delete and automatically save notes
- Offline SQLite storage (notes remain after closing the app)
- Search by note title or content
- Categories: Study, Work, Personal, Ideas
- Pin notes to the top
- Generate a basic title from note text
- Show a short preview and word count
- Share note text using Android's Share menu
- Three example notes for demonstrations
- Dark UI using XML layouts and drawable resources

The title/preview helpers use simple string processing, **not artificial intelligence**.

## Open in Android Studio
1. Download and extract the project ZIP.
2. In Android Studio, use **File → Open** and select the folder containing `settings.gradle` and `gradlew`.
3. Let Android Studio sync Gradle. Install **Android SDK Platform 34** if requested.
4. Select an emulator or your Android device and click **Run**.
5. This Java edition uses application ID `com.ayush.smartnotes.javaxml`, so it can coexist with the older Kotlin edition.

**Requirements:** Java 17 JDK, Android Gradle Plugin 8.2.2, Gradle 8.4 (via official wrapper), Android SDK 34, min Android 7.0 (API 24).

## Main project files

| File | Purpose |
| --- | --- |
| `app/src/main/java/com/ayush/smartnotes/MainActivity.java` | Home screen, searching, category filters |
| `app/src/main/java/com/ayush/smartnotes/EditorActivity.java` | Add, edit, auto-save, share, delete |
| `app/src/main/java/com/ayush/smartnotes/data/NotesDbHelper.java` | Database creation and CRUD operations |
| `app/src/main/java/com/ayush/smartnotes/data/Note.java` | Note model |
| `app/src/main/java/com/ayush/smartnotes/ui/NoteAdapter.java` | `ListView` notes |
| `app/src/main/java/com/ayush/smartnotes/util/SmartText.java` | Word count, title suggestions, quick preview |
| `app/src/main/res/layout/activity_main.xml` | Home screen layout |
| `app/src/main/res/layout/activity_editor.xml` | Add/edit layout |
| `app/src/main/res/layout/item_note.xml` | Each note row layout |
| `.github/workflows/android.yml` | Automatic GitHub debug APK build |

## GitHub upload — easiest way
1. Create a **new GitHub repository** named **SmartNotesApp-Java-XML**. Do not upload these Java files on top of the old Kotlin repository: old `.kt` and `.kts` files would remain and cause conflicts.
2. **Extract the ZIP**, then upload **all files and folders inside the extracted project**, including hidden `.github/` and `.gitignore`.
3. Confirm `app/`, `gradle/`, `tools/`, `.github/` are in the repository root alongside `settings.gradle`, `gradlew` and `README.md`.
4. Go to **Actions → Android Build → latest run**. On success, the `SmartNotesApp-Java-debug-APK` artifact contains the APK.

**Do not upload only the APK** — the teacher needs source code. The APK is a compiled output, not a source project.

## Run from terminal (optional)
On Windows: `gradlew.bat assembleDebug`  
On macOS/Linux: `./gradlew assembleDebug`

The resulting APK is in `app/build/outputs/apk/debug/`.

## Viva
Read `VIVA_GUIDE.md` for simple explanations and a demonstration script.

## Validation
Read `VALIDATION.md` for the checks actually performed and what needs real Android Studio testing.
