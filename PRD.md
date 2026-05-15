# Product Requirements Document (PRD) - ARTA

## 1. Overview

**Nama Project**: ARTA  
**Deskripsi**: Aplikasi mobile untuk manajemen keuangan pribadi dengan fitur khusus pencatatan dan tracking tabungan emas.  
**Status**: Fase Perencanaan  
**Versi**: 1.0 (MVP)

---

## 2. Tujuan dan Visi

### Tujuan Aplikasi

Menyediakan solusi lengkap untuk pengguna perorangan dalam mengelola keuangan pribadi mereka dengan fokus khusus pada tracking aset emas. Aplikasi ini dirancang untuk memudahkan pencatatan transaksi harian, monitoring nilai investasi emas, dan memberikan overview keuangan yang jelas.

### Visi Jangka Panjang

Menjadi aplikasi manajemen keuangan pilihan utama bagi masyarakat yang concern terhadap investasi emas dan pengelolaan keuangan pribadi.

---

## 3. Target User

**Target Pengguna Utama**:

- Individu/Pengguna perorangan (usia 18+)
- Memiliki smartphone Android
- Tertarik dengan manajemen keuangan pribadi
- Memiliki investasi atau minat pada emas

**Karakteristik Pengguna**:

- Tech-savvy atau minimal comfortable dengan teknologi mobile
- Ingin tracking keuangan dengan mudah dan praktis
- Concern terhadap transparansi dan keamanan data keuangan

---

## 4. Problem Statement

### Masalah yang Dihadapi Pengguna

1. **Pencatatan Manual Sulit**: Pencatatan transaksi keuangan manual memakan waktu dan mudah terlupakan
2. **Tracking Emas Kompleks**: Sulit melacak jumlah, berat, harga, dan nilai investasi emas secara real-time
3. **Kurangnya Visibility**: Tidak ada overview jelas tentang kondisi keuangan dan aset pribadi
4. **Kategorisasi Transaksi**: Kesulitan mengkategorikan dan menganalisis pengeluaran
5. **Monitoring Nilai Emas**: Sulit update harga emas dan menghitung nilai aset secara otomatis

---

## 5. Fitur Utama (MVP)

### 5.1 Fitur Core

#### A. Manajemen Wallet/Dompet

- **Konsep Wallet**
  - Wallet adalah dompet utama milik user untuk memisahkan aset keuangan
  - Setiap user memiliki wallet untuk tabungan uang dan wallet untuk tabungan emas
  - Wallet menjadi parent entity untuk transaksi uang dan data emas

- **Jenis Wallet**
  - Dompet Tabungan Uang (`cash_savings`)
  - Dompet Tabungan Emas (`gold_savings`)
  - Setiap user memiliki minimal 2 wallet default: satu untuk uang dan satu untuk emas

- **Perilaku Wallet**
  - Wallet tabungan uang memiliki banyak transaksi (`has many transactions`)
  - Wallet tabungan emas memiliki banyak data emas (`has many golds`)
  - Transaksi uang hanya terkait dengan wallet tabungan uang
  - Data emas hanya terkait dengan wallet tabungan emas

#### B. Manajemen Transaksi Keuangan

- **Pencatatan Transaksi**
  - Tambah transaksi masuk (income)
  - Tambah transaksi keluar (expense)
  - Edit transaksi
  - Hapus transaksi
  - Pencatatan tanggal, waktu, nominal, kategori

- **Kategorisasi**
  - Kategori default (Makanan, Transportasi, Hiburan, Investasi, Gaji, dll)
  - Kemampuan membuat kategori custom
  - Kategori untuk setiap transaksi

#### C. Tracking Emas

- **Pencatatan Emas**
  - Tambah data emas pada wallet tabungan emas (tanggal, gram, harga per gram, total nilai)
  - Edit data emas
  - Hapus data emas
  - Pilih jenis emas dari daftar predefined
  - Deskripsi/note tambahan (opsional)

