# HackHub 🚀

> Piattaforma web full-stack per gestire l'intero ciclo di vita di un hackathon — dall'iscrizione dei team alla proclamazione dei vincitori.

---

## 📖 Panoramica del progetto

**HackHub** è un'applicazione web pensata per digitalizzare completamente la gestione degli hackathon — competizioni a tempo in cui i team sviluppano e consegnano un progetto per raggiungere un obiettivo comune. La piattaforma centralizza ogni fase dell'evento, dall'iscrizione dei partecipanti alla proclamazione dei vincitori.

Costruita con un backend in **Java Spring Boot** e un frontend in **Angular 21**, HackHub offre un ambiente basato sui ruoli in cui gli organizzatori creano e gestiscono gli eventi, i team si iscrivono e consegnano i propri progetti, i giudici valutano le consegne e i mentori supportano i partecipanti durante la competizione.

---

## ✨ Funzionalità

- 🏆 Creazione e gestione di hackathon con ciclo di vita completo a 4 stati
- 👥 Creazione dei team, inviti e gestione delle iscrizioni
- 📁 Consegna del progetto tramite link a repository GitHub, aggiornabile fino alla scadenza
- ⭐ Sistema di valutazione dei giudici con punteggio numerico (0–10) e feedback scritto
- 🚩 Segnalazione dei team da parte dei mentori per violazioni del regolamento
- 🔒 Controllo degli accessi basato sui ruoli (Visitatore, Utente, Staff, Admin)
- 🔐 Autenticazione tramite JWT con claim di ruolo
- 🛡️ Route guard Angular per la protezione del frontend
- 🔍 Filtri, ordinamento e paginazione avanzati per la ricerca di hackathon e team

---

## 🛠️ Stack tecnologico

### Backend
| Tecnologia | Dettagli |
|---|---|
| Java | 21 |
| Spring Boot | 4 |
| Spring Web | API REST con `@RestController` |
| Spring Security | Autenticazione JWT + RBAC |
| Spring Data JPA | Persistenza basata su repository (Hibernate) |
| H2 Database | Database relazionale su file (`database/hackhubdb.mv.db`) |
| Apache Maven | Build e gestione delle dipendenze |

### Frontend
| Tecnologia | Dettagli |
|---|---|
| Angular | 21 |
| TypeScript | JavaScript tipizzato staticamente |
| SCSS | CSS avanzato con variabili, mixin e nesting |
| Bootstrap 5 | Layout a griglia responsive e componenti UI |

---

## 👤 Ruoli e attori

| Ruolo | Descrizione |
|---|---|
| **Visitatore** | Utente non autenticato, può consultare le informazioni pubbliche sugli hackathon |
| **Utente** | Utente registrato, può creare o unirsi a un team |
| **Staff** | Personale assegnato a specifici hackathon come Organizzatore, Giudice o Mentore |
| **Capo team** | Creatore del team, gestisce le iscrizioni, la registrazione all'evento e gli inviti |
| **Membro del team** | Può iscrivere il proprio team a un hackathon e consegnare un progetto |
| **Mentore** | Membro dello staff che supporta i team e può segnalare violazioni del regolamento |
| **Giudice** | Membro dello staff che valuta le consegne con un punteggio (0–10) e un feedback |
| **Organizzatore** | Membro dello staff che crea gli hackathon e proclama il vincitore |

---

## 📁 Struttura del progetto

