# stockapp-auth

Módulo KMP (Kotlin Multiplatform) + Compose Multiplatform do [StockApp](https://github.com/dgbarreto/stockapp-app) — app de acompanhamento de investimentos (projeto de estudo).

Domain + data (cliente do [`stockapp-backend`](https://github.com/dgbarreto/stockapp-backend), endpoints `/auth`) e telas Compose de login/cadastro.

## Estrutura

- `auth/` — único módulo do repo, alvo Android (via `com.android.kotlin.multiplatform.library`) + iOS (framework estático `Auth`), código comum em `auth/src/commonMain`.

## Status

**Fase 3 — Autenticação** (ver roadmap em `docs/roadmap.md` no repo de planejamento): scaffold criado via [KMP Wizard](https://kmp.jetbrains.com/), ainda sem cliente HTTP nem telas implementadas.

## Stack

- Kotlin 2.4.0 · Compose Multiplatform 1.11.1 · AGP 9.0.1

## Rodando

```
./gradlew :auth:build
./gradlew :auth:testAndroidHostTest
./gradlew :auth:iosSimulatorArm64Test
```

---

_Progresso mantido manualmente conforme o projeto avança._
