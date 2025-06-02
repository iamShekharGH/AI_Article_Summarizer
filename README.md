# AI Article Summarizer (Android) 🧠📰✨

## AI-Powered Article Summaries for Offline Access

This Android application efficiently tackles information overload by generating **AI-powered article summaries** for web content. It's built with a modern tech stack and clean architecture, enabling **offline access** to all summarized articles and a highly intuitive user experience.

---

## ✨ Core Features

* **Intelligent Summarization:** Leverages **Google Gemini API** for concise summaries, with customizable length via a pre-defined **prompts file** for dynamic query generation.
* **Web Content Extraction:** Utilizes **Jsoup** for robust and efficient extraction of article content from provided URLs.
* **Offline Accessibility:** All summaries are automatically saved locally via **Room Database** for access without an internet connection.
* **User-Friendly Interface:** Built with **Jetpack Compose** for a modern, responsive, and intuitive user experience.
* **Persistent Preferences:** Manages user settings (e.g., Dark Mode, summary length) reliably using **Jetpack DataStore**.
* **Smart Content Management:** Includes integrated search and easy organization of saved articles.

---

## 📸 Screenshots

*(Replace these placeholders with actual, clear screenshots of your app's key screens: Home, Summarize Input, Summary Output, Search, Settings)*

| Home Screen | Summarize Input | AI-Generated Summary |
| :----------------: | :-----------------: | :--------------------: |
| ![Home Screen](https://via.placeholder.com/300x550?text=Home+Screen) | ![Summarize Input](https://via.placeholder.com/300x550?text=Summarize+Input) | ![Summary Output](https://via.placeholder.com/300x550?text=Summary+Output) |

---

## 🛠️ Technical Stack & Architecture

* **Language:** Kotlin
* **UI:** Jetpack Compose
* **AI:** Google Gemini API (for summarization)
* **Web Scraping:** Jsoup (for article content extraction)
* **Dependency Injection:** Hilt
* **Local DB:** Room Persistence Library
* **Data Storage:** Jetpack DataStore (for app preferences)
* **Networking:** Retrofit
* **Asynchronous:** Kotlin Coroutines & Flows
* **Architecture:** Clean Architecture (Modular: `:app`, `:domain`, `:data` layers)

---

## 🚀 Getting Started

Follow these steps to set up the project locally.

### Prerequisites

* Android Studio [Your Version, e.g., Hedgehog | 2023.1.1] or later
* JDK [Your Version, e.g., 17]
* An Android device or emulator (API Level 21+)
* A Google Gemini API Key: Obtain one from [https://ai.google.dev/](https://ai.google.dev/).

### Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/iamShekharGH/AI_Article_Summarizer.git](https://github.com/iamShekharGH/AI_Article_Summarizer.git)
    ```
2.  **Open in Android Studio:**
    Navigate to the cloned directory and open the project in Android Studio.
3.  **Add your Gemini API Key:**
    * In the root directory of your project, create a file named `local.properties` (if it doesn't already exist).
    * Add your API key to this file:
        ```properties
        GEMINI_API_KEY="YOUR_ACTUAL_GEMINI_API_KEY"
        ```
    * *(Ensure `local.properties` is in your `.gitignore` file to prevent committing your key!)*
4.  **Sync and Run:**
    Sync Gradle files and run the app on your device or emulator.

---

## 💡 Usage

1.  Navigate to the "Summarize" tab.
2.  Paste any article URL into the input field.
3.  The app will extract content using Jsoup, prepend it with instructions from an internal prompts file, and send it to the Gemini API for summarization.
4.  View the generated summary instantly, which is then saved locally for offline access.
5.  Explore other features like search, favorites, and settings via the bottom navigation.

---

## 🔮 Future Enhancements

* **Cloud Sync:** Integrate Google Drive for backup and synchronization.
* **Content Diversity:** Support for summarizing PDFs and other document formats.
* **Advanced AI Customization:** Explore integrating different AI models or more granular prompt control.
* **Collaborative Features:** Enable sharing of summaries.

---

## 👋 Connect & Contribute

Feel free to check out my other repositories on GitHub:

* [https://github.com/iamShekharGH](https://github.com/iamShekharGH)

Contributions are welcome! Please open an issue or submit a pull request for any bug fixes, features, or improvements.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---