- **Jenis-Jenis Emas yang Didukung**
  - **Emas Murni (Pure Gold)**
    - Emas 24 karat (99.9% murni)
    - Emas 22 karat (91.7% murni)
    - Emas 18 karat (75% murni)
    - Emas 14 karat (58.3% murni)
    - Emas 10 karat (41.7% murni)
  - **Perhiasan Emas (Gold Jewelry)**
    - Cincin
    - Kalung
    - Gelang
    - Anting
    - Liontin
    - Lainnya
  - **Emas Investasi (Investment Gold)**
    - Batangan/Ingot
    - Koin emas
  - **Lainnya (Other)**
    - Emas lainnya yang tidak masuk kategori di atas

- **Monitoring Nilai Emas**
  - Update harga emas secara manual atau otomatis
  - Tracking total gram yang dimiliki pada wallet tabungan emas
  - Tracking total nilai investasi emas pada wallet tabungan emas
  - History perubahan nilai emas

#### D. Dashboard/Overview

- **Ringkasan Keuangan**
  - Total balance/saldo wallet tabungan uang saat ini
  - Total income bulan ini
  - Total expense bulan ini
  - Perbandingan income vs expense

- **Ringkasan Aset Emas**
  - Total gram emas pada wallet tabungan emas
  - Total nilai emas saat ini
  - Perkembangan nilai emas
  - List emas yang dimiliki per wallet

- **Chart & Visualisasi**
  - Pie chart pengeluaran per kategori
  - Bar chart transaksi per bulan
  - Progress visualization aset emas

#### E. Kategori Transaksi

- **Manajemen Kategori**
  - Lihat daftar kategori
  - Tambah kategori custom
  - Edit kategori
  - Hapus kategori
  - Ikon/warna untuk setiap kategori

#### F. Autentikasi & User Account

- **Registrasi Pengguna**
  - Sign up dengan email dan password
  - Validasi email (optional)
  - Password requirements (min 8 karakter, kombinasi huruf & angka)
  - Konfirmasi password

- **Login**
  - Login dengan email dan password
  - Remember me option
  - Forgot password (reset password via email)
  - Session management

- **Logout**
  - Logout dan clear session
  - Clear local cache (optional)
  - Redirect ke login screen

- **User Profile**
  - View profile information
  - Edit nama dan email
  - Change password
  - Delete account (optional)

### 5.2 Fitur Supporting

- **Riwayat Transaksi**
  - List semua transaksi dengan filter
  - Filter berdasarkan tanggal, kategori, tipe (income/expense)
  - Search transaksi
  - Sorting (terbaru, terlama, nominal)

- **Data Persistence**
  - Semua data tersimpan di server (backend/database)
  - Offline mode: Temporary UI cache, sync otomatis saat online
  - No local persistent storage

---

## 6. Technical Architecture

### 6.1 Platform & Tech Stack

- **Mobile**: Android (Native / Kotlin atau Java)
  - SharedPreferences untuk menyimpan JWT token
- **Backend**: Go (Fiber framework)
  - Library: `golang-jwt/jwt` untuk JWT token generation & validation
- **Database**: PostgreSQL (backend/server)
- **API**: RESTful API dengan JWT Bearer token authentication
- **Data Storage**: Server-based (100% data stored di backend)

### 6.2 Komponen Sistem

```
┌─────────────────────────────────────────────────┐
│          Android App (Frontend)                  │
│  - Jetpack Compose / Android XML                │
│  - MVVM Architecture                            │
│  - Temporary UI cache only (no persistent data)│
└────────────────┬────────────────────────────────┘
                 │ HTTP/HTTPS (RESTful API)
┌────────────────▼────────────────────────────────┐
│          Backend Server (Go)                     │
│  - Fiber Framework                              │
│  - PostgreSQL Database                          │
│  - All user data stored here                    │
└─────────────────────────────────────────────────┘
```

---

## 7. User Flows

### 7.1 Sign Up / Register

1. User membuka app (tidak login)
2. Tap "Register" atau "Sign Up"
3. Input email
4. Input password (min 8 karakter)
5. Konfirmasi password
6. Tap "Create Account"
7. Verifikasi email (jika ada)
8. Redirect ke login atau langsung ke onboarding

### 7.2 Login

