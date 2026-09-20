# Конститутсияи Ҷумҳурии Тоҷикистон — APK

Барномаи офлайни Android (Kotlin + Jetpack Compose) барои хондан, ҷустуҷӯ ва
захира кардани матни пурраи Конститутсияи Ҷумҳурии Тоҷикистон.

## Хусусиятҳо

- 📖 Матни пурраи Конститутсия: муқаддима + 10 боб + 100 модда (санҷидашуда,
  бе placeholder, бе демо)
- 📄 Realistic page-flip reader бо swipe ва тугмаҳои «Пешина / Баъдӣ»
- 🔍 Ҷустуҷӯи фаврӣ аз рӯи калима, ҷумла ё рақами модда («Моддаи 1»)
- 📑 Мундариҷаи пурра (боб → модда)
- ⭐ Bookmark / Саҳифаҳои захирашуда
- ↩️ Continue Reading — идомаи худкории хондан
- ⚙ Танзимоти андозаи ҳарф, фосилаи сатр ва мавзӯъ (Равшан / Торик / Сепия)
- ⚡ 100% Offline — интернет баъд аз насб лозим нест, бе server, бе login

## Сохтори лоиҳа

```
app/src/main/assets/constitution.json   — матни санҷидашудаи Конститутсия
app/src/main/java/tj/constitution/book/
  data/            — модел, репозиторий, pagination, search, DataStore
  ui/screens/       — Home, Reader, TOC, Search, Bookmarks, Settings, About
  MainActivity.kt   — Navigation graph
.github/workflows/build.yml — GitHub Actions: сохтани APK худкор
```

## Манбаи матни ҳуқуқӣ

Матни моддаҳо аз нусхаи расмии Конститутсия гирифта шуда, бо пойгоҳи
WIPO Lex (сомонаи Созмони Ҷаҳонии Моликияти Зеҳнӣ, ки матни расмии
Тоҷикистонро нашр мекунад) ва маълумоти Маркази миллии қонунгузории назди
Президенти Ҷумҳурии Тоҷикистон (mmk.tj) муқоиса шудааст. Дастрасии
мустақим ба файли PDF-и mmk.tj аз тариқи robots.txt маҳдуд буда, барои
ҳамин манбаи WIPO Lex (нусхаи расмии пешниҳодшуда аз ҷониби давлат)
истифода шудааст. Пеш аз дохил кардан ба `constitution.json` скрипти
санҷиш (article count, рақамгузории пайдарпай, моддаҳои холӣ, ҷобаҷогузории
бобҳо) иҷро шуда, натиҷа тасдиқ мекунад: **100 модда, 10 боб, бе хатои
рақамгузорӣ, бе матни холӣ**.

## Насб аз Termux (бе PC)

```bash
pkg install git -y
git clone <URL-и repository-и шумо>
cd tj-constitution
git branch -M main
git remote add origin <URL-и repository-и шумо>
git push -u origin main
```

Пас аз push, GitHub Actions худкор APK-ро месозад. Барои дидани натиҷа:

1. Ба GitHub → **Actions** → workflow «Build APK» гузаред.
2. Пас аз анҷоми build (якчанд дақиқа), дар қисми **Artifacts** ду файл
   пайдо мешавад: `konstitutsiya-debug-apk` ва `konstitutsiya-release-apk`.
3. Файлро зеркашӣ (download) карда, ба телефон интиқол диҳед ва насб кунед.

> **Тавзеҳ:** `gradlew`/`gradle-wrapper.jar` дар repository қасдан гузошта
> нашудааст (файли binary аст ва бе интернет офлайн сохта намешавад).
> Workflow-и GitHub Actions бевосита Gradle 8.7-ро тавассути
> `gradle/actions/setup-gradle` насб карда, `gradle assembleDebug` /
> `assembleRelease`-ро иҷро мекунад — ба шумо ягон коре бо wrapper лозим
> намеояд.

## Санҷиши маҳаллӣ (агар Gradle дар Termux насб бошад)

```bash
pkg install openjdk-17 gradle -y
gradle assembleDebug
```

APK дар `app/build/outputs/apk/debug/app-debug.apk` пайдо мешавад.

## Танзимоти package

- `applicationId`: `tj.constitution.book`
- `versionName`: `1.0.0`
- `minSdk`: 26 (Android 8.0+)

## Санҷиши сифат (Final QA)

Пеш аз ҳар release тавсия дода мешавад:

- **UI TEST** — ҳамаи тугмаҳо (Хондан, Ҷустуҷӯ, Мундариҷа, Захирашудаҳо,
  Танзимот, Дар бораи барнома).
- **SEARCH TEST** — якчанд калима ва рақами модда.
- **BOOKMARK TEST** — save/remove/open.
- **READER TEST** — Next/Previous, swipe.
- **OFFLINE TEST** — Airplane mode фаъол карда, тамоми функсияҳоро санҷед.
- **DATA TEST** — шумораи моддаҳо (100), тартиби бобҳо (10).
- **BUILD TEST** — `gradle assembleRelease` бе хато анҷом ёбад.
