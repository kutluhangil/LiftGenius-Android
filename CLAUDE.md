# LiftGenius — Android Uygulaması Yapım Rehberi (CLAUDE.md)

> Bu dosya, **LiftGenius** uygulamasının Android sürümünü sıfırdan yazmak için hazırlanmıştır.
> Kaynak: yayında olan **iOS (Swift/SwiftUI)** uygulaması. iOS ve Android **aynı Supabase
> backend'ini paylaşır**. Bu belge Android repo'sunun köküne `CLAUDE.md` olarak konulmalıdır ki
> Android tarafında çalışan asistan bu kurallara otomatik uysun.

---

## 0. En Önemli Kural (ÖNCE BUNU OKU)

**Backend (Supabase veritabanı) iOS ile ORTAKTIR. İki uygulama da aynı tablolara bağlanır.**

- Android tarafında **veritabanı şemasını (tablo/kolon/enum değerleri) DEĞİŞTİRME.** Sadece
  var olan tabloları oku/yaz.
- Bir özellik veritabanı değişikliği (yeni kolon, kolon silme, tip değişikliği, enum değeri
  değişimi) gerektiriyorsa: **DUR ve kullanıcıyı uyar.** Şu cümleyi kur:
  > "⚠️ Bu değişiklik Supabase şemasına dokunuyor. iOS uygulaması aynı tabloyu kullandığı için
  > bu değişiklik iOS'ta KIRILMAYA yol açabilir. Devam etmeden önce iOS tarafını da güncellemen
  > gerekir. Onaylıyor musun?"
- Kolon/enum **isimleri ve string değerleri iOS ile birebir aynı olmak ZORUNDA.** (Aşağıdaki
  şema bölümüne bak.) Tek bir harf farkı bile veri okumayı bozar.

---

## 1. Hedef ve Kapsam

LiftGenius, **kişisel antrenörler ve fitness salonları** için bir CRM uygulamasıdır. Ana işlevler:

- Müşteri (danışan) yönetimi
- Antrenman programları (gün + egzersiz hiyerarşisi)
- Beslenme planları (makro + öğün)
- Seans takvimi
- Paket/üyelik ve ödeme takibi
- İlerleme ölçümleri + rekor (PR) takibi
- Finans/gelir özeti
- Çoklu eğitmen & salon rolleri (owner / trainer)
- Yapay zekâ ile program/beslenme üretimi (Google Gemini)

Android sürümü **iOS ile aynı özellik setini** hedefler ama **tasarım birebir aynı olmak zorunda
değildir** — Material Design 3 kullanılır, iOS ekranları yalnızca **referans** olarak alınır.

---

## 2. Teknoloji Yığını (Tech Stack)

| Katman | Seçim |
|---|---|
| Dil | **Kotlin** (en güncel kararlı sürüm) |
| UI | **Jetpack Compose** + **Material 3** |
| IDE | **Android Studio** (en güncel kararlı sürüm) |
| Mimari | **MVVM** + Repository pattern |
| Async | **Kotlin Coroutines + Flow** |
| DI | **Hilt** (Dagger) |
| Backend SDK | **supabase-kt** (`io.github.jan-tennert.supabase`) |
| Serialization | **kotlinx.serialization** |
| Navigation | **Navigation Compose** |
| Görsel yükleme | **Coil** |
| Min SDK | **26 (Android 8.0)** |
| Target/Compile SDK | En güncel (Android 15 / API 35) |

### Neden bunlar?
- **Jetpack Compose**: iOS'taki SwiftUI'nin Android karşılığı — deklaratif UI, en modern yol.
- **supabase-kt**: Supabase'in resmî Kotlin kütüphanesi. iOS'taki Swift Supabase SDK ile aynı
  backend'e konuşur (GoTrue auth, PostgREST, Realtime, Storage).
- **kotlinx.serialization**: iOS'taki `Codable`'ın karşılığı. `@SerialName` ile snake_case eşleme.

---

## 3. Proje Kurulumu (Adım Adım)

1. **Yeni repo aç** (örn. `liftgenius-android`). iOS repo'suna dokunma; o ayrı kalır.
2. Android Studio → **New Project → Empty Activity (Compose)**.
   - Package name: `com.kutluhangul.liftgenius` (iOS bundle ile aynı olabilir; Play Store'da
     ayrı uygulama kaydıdır, çakışmaz).
   - Language: Kotlin, Minimum SDK: **API 26**.