1. User membuka app
2. Input email
3. Input password
4. Optional: Check "Remember Me"
5. Tap "Login"
6. Validasi credentials
7. Jika berhasil, redirect ke dashboard
8. Jika gagal, tampilkan error message

### 7.3 Onboarding (First Time Login)

1. User login untuk pertama kali
2. Setup profile (nama, mata uang)
3. Setup kategori awal
4. Tutorial singkat
5. Masuk ke dashboard

### 7.4 Pencatatan Transaksi

1. User tap "Tambah Transaksi"
2. Pilih wallet tabungan uang
3. Pilih tipe (income/expense)
4. Input nominal
5. Pilih kategori
6. Add note (opsional)
7. Tap Simpan
8. Kembali ke dashboard

### 7.5 Tracking Emas

1. User tap "Emas" menu
2. View summary emas
3. Tap "Tambah" untuk tambah emas baru
4. Pilih wallet tabungan emas
5. Input gram, harga per gram
6. Tap Simpan
7. Lihat update di dashboard

### 7.6 View Dashboard

1. User membuka app
2. Lihat overview saldo & transaksi
3. Lihat summary emas
4. Lihat chart/visualisasi
5. Tap untuk detail lebih lanjut

---

## 8. Wireframe & UI/UX Considerations

### 8.1 Screen Utama

1. **Login Screen**
   - Email input field
   - Password input field
   - Remember me checkbox
   - Login button
   - Link ke register
   - Link ke forgot password

2. **Register Screen**
   - Email input field
   - Password input field
   - Confirm password field
   - Terms & conditions checkbox
   - Register button
   - Link ke login

3. **Dashboard/Home**
   - Header dengan total saldo
   - Quick stats (income, expense, emas)
   - Charts
   - Recent transactions

4. **Transaction List**
   - List semua transaksi
   - Filter & search
   - Swipe untuk edit/delete

5. **Add Transaction**
  - Form sederhana
  - Pilih wallet tabungan uang
   - Quick category selection
   - Date/time picker

6. **Gold Tracking**
  - Summary emas per jenis pada wallet tabungan emas (Pure, Jewelry, Investment)
   - Total gram dan nilai per jenis
   - Breakdown ringkas setiap jenis emas
   - List data emas dengan filter per jenis
   - Add/Edit/Delete dengan pemilihan jenis

7. **Categories**
   - Manage kategoris
   - Custom kategori

8. **Settings/Profile**
   - Profile settings
   - Change password
   - Currency preference
   - Data backup
   - Logout button

### 8.2 Design Principles

- **Simplicity**: Interface yang clean dan tidak overwhelming
- **Usability**: Mudah digunakan oleh pengguna non-technical
- **Consistency**: Design language yang konsisten
- **Accessibility**: Readable fonts, good contrast

---

## 9. Data Model (Preliminary)

### 9.1 Core Entities

#### Wallet

```
- id (Primary Key)
- user_id (Foreign Key to User)
- name (String)
- type (enum: cash_savings/gold_savings)
- is_default (Boolean, default: true for MVP)
- created_at (DateTime)
- updated_at (DateTime)
```

- Wallet tabungan uang menjadi parent untuk semua transaction.
- Wallet tabungan emas menjadi parent untuk semua gold entry.

#### User

```
- id (Primary Key)
- email (String, unique)
- password_hash (String, hashed)
- name (String)
- currency (String, default: IDR)
- is_verified (Boolean, default: false)
- created_at (DateTime)
- updated_at (DateTime)
```

#### Transaction

```
- id (Primary Key)
- wallet_id (Foreign Key to Wallet, cash_savings only)
- type (enum: income/expense)
- amount (Decimal)
- category_id (Foreign Key)
- description (String, optional)
- date (DateTime)
- created_at (DateTime)
- updated_at (DateTime)
```

#### Category

```
- id (Primary Key)
- user_id (Foreign Key to User, nullable)
- name (String)
- type (enum: income/expense/general)
- icon (String)
- color (String)
- is_custom (Boolean)
- is_default (Boolean)
- created_at (DateTime)
```

#### Gold