```
HackHubWeb/
│
├── LICENSE
├── README.md
├── TODO.txt
│
├── database/
│   └── hackhubdb.mv.db                        # Database H2 (su file)
│
├── hackhub_backend/                           # API REST Spring Boot
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd                         # Maven Wrapper
│   └── src/main/
│       ├── java/it/unicam/cs/hackhub/
│       │   ├── HackhubApplication.java         # Punto di ingresso
│       │   ├── client/                         # Integrazione GitHub (parsing dei repository)
│       │   ├── controller/                     # Endpoint REST
│       │   ├── DTO/                             # Data Transfer Object
│       │   ├── exception/                       # Gestione globale delle eccezioni
│       │   ├── model/                           # Entità JPA (con pattern Builder e Factory)
│       │   ├── repository/                      # Repository Spring Data
│       │   ├── security/                        # Configurazione JWT e Spring Security
│       │   └── service/                         # Logica di business
│       └── resources/
│           └── application.properties
│
└── hackhub_frontend/                           # Applicazione Angular (SPA)
    ├── angular.json
    ├── package.json
    ├── tsconfig*.json
    └── src/
        ├── main.ts
        ├── index.html
        ├── styles.scss
        ├── app/
        │   ├── app.config.ts                    # Configurazione dell'applicazione
        │   ├── app.routes.ts                    # Definizione delle rotte
        │   ├── app.ts                           # Componente radice
        │   ├── core/
        │   │   ├── guard/                        # Route guard (auth, role, team)
        │   │   └── interceptors/                # HTTP interceptor (jwt, error)
        │   └── features/                         # Moduli funzionali
        │       ├── account/                      # Profilo utente e inviti
        │       ├── auth/                          # Login e registrazione
        │       ├── dashboard/                     # Home / dashboard
        │       ├── hackathon/                     # Gestione hackathon (creazione, lista, modifica, dettaglio)
        │       ├── report/                        # Segnalazioni
        │       ├── submissions/                   # Consegne dei progetti
        │       ├── team/                          # Gestione dei team
        │       └── users/                         # Profilo utente
        └── environments/
            ├── environment.ts
            └── environment.prod.ts
```

> Ogni feature segue la stessa convenzione interna: `components/` (i componenti), `model/` (i modelli TypeScript) e `service/` (i servizi verso l'API).

---

## 🚀 Come iniziare

### Prerequisiti

- **Java** 21+
- **Maven** 3.8+
- **Node.js** 20+ (richiesto da Angular 21)
- **npm** 10+
- **Angular CLI** 21+

---

### Avvio del backend

```bash
# Spostarsi nella cartella del backend
cd hackhub_backend

# Compilare il progetto
.\mvnw clean install

# Avviare l'applicazione
.\mvnw spring-boot:run
```

Il backend si avvia su: `http://localhost:8080`

> Le credenziali del datasource sono configurate in `src/main/resources/application.properties`.

---

### Avvio del frontend

```bash
# Spostarsi nella cartella del frontend
cd hackhub_frontend

# Installare le dipendenze
npm install

# Avviare il server di sviluppo
ng serve --open
```

L'applicazione sarà disponibile su: `http://localhost:4200`

#### Build di produzione

```bash
# Spostarsi nella cartella del frontend
cd hackhub_frontend

# Compilare
ng build

# Servire la build di produzione
ng serve --configuration=production
```

---

## 🔐 Sicurezza

### Autenticazione JWT

Al login, il backend emette un JWT firmato che contiene l'identità dell'utente e il suo ruolo come claim personalizzato. Il token viene allegato a ogni successiva richiesta HTTP nell'header `Authorization` (`Bearer <token>`), abilitando un'autenticazione stateless senza gestione delle sessioni lato server.

### Controllo degli accessi basato sui ruoli (RBAC)

Ogni endpoint REST è protetto da annotazioni di Spring Security che definiscono quali ruoli sono autorizzati ad accedervi:

- `USER` — funzionalità di base per team e partecipazione
- `STAFF` — gestione degli hackathon per gli eventi assegnati
- `ADMIN` — accesso completo alla piattaforma

### Protezione del frontend

Le route guard di Angular impediscono la navigazione verso pagine riservate agli utenti non autenticati o con ruoli insufficienti. L'autorizzazione lato backend resta però il livello di sicurezza autoritativo: l'API respinge ogni richiesta con token non valido o permessi insufficienti con `401 Unauthorized` o `403 Forbidden`.

---

## 📝 Licenza

Questo progetto è rilasciato sotto [Licenza MIT](LICENSE).

---

## 👨‍💻 Autori

- **Maurizio Crescenzi**: [@nomeutentegithub](link github)
- **Luca Gasparretti**: [@nomeutentegithub](link github)

Sviluppato come progetto universitario presso l'**Università di Camerino (UNICAM)**.

---

> HackHub — Dove le idee competono.