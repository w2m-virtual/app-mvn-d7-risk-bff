# app-mvn-d7-risk-bff

W2M Virtual · D7 Risk & Compliance — antifraude pre-booking.

El orquestador llama a este servicio entre `payments.authorize` (paso #2) y
`supplier.create-booking` (paso #4). Si la valoración es **DENY**, el orquestador
reaprovecha la saga del paso #3 para hacer refund automático.

## Stack

- Spring Boot 3.5.5, Java 24
- Hexagonal puro (módulo `risk` dominio + `app` bootstrap)
- Persistencia in-memory (`ConcurrentHashMap`)
- Reglas mock (RuleEngine) — fácilmente extensibles a un motor real

## Arrancar

```bash
mvn -DskipTests package
java -jar app/target/app-0.1.0-SNAPSHOT.jar
```

Servicio en http://localhost:8086.

## Endpoints

| Verb | Path | Descripción |
| --- | --- | --- |
| POST | `/api/risk/assess` | Evalúa el riesgo de un intento de booking |
| GET | `/api/risk/assessments/{id}` | Recupera la valoración persistida |
| GET | `/actuator/health` | Health probe |

## Reglas mock

1. **EmailBlacklistRule** — DENY directo si `holderEmail` en lista hardcoded.
2. **InvalidDomainRule** — DENY directo si email termina en `@blocked.invalid` o `.test`.
3. **HighAmountRule** — `amount > 5000` suma score 40 (REVIEW).
4. **HighRiskCountryRule** — country en `["XX","YY"]` suma score 30.
5. **HolderNameSuspiciousRule** — DENY directo si `holderName` contiene "FRAUD".
6. **DefaultRule** — score base 10 si nadie marcó nada.

Decisión final: cualquier DENY → DENY · score ≥ 70 → DENY · score ≥ 40 → REVIEW · resto → APPROVE.

## Configuración relevante

```yaml
server.port: 8086
```