```
- id (Primary Key)
- wallet_id (Foreign Key to Wallet, gold_savings only)
- date (DateTime)
- grams (Decimal)
- price_per_gram (Decimal)
- total_value (Decimal)
- type (enum: pure_24k, pure_22k, pure_18k, pure_14k, pure_10k, jewelry_ring, jewelry_necklace, jewelry_bracelet, jewelry_earring, jewelry_pendant, jewelry_other, investment_ingot, investment_coin, other)
- purity_percentage (Decimal, optional - for reference)
- notes (String, optional)
- created_at (DateTime)
- updated_at (DateTime)
```

#### GoldPrice (History)

```
- id (Primary Key)
- date (DateTime)
- price_per_gram (Decimal)
- currency (String, default: IDR)
- created_at (DateTime)
```

#### Session (JWT Token Storage)

```
- id (Primary Key)
- user_id (Foreign Key to User)
- token (String, unique) - JWT token
- token_type (String, default: 'Bearer')
- expires_at (DateTime) - Token expiration time
- created_at (DateTime)
- revoked (Boolean, default: false) - For token revocation (logout)
```

---

## 10. Success Metrics (KPIs)

### 10.1 Adoption Metrics

- Total users downloaded
- Active users (DAU/MAU)
- User retention rate (7-day, 30-day)

### 10.2 Engagement Metrics

- Average transaction entries per user per week
- Feature usage (% users using gold tracking)
- Session duration & frequency

### 10.3 Quality Metrics

- App crash rate
- Bug report frequency
- User rating (App Store)

---

## 11. Monetization Strategy (To Be Determined)

### 11.1 Potential Models

1. **Free to Use**
   - Ad-supported model
   - Data monetization (anonymized)

2. **Freemium**
   - Basic features free
   - Premium features (advanced analytics, cloud sync, etc) - berbayar

3. **Subscription**
   - Monthly/yearly subscription
   - All features included

**Decision**: Akan ditentukan setelah MVP launched dan mendapat user feedback.

---

## 12. Timeline & Roadmap

### 12.1 Phase 1: MVP Development (Ongoing)

- ✓ PRD & Design finalization
- Backend API setup & database
- Android app development
- Core features implementation
- Testing & QA
- Estimated: TBD

### 12.2 Phase 2: Beta Launch

- Soft launch to beta users
- Collect feedback
- Bug fixes & improvements
- Estimated: TBD

### 12.3 Phase 3: Public Release

- Official launch on Google Play Store
- Marketing & user acquisition
- Estimated: TBD

### 12.4 Phase 4: Post-Launch (Future)

- iOS app (if successful)
- Web app (if successful)
- Cloud sync & backup
- Advanced analytics
- Multiple currency support
- Social features (family sharing)
- AI-powered insights

---

## 12.1 Security & Authentication

### JWT (JSON Web Token) Authentication

- **Token Generation**
  - Saat login berhasil, server generate JWT token
  - Token berisi: user_id, email, issued_at, expiration_time
  - Token di-sign menggunakan secret key (HS256 atau RS256)
  - Token disimpan di Session table di server

- **Token Storage & Transmission**
  - Client menyimpan JWT token di SharedPreferences (Android)
  - Token bertipe: `Bearer <token>`
  - Client mengirimkan token di header untuk setiap request:
    - `Authorization: Bearer <jwt_token>`
  - Server mengvalidasi signature token sebelum memproses request

- **Token Validation**
  - Server validate JWT signature menggunakan secret key
  - Server check apakah token sudah expired
  - Server check apakah token sudah di-revoke (logout)
  - Jika valid, lanjut proses request
  - Jika invalid/expired, return 401 Unauthorized

- **Token Lifecycle**
  - Token expiration: 7 hari (configurable)
  - Token refresh: Client bisa request token baru sebelum expiry (optional)
  - Token revocation: Saat logout, token di-mark sebagai revoked di database

### JWT Token Payload (Claims)

```json
{
  "user_id": "uuid-string",
  "email": "user@example.com",
  "iat": 1234567890,
  "exp": 1234654290,
  "type": "access"
}
```

