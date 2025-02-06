# FocusFlow

FocusFlow est une API REST qui aide les utilisateurs à organiser leurs tâches et à gérer des sessions de focus avec des
statuts comme *PENDING*, *IN_PROGRESS* et *DONE*. Construit avec Spring Boot, l'application utilise JWT
pour l'authentification, WebSocket pour le suivi en temps réel, et inclut une documentation intégrée avec Swagger.

## Table des Matières

- [Technologies](#technologies)
- [Installation](#installation)
- [Configuration](#configuration)
- [Fonctionnalités](#fonctionnalités)
- [Endpoints](#endpoints)
- [Exemples d'Utilisation](#exemples-dutilisation)
- [Tests](#tests)

## Technologies

- **Java 21**
- **Spring Boot 3.x**
- **Spring Security** - Authentification avec JWT
- **Spring WebSocket** - Communication temps réel
- **Spring Data JPA** - Gestion des données
- **PostgreSQL** - Base de données relationnelle
- **Swagger avec Springdoc OpenAPI** - Documentation de l'API
- **JUnit & Mockito** - Tests unitaires et d'intégration

## Installation

1. **Cloner le dépôt :**

    ```bash
    git clone https://github.com/votre-username/focusflow.git
    cd focusflow
    ```

2. **Configurer la base de données :**

    - Créez une base de données PostgreSQL nommée `focusflow`.
    - Mettez à jour `src/main/resources/application.properties` avec vos informations de connexion.

3. **Lancer l'application :**

    ```bash
    ./mvnw spring-boot:run
    ```

   Par défaut, l'application est disponible sur `http://localhost:8080`.

## Configuration

- **JWT :** Le jeton secret JWT peut être configuré dans `application.properties` :
    ```properties
    jwt.secret=VotreSecretJWT
    jwt.expiration=3600000
    ```

- **Swagger :** Accessible depuis `http://localhost:8080/swagger-ui.html` ou
  `https://focusflow-back.onrender.com/swagger-ui.html`.

- **WebSocket Endpoint :** L'endpoint WebSocket principal est `ws://localhost:8080/wsocket`.

- **Hébergement Render :** Accessible depuis `https://focusflow-back.onrender.com/`.

## Fonctionnalités

1. **Authentification :**
    - Inscription et connexion avec JWT.
    - Protection des endpoints nécessitant une authentification.

2. **Gestion des Tâches :**
    - Création, récupération, mise à jour et suppression des tâches.
    - Attribution de tâches aux utilisateurs avec des priorités et des dates d'échéance.

3. **Sessions de Focus :**
    - Démarrer ou reprendre des sessions de focus.
    - Gestion du statut de chaque session.
    - Utilisation des WebSockets pour le suivi en temps réel de chaque session.

## Endpoints

### Authentification

- **POST** `/api/v1/login` : Connexion de l'utilisateur et génération d'un JWT.
- **POST** `/api/v1/signup` : Inscription de l'utilisateur avec un rôle par défaut.

### Tâches

- **GET** `/api/v1/tasks` : Liste des tâches de l'utilisateur connecté.
- **POST** `/api/v1/tasks` : Création d'une nouvelle tâche.
- **GET** `/api/v1/tasks/{id}` : Récupération d'une tâche spécifique.
- **PUT** `/api/v1/tasks/{id}` : Mise à jour d'une tâche.
- **DELETE** `/api/v1/tasks/{id}` : Suppression d'une tâche.

## 📌 API Endpoints - Focus Sessions

### **🔹 Modifier le statut d'une session**

- **PUT** `/api/v1/sessions/status/resume/{sessionId}` : Marquer une session comme **en cours** (`IN_PROGRESS`).
- **PUT** `/api/v1/sessions/status/pending/{sessionId}` : Marquer une session comme **en attente** (`PENDING`).
- **PUT** `/api/v1/sessions/status/done/{sessionId}` : Marquer une session comme **terminée** (`DONE`).

### **🔹 Création de session**

- **POST** `/api/v1/sessions/create` : **Créer et démarrer** une nouvelle session de focus.

### WebSocket

- **Endpoint** : `/wsocket`
- **Souscriptions** :
    - `/topic/sessions/{sessionId}/info` : Suivre les mises à jour d'une session de focus spécifique.

## Exemples d'Utilisation

### Inscription d'un Utilisateur

**Request :**

```json
POST /api/v1/signup
{
  "username": "focusUser",
  "email": "focus@flow.com",
  "password": "securePassword"
}
```

### Démarrer une Session de Focus

**Request :**

```json
PUT /api/v1/sessions/status/start
{
  "taskId": 1
}
```

### Suivi de Session en Temps Réel (WebSocket)

Souscription au topic :

```
/topic/sessions/2/info
```

## Tests

### Lancer les Tests

Les tests unitaires et d'intégration peuvent être lancés avec Maven :

```bash
./mvnw test
```

### Couverture des Tests

- **Unitaires :** JUnit et Mockito sont utilisés pour les services, contrôleurs, et validateurs.
- **Intégration :** Tests avec une base de données en mémoire pour valider les appels aux endpoints REST.

