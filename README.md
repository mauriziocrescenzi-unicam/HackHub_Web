# HackHub 🚀

Una piattaforma web full-stack per la gestione dell'intero ciclo di vita degli hackathon: dalla registrazione dei team fino alla proclamazione dei vincitori.

## 📖 Panoramica del Progetto

HackHub è un'applicazione web progettata per digitalizzare completamente la gestione degli hackathon: competizioni a tempo in cui i team sviluppano e consegnano un progetto per raggiungere un obiettivo comune. La piattaforma centralizza ogni fase dell'evento, dalla registrazione dei partecipanti alla proclamazione dei vincitori.

Realizzata con un backend in **Java Spring Boot** e un frontend in **Angular 21**, HackHub offre un ambiente basato sui ruoli in cui gli organizzatori possono creare e gestire gli eventi, i team possono registrarsi e inviare i propri progetti, i giudici possono valutare le submission e i mentor possono supportare i partecipanti durante tutta la competizione.

## ✨ Funzionalità

- 🏆 Creazione e gestione di hackathon con un ciclo di vita completo a 4 stati.
- 👥 Creazione di team, gestione degli inviti e dei membri.
- 📁 Invio dei progetti tramite link a repository GitHub, aggiornabile fino alla scadenza.
- ⭐ Sistema di valutazione dei giudici con punteggio numerico (0–10) e feedback testuale.
- 🚩 Segnalazione di team da parte dei mentor per violazioni del regolamento.
- 🔒 Controllo degli accessi basato sui ruoli (Visitatore, Utente, Staff, Admin).
- 🔐 Autenticazione basata su JWT con claims per i ruoli.
- 🛡️ Route guards di Angular per la protezione delle rotte frontend.
- 🔍 Filtri avanzati, ordinamento e paginazione per la scoperta di hackathon e team.

## 🛠️ Stack Tecnologico

### Backend

| Tecnologia | Dettagli |
| :--- | :--- |
| **Java** | 21 |
| **Spring Boot** | 4 |
| **Spring Web** | REST API con `@RestController` |
| **Spring Security** | Autenticazione JWT + RBAC |
| **Spring Data JPA** | Persistenza basata su repository (Hibernate) |
| **H2 Database** | Database relazionale in memoria |
| **Apache Maven** | Strumento di build e gestione delle dipendenze |

### Frontend

| Tecnologia | Dettagli |
| :--- | :--- |
| **Angular** | 21 |
| **TypeScript** | ~5.9.2 |
| **RxJS** | ~7.8.0 (Programmazione reattiva) |
| **SCSS** | Stili avanzati con variabili, mixin e nesting |

## 👤 Ruoli e Attori

| Ruolo | Descrizione |
| :--- | :--- |
| **Visitatore** | Utente non autenticato, può navigare le informazioni pubbliche degli hackathon. |
| **Utente** | Utente registrato, può creare o unirsi a un team. |
| **Staff** | Personale assegnato a specifici hackathon come Organizzatore, Giudice o Mentor. |
| **Team Leader** | Creatore del team che gestisce i membri, le iscrizioni agli eventi e gli inviti. |
| **Membro del Team** | Può registrare il proprio team a un hackathon e inviare un progetto. |
| **Mentor** | Membro dello staff che supporta i team e può segnalare violazioni del regolamento. |
| **Giudice** | Membro dello staff che valuta le submission assegnando un punteggio (0–10) e un feedback. |
| **Organizzatore** | Membro dello staff che crea gli hackathon e proclama il vincitore. |

## 📁 Struttura del Progetto

