# LiftGenius — Tasarım Sistemi (Renk Paleti & Tokenlar)

> iOS uygulamasının `DesignTokens.swift` dosyasından birebir çıkarılmıştır. Android (Compose +
> Material 3) tarafında `ui.theme` altında `Color.kt` / `Theme.kt` / dimensler bu değerlere göre
> kurulmalı. Renkler **birebir aynı olmak zorunda değil ama marka kimliği (turuncu→pembe gradient)
> korunmalı.** Material 3 dinamik renk yerine bu sabit marka paleti kullanılacak.

## Marka Kimliği
Logo: **Sonsuzluk + Halter**, üç renkli neon gradient.
> Neon Turuncu `#FF6B1A` → Kor Kırmızı `#FF4455` → Neon Pembe `#FF2D8B`

## 1. Marka / Aksan Renkleri (light & dark ortak)
| Rol | Hex | Not |
|---|---|---|
| accent (ana) | `#FF6B1A` | Neon turuncu — primary |
| accentMid | `#FF4455` | Kor kırmızı — gradient ortası |
| accentSecondary | `#FF2D8B` | Neon pembe — secondary |
| **Ana gradient** | `#FF6B1A → #FF4455 → #FF2D8B` | Butonlar, hero, logo (yatay/dikey/çapraz) |

## 2. Sistem Durum Renkleri
| Rol | Hex |
|---|---|
| success | `#34D399` |
| warning | `#FF9F0A` |
| danger / error | `#FF4455` |

## 3. Arka Plan (Background) — Dark / Light
| Rol | Dark | Light |
|---|---|---|
| bgPrimary | `#0F0E13` (charcoal) | `#F8F6FA` (warm white) |
| bgSecondary | `#18171E` | `#FFFFFF` |
| bgTertiary | `#22212A` | `#EAE7EE` |

## 4. Metin (Text) — Dark / Light
| Rol | Dark | Light |
|---|---|---|
| textPrimary | `#FCFAFF` | `#100C18` |
| textSecondary | `#FCFAFF` @ %68 opaklık | `#100C18` @ %68 |
| textTertiary | `#FCFAFF` @ %35 opaklık | `#100C18` @ %35 |

## 5. Yüzey / Kenar (Glass & Border) — Dark / Light
| Rol | Dark | Light |
|---|---|---|
| cardBorder | `#FF6B1A` @ %14 | `#FF2D8B` @ %10 |
| glassHairline | beyaz @ %12 | beyaz @ %90 |
| glassSpecular | beyaz @ %18 | beyaz @ %95 |
| ledgerDivider | beyaz @ %8 | `#1A1420` @ %9 |
| ambientGlow | turuncu@%18 → pembe@%12 (çapraz) | aynı |

> Tema koyu (dark) ağırlıklı bir "Obsidian Glass" estetiğine sahip: koyu charcoal zemin, cam kart
> yüzeyleri, kısık turuncu/pembe ambient glow. Android'de de varsayılan **dark** öne çıkarılabilir,
> ama light tema değerleri yukarıda mevcut.

## 6. Köşe Yarıçapı (Corner Radius) — dp
| xs | sm | md | lg | xl | full |
|---|---|---|---|---|---|
| 4 | 8 | 12 | 16 | 24 | 9999 (tam yuvarlak) |

## 7. Boşluk (Spacing) — dp
| xs | sm | md | lg | xl | xxl | xxxl |
|---|---|---|---|---|---|---|
| 4 | 8 | 12 | 16 | 20 | 24 | 32 |

## 8. Tipografi (sp / ağırlık)
> iOS "rounded" fontu kullanıyor. Android'de en yakın karşılık: sistem fontu + `FontWeight`.
> İstenirse "rounded" hissi için Google Fonts'tan **Nunito** veya **Quicksand** kullanılabilir.

| Rol | Boyut (sp) | Ağırlık |
|---|---|---|
| dashboardTitle | 28 | Bold |
| greetingTitle | 22 | Bold |
| sectionHeader | 20 | Bold |
| dashboardSubtitle | 18 | Medium |
| cardTitle | 16 | SemiBold |
| statValue | 24 | Bold |
| heroNumber | 44 | Heavy/Black (tabular rakam) |
| ledgerValue | 20 | Medium (serif) |
| statLabel | 13 | Medium |
| kickerLabel | 11 | SemiBold (harf aralığı ~2.4) |
| caption | 12 | Regular |

## 9. Compose için örnek `Color.kt` (referans)
```kotlin
// Marka
val Accent          = Color(0xFFFF6B1A)
val AccentMid       = Color(0xFFFF4455)
val AccentSecondary = Color(0xFFFF2D8B)
val BrandGradient   = listOf(Accent, AccentMid, AccentSecondary) // Brush.linearGradient(...)

// Durum
val Success = Color(0xFF34D399)
val Warning = Color(0xFFFF9F0A)
val Danger  = Color(0xFFFF4455)

// Dark
val BgPrimaryDark   = Color(0xFF0F0E13)
val BgSecondaryDark = Color(0xFF18171E)
val BgTertiaryDark  = Color(0xFF22212A)
val TextPrimaryDark = Color(0xFFFCFAFF)

// Light
val BgPrimaryLight   = Color(0xFFF8F6FA)
val BgSecondaryLight = Color(0xFFFFFFFF)
val BgTertiaryLight  = Color(0xFFEAE7EE)
val TextPrimaryLight = Color(0xFF100C18)
```

> Material 3 `ColorScheme`: `primary = Accent`, `secondary = AccentSecondary`, `error = Danger`,
> `background/surface = Bg*`, `onBackground/onSurface = TextPrimary*`. Gradient'ler `Brush` olarak
> ayrı tutulur (Material renk şemasına gradient girmez).
