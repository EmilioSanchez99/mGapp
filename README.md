# 🗺️ mGapp – Interactive SVG Hotspot Editor

**mGapp** is an Android application developed in **Kotlin** with **Jetpack Compose**, designed to **display an interactive SVG map** where the user can create, edit, and manage **hotspots** (points of interest) with configurable data fields.  
This project was developed as a **technical assignment** for **DEKRA Spain**.

---

## 🚀 Main Features

### 🖼️ Interactive SVG
- Displays **SVG graphics** with support for **zoom, pan, and inertia**.
- Vector rendering remains crisp at all zoom levels (using `coil-svg` + `SvgDecoder`).
- Accurate hotspot detection through **hit-testing** (`Path.contains()` with transformation matrices).

### 📍 Editable Hotspots
- Tapping an area of the SVG opens a **BottomSheet** with dynamic form fields.
- Supports text, number, date, list, and checkbox fields.
- Each hotspot displays its **completion status**:
  - 🟢 *Complete* – all fields filled
  - 🟡 *Partial* – some fields filled
  - ⚪ *Empty* – no data entered

### 💾 Persistence and Export
- Local data stored using **Room**.
- Data can be **exported/imported as JSON** with versioning and validation.
- Files are managed via **MediaStore** in the *Downloads* folder.
- The `exporToJson()` function is fully operational.

### ↩️ Undo / Redo
- Full history tracking system (minimum 20 steps).
- Undo or redo hotspot creation, edits, and deletions.
- Contextual `Snackbar` with dynamic icons and messages.

### 📋 Hotspot Overview List
- Dedicated screen displaying all stored hotspots.
- Filter options by state (*Complete, Partial, Empty*).
- Direct access to edit or delete each hotspot.

### 🌍 Internationalization
- Fully translated into **Spanish**, **English**, and **German**.
- Automatic language switching based on system settings.
- All text, labels, and `contentDescription` values are loaded from `strings.xml`.

### ♿ Accessibility
- Fully compatible with **TalkBack** and keyboard navigation.
- Every interactive element includes a `contentDescription`.
- Verified focus order, contrast, and logical navigation.

---

## 🏗️ Architecture

mGapp follows the **MVVM** pattern with **unidirectional data flow**, ensuring code clarity and easy testability.

---

## ⚙️ Project Setup and Execution

1. Clone the repository:
   ```bash
   git clone https://github.com/EmilioSanchez99/mGapp