### API Endpoints Authentication

#### Auth Endpoints

- `POST /api/auth/register` - Register akun baru (no auth required)
- `POST /api/auth/login` - Login & return JWT token (no auth required)
- `POST /api/auth/logout` - Logout & revoke token (requires auth)
- `POST /api/auth/refresh` - Refresh token (requires current valid token)
- `POST /api/auth/forgot-password` - Request password reset (no auth required)

#### Protected Endpoints (Require JWT in Authorization header)

- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update user profile
- `GET /api/transactions` - Get user transactions
- `POST /api/transactions` - Create transaction
- `GET /api/gold` - Get user gold data
- `POST /api/gold` - Add gold entry
- (semua endpoint yang mengakses data user)

### Security Considerations

- Password hashing menggunakan bcrypt atau argon2
- JWT secret key harus strong dan secure (di-store di environment variable)
- HTTPS/TLS untuk semua komunikasi (prevent token interception)
- Secure password reset mechanism
- CORS configuration di backend
- Rate limiting untuk login & token refresh attempts
- Token stored di SharedPreferences pada Android (protected by OS)
- Password requirements (minimum 8 karakter, kombinasi huruf & angka)

### Future Authentication (Phase 2+)

- Token refresh endpoint (refresh access token tanpa login ulang)
- OAuth 2.0 (Google, Facebook login)
- Two-factor authentication (2FA)
- Biometric authentication (fingerprint, face recognition)
- Multiple device sessions management

---

## 13. Risks & Mitigation

### 13.1 Identified Risks

| Risk                       | Impact | Probability | Mitigation                               |
| -------------------------- | ------ | ----------- | ---------------------------------------- |
| Data loss                  | High   | Medium      | Implement data backup system             |
| Inaccurate gold price data | Medium | Medium      | Partner with reliable price provider     |
| User adoption              | High   | Medium      | Strong marketing & UX focus              |
| Competition                | Medium | High        | Focus on niche (gold + personal finance) |
| Technical delays           | High   | Medium      | Agile approach & buffer in timeline      |

---

## 14. Assumptions & Constraints

### 14.1 Assumptions

- Pengguna memiliki akses internet untuk update harga emas
- Pengguna nyaman memasukkan data keuangan pribadi di mobile app
- Market Indonesia tertarik dengan aplikasi manajemen keuangan lokal

### 14.2 Constraints

- MVP hanya untuk Android platform
- MVP untuk pengguna individu (bukan bisnis/family sharing)
- Semua data harus tersinkronisasi dengan backend server
- Memerlukan koneksi internet untuk operasi (no offline mode di MVP)
- Tidak ada automated expense tracking di MVP

---

## 15. Success Criteria

### 15.1 MVP Success Criteria

- ✓ Aplikasi dapat mencatat & manage transaksi dengan stabil
- ✓ Fitur tracking emas berfungsi dengan baik
- ✓ Dashboard menampilkan informasi dengan akurat
- ✓ Zero crash rate dalam testing
- ✓ User dapat onboard dalam < 2 menit
- ✓ Minimum 50 beta users engaged

### 15.2 Future Success Criteria

- 10K+ active users dalam 6 bulan
- 4.5+ rating di Google Play Store
- Monthly active user (MAU) growth > 20%
- User retention rate 7-hari > 40%

---

## 16. Documentation & Resources

### 16.1 Additional Documents

- Technical Specification (TBD)
- UI/UX Mockups (TBD)
- API Documentation (TBD)
- Testing Plan (TBD)

### 16.2 References

- [Android Development Guidelines](https://developer.android.com)
- [Go Fiber Framework Docs](https://docs.gofiber.io)
- [RESTful API Best Practices](https://restfulapi.net)

---

## 17. Approval & Sign-off

| Role            | Name | Date | Signature |
| --------------- | ---- | ---- | --------- |
| Product Owner   | TBD  | -    | -         |
| Tech Lead       | TBD  | -    | -         |
| Project Manager | TBD  | -    | -         |

---

**Last Updated**: May 14, 2026  
**Version**: 1.0  
**Status**: DRAFT