3. Bu `CLAUDE.md` dosyasını repo köküne koy.
4. `libs.versions.toml` (version catalog) ile bağımlılıkları ekle (aşağıdaki listeye göre).
5. Supabase anahtarlarını **`local.properties`** veya bir `secrets.properties` dosyasına koy
   (git'e commit'leme — `.gitignore`'a ekle). iOS'ta bu `Config.xcconfig`'ti; Android'de
   `local.properties` + `BuildConfig` kullanılır.

### Önerilen Gradle bağımlılıkları (kavramsal liste)
```
- supabase-kt: postgrest-kt, auth-kt (gotrue), realtime-kt (opsiyonel), storage-kt (opsiyonel)
- ktor-client-android  (supabase-kt bunu ister)
- kotlinx-serialization-json
- androidx.compose (BOM), material3, navigation-compose, lifecycle-viewmodel-compose
- hilt-android + hilt-navigation-compose
- coil-compose
- androidx.credentials + googleid  (Google Sign-In için)
```

---

## 4. ORTAK BACKEND SÖZLEŞMESİ (En Kritik Bölüm)

**Supabase Projesi**
- URL: `https://exszotuhvjljgglcfkyx.supabase.co`
- Anon Key (public / RLS ile korunur, istemciye gömülür):
  `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` (iOS'taki `Config.xcconfig` içindeki anon key ile
  **aynısını** kullan. Anon key istemcide bulunması normaldir; güvenlik RLS ile sağlanır.)

> Not: Bu anon key herkese açık olabilir; asıl güvenlik Supabase **Row Level Security (RLS)**
> politikalarındadır. RLS zaten iOS için kurulu olduğundan Android'de **aynı politikalar geçerli**
> — ekstra bir şey yapmana gerek yok.

### 4.1 Tablolar (10 adet)

Aşağıdaki tablo/kolon isimleri ve tipleri **iOS ile birebir aynıdır ve değiştirilemez.**
Tüm kolonlar Supabase'te **snake_case**'tir. Kotlin modellerinde `@SerialName("snake_case")` kullan.

#### `clients`
| Kolon | Tip | Null? | Not |
|---|---|---|---|
| id | uuid | hayır | PK |
| full_name | text | hayır | |
| phone | text | evet | |
| email | text | evet | |
| birth_date | timestamptz/date | evet | |
| gender | text (enum) | evet | `male` / `female` / `other` |
| goal | text (enum) | evet | FitnessGoal (bkz. 4.2) |
| notes | text | evet | |
| status | text (enum) | hayır | `active` / `inactive` / `trial` / `frozen` |
| weight | float8 | evet | kg |
| height | float8 | evet | cm |
| profile_image_url | text | evet | |
| created_at | timestamptz | hayır | |
| trainer_id | uuid | hayır | Sahibi eğitmen (auth user id) |

#### `client_progress`
| id uuid PK | client_id uuid | date timestamptz | weight float8? | body_fat float8? | muscle_mass float8? | chest float8? | arm_left float8? | arm_right float8? | waist float8? | hips float8? | created_at timestamptz |

#### `client_prs` (rekor ağırlıklar)
| id uuid PK | client_id uuid | exercise_name text | weight float8 | reps int | date timestamptz | created_at timestamptz |

#### `packages` (üyelik/paket)
| id uuid PK | client_id uuid | name text | total_sessions int | remaining_sessions int | price float8 | payment_method text? (`cash`/`transfer`/`creditCard`) | start_date timestamptz | end_date timestamptz? | is_paid bool | created_at timestamptz |

#### `sessions` (seanslar)
| id uuid PK | client_id uuid | package_id uuid? | date timestamptz | duration_minutes int | title text? | status text (`scheduled`/`completed`/`cancelled`/`noShow`) | notes text? | created_at timestamptz |

#### `nutrition_plans`
| id uuid PK | client_id uuid | daily_calories int | protein_grams int | carb_grams int | fat_grams int | meal_plan_text text | notes text? | created_at timestamptz | trainer_id uuid |

#### `workout_plans`
| id uuid PK | client_id uuid | title text | description text? | created_at timestamptz | trainer_id uuid |

#### `workout_days`
| id uuid PK | plan_id uuid | day_name text | order_index int |

#### `exercises`
| id uuid PK | day_id uuid | name text | category text? | sets int | reps text | weight text? | notes text? | order_index int |
> Not: `reps` ve `weight` **String**'tir ("10-12", "Max", "Bodyweight", "50 kg" olabildiği için).

#### `trainer_profiles`
| id uuid PK (auth user id) | full_name text | role text (`owner`/`trainer`) | salon_name text? | created_at timestamptz |

### 4.2 Enum değerleri (STRING olarak DB'de saklanır — birebir aynı olmalı)

Bu string'ler veritabanına **camelCase** olarak yazılır. Kotlin enum'larını bu değerlere
**tam eşleyecek** şekilde `@SerialName` ile yaz.

- **Gender**: `male`, `female`, `other`
- **ClientStatus**: `active`, `inactive`, `trial`, `frozen`
- **FitnessGoal**: `weightLoss`, `fatLoss`, `muscleGain`, `strength`, `toning`, `generalHealth`,
  `flexibility`, `endurance`, `athleticPerformance`, `rehabilitation`, `posture`, `weightGain`
- **PaymentMethod**: `cash`, `transfer`, `creditCard`
- **SessionStatus**: `scheduled`, `completed`, `cancelled`, `noShow`
- **Role**: `owner`, `trainer`

> ⚠️ `creditCard`, `noShow`, `athleticPerformance`, `weightLoss` gibi değerler camelCase. iOS bunları
> aynen bu şekilde yazıyor. Android'de `credit_card` gibi yazarsan iOS verisini okuyamaz ve tersi.

### 4.3 Örnek Kotlin model (referans)
```kotlin
@Serializable
data class Client(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val phone: String? = null,
    val email: String? = null,
    @SerialName("birth_date") val birthDate: Instant? = null,
    val gender: Gender? = null,
    val goal: FitnessGoal? = null,
    val notes: String? = null,
    val status: ClientStatus,
    val weight: Double? = null,
    val height: Double? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("trainer_id") val trainerId: String,
)

@Serializable
enum class SessionStatus {
    @SerialName("scheduled") SCHEDULED,
    @SerialName("completed") COMPLETED,
    @SerialName("cancelled") CANCELLED,
    @SerialName("noShow")    NO_SHOW,
}
```
> Kural: **DB'ye giden string** `@SerialName`'deki değerdir. Enum sabit adını (SCHEDULED) istediğin
> gibi yaz ama `@SerialName` iOS ile aynı olmalı.

---

## 5. Kimlik Doğrulama (Auth)

iOS'ta üç yöntem var. Android'de öncelik sırası:

1. **E-posta / şifre** — birebir aynı, `supabase.auth.signInWith(Email)` / `signUpWith(Email)`.
   - Kayıt sonrası e-posta onayı davranışı iOS ile aynı: oturum yoksa "e-posta onayı bekleniyor".
2. **Google ile giriş** — Android'de **Credential Manager + Google ID token** alınır, sonra
   `supabase.auth.signInWith(IDToken)` ile provider `Google` olarak Supabase'e verilir.
   - Supabase panelinde Google provider zaten açık (iOS için kuruldu). Android için Google Cloud'da
     **Android OAuth client ID** (SHA-1 parmak izi ile) eklemen gerekebilir.
3. **Apple ile giriş** — iOS'ta native var. Android'de zorunlu değil; istenirse OAuth web akışıyla
   eklenir. **İlk sürümde atlanabilir** (Android kullanıcıları genelde Google/e-posta kullanır).

> Auth kullanıcısı (`auth.uid()`) = `trainer_profiles.id` ve tablolardaki `trainer_id`. Bu ilişki
> iOS ile aynı; değiştirme.

---

## 6. Yapay Zekâ (Gemini) Entegrasyonu

- Endpoint: `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent`
- API anahtarı: iOS'ta kullanıcı `UserDefaults`'a girebiliyor veya `Info.plist`'te `GEMINI_API_KEY`
  var. Android'de karşılığı: **DataStore/SharedPreferences** (kullanıcı girerse) veya
  `BuildConfig.GEMINI_API_KEY` (local.properties'ten).
- Kullanım: antrenman programı ve beslenme planı **üretimi**. Prompt → Gemini → dönen metin
  parse edilip ilgili tablolara (`workout_plans`/`workout_days`/`exercises` veya `nutrition_plans`)
  kaydedilir.
- Bu backend'e ait değil, harici Google API — iOS/Android bağımsız çalışır, çakışma yok.

---

## 7. Ekranlar / Özellikler (iOS referansı)

iOS'taki ekran grupları (Android'de Compose ekranları olarak yeniden yazılacak):

- **Auth**: Splash, Intro/Onboarding, Welcome, Login, Register
- **Ana (MainTab)**: Dashboard, Takvim (Calendar), Müşteriler (ClientList), Finans, Profil
- **Müşteri**: ClientDetail, AddClient, EditClient, AddPackage, AddProgress, AddPR
- **Planlar**: AI Workout Generator, AI Nutrition Generator, program/beslenme görüntüleme
- **Profil/Takım**: Profile, TeamManagement (çoklu eğitmen/salon)
- **PDF**: Antrenman/Beslenme/Finans PDF çıktısı (Android'de PDF üretimi ayrı kütüphane ile)

Widget/Live Activity (iOS'a özel) → Android'de karşılığı **App Widget**; ilk sürümde opsiyonel.

Her ekran için: iOS ekran görüntüsünü referans al, Material 3 ile Android'e uygun şekilde tasarla.
**Birebir aynı görünmek zorunda değil**, işlev ve veri aynı olsun.

---

## 8. Mimari Kurallar

- **Katmanlar**: `ui` (Compose + ViewModel) → `domain` (modeller, use-case'ler) →
  `data` (repository + Supabase kaynağı).
- Supabase erişimi tek bir yerde toplansın: iOS'taki `SupabaseManager` + `*Service` yapısının
  karşılığı → `SupabaseClientProvider` + `ClientRepository`, `SessionRepository`,
  `WorkoutRepository`, `NutritionRepository`, `BusinessRepository` (trainer/salon).
- **Savunmacı okuma**: Model alanları nullable + varsayılan değerli olsun. Backend'e ileride yeni
  kolon eklenirse eski Android sürümü çökmesin (bilinmeyen alanları yok say).
  `Json { ignoreUnknownKeys = true }` KULLAN.

---

## 9. ORTAK vs BAĞIMSIZ — Özet Tablo

| Öğe | Durum |
|---|---|
| Supabase veritabanı (tablolar, kolonlar, enum string'leri) | **ORTAK — değiştirme** |
| Supabase Auth (kullanıcılar, sağlayıcılar) | **ORTAK** |
| RLS politikaları | **ORTAK** (Android'de ekstra iş yok) |
| Gemini API | Ortak servis, kod bağımsız |
| UI / ekran tasarımı | **BAĞIMSIZ** — Material 3, farklı olabilir |
| Uygulama içi mantık (ViewModel vb.) | **BAĞIMSIZ** — Kotlin'de yeniden yazılır |
| Widget / bildirim / PDF | Platforma özel, bağımsız |
| Play Store kaydı | Ayrı (iOS App Store'dan bağımsız) |

---

## 10. DEĞİŞİKLİK YAPARKEN UYARI KURALLARI (Asistan için)

Android tarafında çalışan asistan şu durumlarda **DURUP kullanıcıyı uyarmalı**:

1. **Tablo/kolon ekleme, silme, yeniden adlandırma** veya **tip değiştirme** gerektiren her istek:
   > "⚠️ Bu Supabase şemasını değiştirir → iOS aynı tabloyu kullanıyor, KIRILABİLİR. Önce iOS'u da
   > güncellemen gerekir. Onaylıyor musun?"
2. **Enum'a yeni değer ekleme**: Güvenli olması için **sona ekle**, mevcut değerleri değiştirme.
   Yine de "iOS bu yeni değeri bilmiyor, iOS güncellenene kadar o değeri gösteremeyebilir" uyarısı ver.
3. **Bir kolonu zorunlu (NOT NULL) yapma** veya varsayılanını değiştirme: iOS eski verisiyle
   uyumsuz kalabilir → uyar.
4. **Tablo/grafik gibi görsel bir bileşen** temel veri yapısını değiştiriyorsa (örn. yeni alan
   okuyorsa), önce o alanın DB'de olup olmadığını kontrol et; yoksa şema değişikliği gerektirir → uyar.

**Güvenli değişiklikler (uyarı gerektirmez):** Sadece UI, yeni ekran, mevcut veriyle yeni görünüm,
yeni kolon *ekleme* (bozmayan/additive), Android'e özel özellikler.

---

## 11. Başlarken İlk Görevler (Sıra)

1. Proje + bağımlılık + `BuildConfig` anahtarları (Supabase URL/anon key, Gemini key).
2. `SupabaseClientProvider` (auth + postgrest yapılandırması, `ignoreUnknownKeys = true`).
3. Auth akışı (e-posta/şifre → sonra Google).
4. Modeller (bölüm 4.1–4.2'ye göre, `@SerialName` ile).
5. Repository katmanı (clients → sessions → packages → workout → nutrition → trainer).
6. Ekranlar (Auth → Dashboard → ClientList/Detail → Calendar → Finans → Profil).
7. AI üretim ekranları (Gemini).
8. Play Store hazırlığı (ikon, ekran görüntüleri, gizlilik, imzalı AAB).

---

_Bu belge iOS kaynak koduna (Swift modelleri + SupabaseManager) göre hazırlanmıştır. Şema
değişikliği gerektiğinde her iki platformu da senkron güncelle. Tek gerçek kaynak: Supabase._
