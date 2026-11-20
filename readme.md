# Trading API

API REST (Spring Boot 4, Java 21, PostgreSQL, Docker) pour suivre un challenge de trading
chez une prop firm : un **compte de trading** possède plusieurs **trades**, et la couche
service applique les règles de risque du challenge (perte quotidienne 4 %, perte maximale 12 %).

Projet du module R3.03 (BUT Informatique 2e année, IUT du Littoral Côte d'Opale).

## Équipe
- Noaïm MAILLARD
- Gabriel LANIEZ
- Marin BOSC

## Lancer le projet
Prérequis : Docker Engine ou Docker Desktop, avec Docker Compose.

```bash
docker compose up
```

| Service | Adresse |
|---|---|
| API | http://localhost:8080 |
| Adminer (gestion de la base) | http://localhost:8081 |
| PostgreSQL | localhost:5433 |

Les données sont conservées dans le volume `postgres_data`.
Pour tout remettre à zéro : `docker compose down -v` puis `docker compose up -d`.

**Attention :** `docker compose down -v` supprime le volume PostgreSQL et toutes les données qu'il contient.

## Architecture
`Controller (DTO)` → `Service (interface + implémentation)` → `Repository (Entity)`

| Package | Rôle |
|---|---|
| `controller` | Endpoints HTTP, aucune logique métier |
| `dto` | Objets d'entrée/sortie avec validation (`@Valid`) |
| `entity` | Entités JPA : `TradingAccount` (1) — (N) `Trade` |
| `repository` | Repositories Spring Data JPA |
| `service` / `service.impl` | Logique métier (règles de risque) |
| `exception` | Exceptions métier et gestionnaire d'erreurs global |

## Endpoints

### Comptes
| Méthode | URL | Succès | Erreurs |
|---|---|---|---|
| POST | `/api/accounts` | 201 | 400 |
| GET | `/api/accounts` | 200 | |
| GET | `/api/accounts/{id}` | 200 | 404 |
| PUT | `/api/accounts/{id}` | 200 | 400, 404 |
| DELETE | `/api/accounts/{id}` | 204 | 404 |

### Trades
| Méthode | URL | Succès | Erreurs |
|---|---|---|---|
| POST | `/api/trades` | 201 | 400, 404 (compte inconnu), 409 (règle de risque) |
| GET | `/api/trades` | 200 | |
| GET | `/api/trades/{id}` | 200 | 404 |
| GET | `/api/accounts/{accountId}/trades` | 200 | 404 |
| PUT | `/api/trades/{id}` | 200 | 400, 404 |
| DELETE | `/api/trades/{id}` | 204 | 404 |

### Exemples
Créer un compte :
```json
POST /api/accounts
{ "name": "FundingPips 10k", "initialCapital": 10000 }
```
Créer un trade (`direction` : `BUY` ou `SELL`, `openedAt` optionnel) :
```json
POST /api/trades
{ "instrument": "XAUUSD", "direction": "BUY", "profitLoss": -120.50, "accountId": 1 }
```
Le champ `balance` d'un compte est calculé (capital initial + somme des trades).

## Règle métier
Un nouveau trade est refusé avec **409 Conflict** quand, pour le compte :
- la perte totale a déjà atteint 12 % du capital initial, ou
- la perte du jour du trade a déjà atteint 4 % du capital initial.

## Tests
Collection Bruno dans le dossier `bruno/` : ouvrir le dossier dans Bruno, choisir
l'environnement `local`, lancer la collection (15 requêtes, CRUD complet, cas 400, 404 et 409).

## Organisation
Tickets et répartition par personne dans l'onglet *Projects* du dépôt GitHub.
Commits au format `feat:`, `fix:`, `docs:`, une branche par ticket, une pull request relue par un autre membre.

## Suite prévue
- Sprint 2 : authentification
- Ensuite : Swagger, tests unitaires, test de performance, interface front