```text
HackHubWeb/
 │
 ├── hackhub_backend/                          # REST API Spring Boot
 │   ├── pom.xml
 │   └── src/main/java/it/unicam/cs/hackhub/
 │       ├── HackhubApplication.java           # Entry point
 │       ├── client/                           # Integrazioni esterne (es. GitHub)
 │       ├── controller/                       # Endpoint REST
 │       ├── exception/                        # Gestione globale delle eccezioni
 │       ├── DTO/                              # Data Transfer Objects
 │       ├── model/                            # Entità JPA
 │       ├── repository/                       # Repository Spring Data
 │       ├── service/                          # Logica di business
 │       └── security/                         # Configurazione JWT e Spring Security
 │
 └── hackhub_frontend/                         # Applicazione Angular
     ├── package.json
     └── src/
         ├── app/
         │   ├── core/                         # Modulo core
         │   │   ├── guard/                    # Route guards (auth, role, team)
         │   │   └── interceptors/             # Intercettori HTTP (JWT, Errori)
         │   │
         │   └── features/                     # Moduli funzionali (Feature Modules)
         │      ├── account/                   # Gestione profilo e inviti
         │      ├── auth/                      # Login e Registrazione
         │      ├── dashboard/                 # Dashboard e Home
         │      ├── hackathon/                 # Gestione hackathon (CRUD, lista)
         │      ├── report/                    # Gestione segnalazioni
         │      ├── submissions/               # Gestione submission dei team
         │      ├── team/                      # Gestione team e membri
         │      └── users/                     # Visualizzazione utenti
         ├── environments/                     # Configurazione ambiente (dev)
         ├── index.html
         ├── main.ts                           # Bootstrap dell'applicazione
         └── styles.scss                       # Stili globali
```

## 🚀 Come Iniziare

### Prerequisiti

- Java 21+
- Maven 3.8+
- Node.js 18+
- npm 10+
- Angular CLI 21+

### Setup Backend

```bash
# Naviga nella cartella del backend
cd hackhub_backend

# Compila il progetto
./mvnw clean install   # Su Windows: .\mvnw.cmd clean install

# Avvia l'applicazione
./mvnw spring-boot:run # Su Windows: .\mvnw.cmd spring-boot:run
```

Il backend sarà disponibile all'indirizzo: `http://localhost:8080`

Le credenziali predefinite per il datasource sono configurate in `src/main/resources/application.properties`.

### Setup Frontend

```bash
# Naviga nella cartella del frontend
cd hackhub_frontend

# Installa le dipendenze
npm install

# Avvia il server di sviluppo
ng serve --open
```

L'applicazione sarà disponibile all'indirizzo: `http://localhost:4200`

### Build di Produzione

```bash
# Naviga nella cartella del frontend
cd hackhub_frontend

# Genera la build di produzione
ng build
```

I file compilati e ottimizzati per la produzione verranno generati nella cartella `dist/`.

## 🔐 Sicurezza

### Autenticazione JWT

Al login, il backend genera un JWT firmato contenente l'identità dell'utente e il suo ruolo come *claim* personalizzato. Questo token viene allegato a ogni successiva richiesta HTTP nell'header `Authorization` (`Bearer <token>`), consentendo un'autenticazione *stateless* senza la gestione di sessioni lato server.

### Controllo Accessi Basato sui Ruoli (RBAC)

Ogni endpoint REST è protetto da annotazioni di Spring Security che definiscono quali ruoli sono autorizzati ad accedervi:

- `USER` — Funzionalità base per team e partecipazione.
- `STAFF` — Gestione degli hackathon per gli eventi assegnati.
- `ADMIN` — Accesso completo alla piattaforma.

### Protezione Frontend

I *Route Guards* di Angular prevengono la navigazione verso pagine riservate per utenti non autenticati o con ruoli insufficienti. L'autorizzazione lato backend rimane il livello di sicurezza autorevole: l'API rifiuta qualsiasi richiesta con un token non valido o permessi insufficienti restituendo `401 Unauthorized` o `403 Forbidden`.

## 📝 Licenza

Questo progetto è distribuito sotto licenza [MIT License](LICENSE).

## 👨‍💻 Autori

- **[Maurizio Crescenzi]**: [@mauriziocrescenzi-unicam](https://github.com/mauriziocrescenzi-unicam)
- **[Luca Gasparretti]**: [@lucagas-lab](https://github.com/lucagas-lab)

Progetto sviluppato nell'ambito del corso di studi presso l'**Università di Camerino (UNICAM)**.