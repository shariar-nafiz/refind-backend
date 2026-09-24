# 🧭 ReFind — Master Project Architecture, Status Audit & Implementation Roadmap

> **Academic & Production Blueprint**  
> **Project:** LOST & FOUND (ReFind) — A Lightweight Recovery Platform for Reuniting People with Lost Belongings  
> **Institution:** Southeast University, Department of Computer Science & Engineering  
> **Prepared by:** Md Shariar (Project Manager & Lead Software Engineer)  
> **Project Team:**  
> - Md Shariar (ID: 2024100010037)  
> - Md. Rahat Hossain (ID: 2023000010078)  
> - Meherunnesa Shohagi (ID: 2024100010043)  
> - Hamidul Hoque Masum (ID: 2024000010005)  
> - Arop Sutra Dhar (ID: 2024100010161)  
> **Project Supervisor:** Mr. Miftahul Sheikh (Adjunct Lecturer, Dept. of CSE, Southeast University)  
> **Date:** September 2026 | **Version:** 2.0.0-PROPOSAL-ALIGNED  

---

## 📌 Executive Summary

Every single day, thousands of students, commuters, and citizens misplace essential valuables — national identity cards, university student IDs, smartphones, wallets, keys, and backpacks — across university campuses, transit hubs, and public spaces. The prevailing recovery methods rely on unindexed, ephemeral social media posts (Facebook groups, WhatsApp messages) or chaotic word-of-mouth. These existing approaches suffer from:
1. **Low Discoverability:** Posts drown in social media feeds within hours.
2. **Severe Privacy & Safety Risks:** Posters expose personal phone numbers and locations publicly.
3. **Opportunistic Fraud:** Fraudsters claim valuable items without proving ownership.
4. **No Matching Automation:** No algorithmic bridge exists between lost reports and found listings.

**ReFind** bridges this critical gap with a high-performance, lightweight, privacy-first recovery platform. It replaces unstructured search with a disciplined 4-stage pipeline:
$$\text{Report} \longrightarrow \text{Smart Match} \longrightarrow \text{Secret Verify} \longrightarrow \text{Secure Reunite}$$

This document presents the **full software architecture audit**, analyzing the initial project proposal against the current codebase status and Flyway database migrations. It details completed modules, uncompleted modules, precise database schemas for upcoming migrations (`V4` through `V8`), concrete API specifications, and high-impact feature innovations.

---

## 🏗️ High-Level System Architecture

```
                                  +-------------------------------------------------------+
                                  |                    CLIENT LAYER                       |
                                  |  (Flutter Mobile / PWA / Responsive Web Frontend)     |
                                  +---------------------------+---------------------------+
                                                              | HTTPS / REST / SSE / WSS
                                                              v
+-------------------------------------------------------------------------------------------------------------------------+
|                                              SPRING BOOT 4.x BACKEND (JAVA 21)                                          |
|                                                                                                                         |
|  +---------------------+  +---------------------+  +------------------------+  +-------------------------------------+  |
|  |   Security & Auth   |  |   User & Profile    |  |     Media Storage      |  |      Category & Location Hub        |  |
|  | JWT / Redis Session |  | Addresses / Prefs   |  | Local / Cloud Storage  |  | BD 64 Districts & Thana Dropdowns  |  |
|  +---------------------+  +---------------------+  +------------------------+  +-------------------------------------+  |
|                                                                                                                         |
|  +---------------------------------------+  +---------------------------------------+  +-----------------------------+  |
|  |          Item Submission Hub          |  |         Smart Matching Engine         |  |   Claim & Approval Engine   |  |
|  |  Lost & Found / Secret Identifier     |  |    Weighted Multi-Factor Scoring      |  | Zero-Knowledge Proof / OTP  |  |
|  +---------------------------------------+  +---------------------------------------+  +-----------------------------+  |
|                                                                                                                         |
|  +-------------------------------------------------------+  +--------------------------------------------------------+  |
|  |                 Notification Pipeline                 |  |               Admin Moderation & Analytics             |  |
|  |        In-App (DB) + Email (Resend) + Push (FCM)      |  |            Platform Metrics / Content Review           |  |
|  +-------------------------------------------------------+  +--------------------------------------------------------+  |
+---------------------------------------------+---------------------------------------+-----------------------------------+
                                              |                                       |
                                              v                                       v
                     +----------------------------------+          +----------------------------------+
                     |    PostgreSQL (Relational DB)    |          |     Redis (Upstash / Cluster)    |
                     |  Users, Items, Claims, Matches   |          | Token Blacklist / Session State  |
                     +----------------------------------+          +----------------------------------+
```

---

## 📊 Comprehensive Status Matrix: Completed vs. Not Completed

The following table provides an exhaustive assessment of the 7 core feature modules and unique innovations defined in the Southeast University proposal report:

| Module / Capability | Proposal Spec | Current Codebase Status | Completed Components | Missing / Pending Components |
| :--- | :--- | :--- | :--- | :--- |
| **1. User Management & Auth** | Email/phone auth, profile dashboard, claim & post counters | **85% Completed** | • BCrypt JWT Auth (Access + Refresh)<br>• Redis Token Blacklist & Session Revoke<br>• Profile CRUD (`/api/v1/users/me`)<br>• Address Profiles (`user_addresses`)<br>• User Privacy & Alerts Preferences (`user_preferences`)<br>• Onboarding Wizard (`/me/setup`)<br>• Public Profile Masking (`/{id}/public`) | • Email/SMS OTP Verification<br>• Password Reset token flow<br>• Dynamic aggregation of Lost/Found/Reunited counts in Dashboard |
| **2. Media & Storage** | Photos for items, proof documents, profile avatars | **80% Completed** | • Local filesystem storage engine<br>• Strict MIME & size validation (JPEG, PNG, WebP)<br>• Media entity tracking (`media` table - V2)<br>• Media upload endpoint (`/api/v1/media/upload`) | • Item-to-Media association table (`item_media`)<br>• Automated thumbnail generation & WebP compression<br>• AWS S3 / Cloudflare R2 cloud driver fallback |
| **3. Category & Administrative Location** | Dropdown-driven structure (Districts/Thanas), categorized items | **0% Completed** | • Concept and ERD defined in proposal | • Flyway `V4__create_categories_and_locations_tables.sql`<br>• Pre-seeded 64 Bangladesh districts & thanas<br>• Category CRUD & Public Dropdown APIs<br>• Location Cascading Selectors API |
| **4. Item Submission Hub** | Lost & Found forms, tags, dates, secret identifier detail | **0% Completed** | • Concept and ERD defined in proposal | • Flyway `V5__create_items_and_item_media_tables.sql`<br>• Item Entities & Repositories<br>• Secret Identifier protection logic<br>• Public Item Search & Filter APIs<br>• User Item Management (Edit, Close, Archive) |
| **5. Smart Matching Engine** | Point-based ranking (50% Cat, 20% Loc, 15% Date, 15% Tag) | **0% Completed** | • Scoring model formulated in proposal | • Flyway `V7__create_matching_engine_tables.sql`<br>• Algorithmic Scoring Engine Service<br>• Event-driven async matching listener<br>• User Match Feed (`/api/v1/items/{id}/matches`) |
| **6. Claim & Approval Workflow** | Submit claim with proof, finder review, approve/reject, contact unlock | **0% Completed** | • Privacy-gated contact unlock concept | • Flyway `V6__create_claims_and_claims_workflow_tables.sql`<br>• Claim submission with Secret Identifier Guess<br>• Anti-brute force claim rate limiter<br>• Finder Approval/Rejection Handshake<br>• Privacy-gated Contact Release trigger |
| **7. Notification Engine** | In-app, Email (Resend), Push (FCM) alerts for matches & claims | **15% Completed** | • Dependencies in `build.gradle.kts`<br>• Configuration keys in `application.yml` | • Flyway `V8__create_notifications_tables.sql`<br>• In-App Notifications API (`/api/v1/notifications`)<br>• Resend transactional email dispatcher<br>• FCM mobile push delivery service |
| **8. Admin Panel & Moderation** | Category/Location CRUD, Item/User moderation, System stats | **30% Completed** | • Admin User Management (`/api/v1/admin/users`: search, filter, status toggle, role change) | • Admin Category & Location management<br>• Admin Item moderation & spam removal<br>• Claim dispute arbitration<br>• Platform-wide metrics dashboard |

---

## 🗄️ Flyway Database Migration Roadmap

The current migration directory contains:
- `V1__create_users_table.sql`
- `V2__create_media_table.sql`
- `V3__create_user_addresses_and_preferences.sql`

To complete the full system as specified in the proposal and architectural blueprint, the following migrations are scheduled:

```
src/main/resources/db/migration/
├── V1__create_users_table.sql                         (COMPLETED)
├── V2__create_media_table.sql                         (COMPLETED)
├── V3__create_user_addresses_and_preferences.sql     (COMPLETED)
├── V4__create_categories_and_locations_tables.sql     (NEXT UP)
├── V5__create_items_and_item_media_tables.sql         (NEXT UP)
├── V6__create_claims_and_claims_workflow_tables.sql   (NEXT UP)
├── V7__create_matching_engine_tables.sql             (NEXT UP)
└── V8__create_notifications_tables.sql                (NEXT UP)
```

### Detailed Schema Specifications for Planned Migrations

#### `V4__create_categories_and_locations_tables.sql`
```sql
-- Categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    icon_name VARCHAR(50),
    description VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Administrative Locations (Bangladesh Districts & Thanas)
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    division VARCHAR(50) NOT NULL,
    district VARCHAR(100) NOT NULL,
    thana VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_location_district_thana UNIQUE(district, thana)
);

CREATE INDEX idx_locations_district ON locations(district);
CREATE INDEX idx_locations_thana ON locations(thana);

-- Seed Categories
INSERT INTO categories (name, slug, icon_name, display_order) VALUES
('Wallets & Purses', 'wallets-purses', 'wallet', 1),
('National IDs & Documents', 'ids-documents', 'id-card', 2),
('Mobile Phones & Tablets', 'phones-tablets', 'smartphone', 3),
('Laptops & Electronics', 'laptops-electronics', 'laptop', 4),
('Bags & Backpacks', 'bags-backpacks', 'briefcase', 5),
('Keys & Keychains', 'keys-keychains', 'key', 6),
('Jewelry & Watches', 'jewelry-watches', 'watch', 7),
('Books & Study Materials', 'books-study', 'book', 8),
('Clothing & Accessories', 'clothing-accessories', 'shirt', 9),
('Other Valuables', 'other-valuables', 'help-circle', 10);
```

#### `V5__create_items_and_item_media_tables.sql`
```sql
CREATE TYPE item_type AS ENUM ('LOST', 'FOUND');
CREATE TYPE item_status AS ENUM ('OPEN', 'CLAIM_PENDING', 'RESOLVED', 'EXPIRED', 'ARCHIVED');

CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    location_id BIGINT NOT NULL REFERENCES locations(id),
    type VARCHAR(20) NOT NULL, -- 'LOST' or 'FOUND'
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    specific_location_hint VARCHAR(255), -- e.g. 'SEU Library 4th Floor', 'Mirpur 10 Bus Stand'
    incident_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Novel Feature: Secret Identifier (Hidden Verification Detail)
    secret_identifier_question VARCHAR(255), -- e.g. "What is the laptop wallpaper or sticker?"
    secret_identifier_answer VARCHAR(255),   -- Stored securely or hashed; verified during claim
    
    tags TEXT[], -- Array of descriptive keywords e.g. ['black', 'leather', 'samsung', 'blue case']
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    view_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item_media (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    media_id BIGINT NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_items_user_id ON items(user_id);
CREATE INDEX idx_items_type_status ON items(type, status);
CREATE INDEX idx_items_category_id ON items(category_id);
CREATE INDEX idx_items_location_id ON items(location_id);
CREATE INDEX idx_items_incident_date ON items(incident_date_time);
CREATE INDEX idx_items_tags ON items USING GIN(tags);
```

#### `V6__create_claims_and_claims_workflow_tables.sql`
```sql
CREATE TABLE claims (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    claimant_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    proof_description TEXT NOT NULL,
    secret_identifier_submission VARCHAR(255), -- Claimant's answer to secret identifier question
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, CANCELLED
    finder_response_notes TEXT,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    contact_unlocked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE claim_media (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    media_id BIGINT NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item_handovers (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL UNIQUE REFERENCES claims(id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    verification_code VARCHAR(10) NOT NULL, -- 4-digit handover OTP
    poster_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    claimant_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    feedback_rating INT CHECK (feedback_rating BETWEEN 1 AND 5),
    feedback_notes TEXT,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_claims_item_id ON claims(item_id);
CREATE INDEX idx_claims_claimant_id ON claims(claimant_id);
CREATE INDEX idx_claims_status ON claims(status);
```

#### `V7__create_matching_engine_tables.sql`
```sql
CREATE TABLE item_matches (
    id BIGSERIAL PRIMARY KEY,
    lost_item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    found_item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    total_score INT NOT NULL, -- 0 to 100
    category_score INT NOT NULL, -- Max 50
    location_score INT NOT NULL, -- Max 20
    date_score INT NOT NULL,     -- Max 15
    tag_score INT NOT NULL,      -- Max 15
    is_dismissed_by_lost_user BOOLEAN NOT NULL DEFAULT FALSE,
    is_dismissed_by_found_user BOOLEAN NOT NULL DEFAULT FALSE,
    is_notified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_lost_found_match UNIQUE(lost_item_id, found_item_id)
);

CREATE INDEX idx_matches_lost_item ON item_matches(lost_item_id, total_score DESC);
CREATE INDEX idx_matches_found_item ON item_matches(found_item_id, total_score DESC);
```

#### `V8__create_notifications_tables.sql`
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- MATCH_ALERT, CLAIM_SUBMITTED, CLAIM_APPROVED, CLAIM_REJECTED, HANDOVER_COMPLETE
    reference_id BIGINT,       -- item_id or claim_id
    reference_type VARCHAR(50),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_device_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fcm_token VARCHAR(500) NOT NULL UNIQUE,
    device_type VARCHAR(50) DEFAULT 'WEB', -- ANDROID, IOS, WEB
    last_used_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id, is_read, created_at DESC);
```

---

## 🎯 Detailed Feature Innovations & Deep Architectural Concepts

### 1. Smart Weighted Point-Based Matching Engine
Instead of conventional keyword search that either yields 0 results or floods users with irrelevant matches, ReFind employs an automated, point-based scoring engine.

#### Algorithmic Formulation
Whenever a new `LOST` or `FOUND` item is reported, an asynchronous Spring Event is emitted (`ItemCreatedEvent`). The matching engine computes similarity against all reciprocal active items:

$$\text{Total Match Score} = S_{\text{Category}} + S_{\text{Location}} + S_{\text{Date}} + S_{\text{Tags}}$$

$$\text{Where: } S_{\text{Total}} \in [0, 100]$$

| Dimension | Max Points | Weight % | Mathematical / Evaluation Criteria |
| :--- | :--- | :--- | :--- |
| **Category Match ($S_{\text{Category}}$)** | **50 Pts** | 50% | • Exact Category ID match = **50 pts**<br>• Different category = **0 pts** (hard filter threshold) |
| **Location Match ($S_{\text{Location}}$)** | **20 Pts** | 20% | • Exact District + Same Thana = **20 pts**<br>• Same District, Different Thana = **12 pts**<br>• Adjacent/Bordering District = **5 pts**<br>• Unrelated District = **0 pts** |
| **Temporal Proximity ($S_{\text{Date}}$)** | **15 Pts** | 15% | • Found Date $\ge$ Lost Date within 48 hours = **15 pts**<br>• Proximity decay over $\Delta t$ days: $S_{\text{Date}} = \max\left(0, 15 \times e^{-\frac{\Delta t}{14}}\right)$<br>• Found date *preceding* lost date by $>3$ days = **0 pts** |
| **Tag & Keyword Overlap ($S_{\text{Tags}}$)** | **15 Pts** | 15% | • Jaccard Token Similarity over tags + title trigrams:<br>$S_{\text{Tags}} = 15 \times \frac{|T_{\text{Lost}} \cap T_{\text{Found}}|}{|T_{\text{Lost}} \cup T_{\text{Found}}|}$ |

#### Match Action Tiers:
- **$\ge 75\%$ High Confidence:** Trigger instant Push Notification + Priority Match Alert Email via Resend. Display at top of dashboard with green badge.
- **$50\% - 74\%$ Probable Match:** Display in the user's "Suggested Matches" tab. Include in daily digest.
- **$< 50\%$ Low Confidence:** Filtered out to avoid notification fatigue.

---

### 2. Zero-Knowledge Secret Identifier Verification Protocol
To eliminate false claimants and opportunistic scams, ReFind implements a **Secret Identifier Handshake**:

```
 [ Finder posts FOUND item ]
              |
              v
 Records a hidden detail only true owner knows:
 e.g., "Serial number last 4 digits", "Engraving inside ring", "Family photo in wallet"
              |
              v
 [ Item is publicly listed ]
 (Photo is shown, but Secret Identifier Answer is NEVER shown)
              |
              v
 [ Claimant submits CLAIM ]
 Claimant must provide:
 1. Description of the secret identifier
 2. Proof media (e.g. old purchase receipt, ID photocopy, or picture with the item)
              |
              v
 [ Finder reviews Claim ]
 Finder compares claimant answer with their recorded secret detail
              |
         +----+----+
         |         |
      Matches   Mismatch
         |         |
         v         v
     [APPROVE]  [REJECT]
```

#### Anti-Abuse & Brute Force Prevention:
- Maximum **3 claim attempts per user per item**.
- Attempt cooldown: 12-hour timeout after 2 incorrect claims.
- Captcha / reCAPTCHA integration on claim submission to eliminate automated bot scraping.

---

### 3. Approval-Gated Contact Privacy Shield
In public lost-and-found boards, personal phone numbers and emails are published openly, leading to harassment, spam, and stalking. ReFind guarantees **zero contact disclosure** until trust is established:

1. **Default State:** The item poster's phone and email are completely redacted from public view.
2. **Review Stage:** Communications occur strictly through the platform's structured claim submission interface.
3. **Approval Handshake:** Once the poster clicks **"Approve Claim"**:
   - The platform checks both users' `user_preferences` (`show_phone_on_claim_approved`, `show_email_on_claim_approved`, `preferred_contact_method`).
   - The verified contact information is disclosed **exclusively** to the approved claimant and finder.
   - An interactive Handover Card is unlocked with a **4-digit Handover PIN**.

---

### 4. Physical Handover Confirmation & Trust Badges
When the two parties meet in person (e.g., SEU Campus Security, police station, coffee shop):
1. The claimant presents the 4-digit Handover PIN generated in their app.
2. The finder enters the PIN in their ReFind dashboard.
3. The system atomically marks the item as `RESOLVED`.
4. Both users are awarded **Civic Karma Points**:
   - Finder receives: **"Good Samaritan" Badge** (+50 Karma).
   - Claimant receives: **"Reunited Owner" Badge** (+20 Karma).
   - The user's public profile counter `totalItemsReunited` increments automatically.

---

### 5. Structured Bangladesh Administrative Location Hierarchy
To ensure high usability without relying on complex, battery-draining Google Maps GPS APIs:
- Pre-populated cascading dropdown: **Division ➔ District (64 Districts) ➔ Upazila/Thana (495+ Thanas)**.
- Specific Campus & Landmark Anchors: Supports tagging high-frequency recovery zones (e.g. *Southeast University Campus*, *Dhaka Metro Rail Stations*, *Shahbagh*, *Mirpur 10*).
- Form-driven UX reduces friction for users reporting lost or found items on slow mobile data connections.

---

## 🗺️ Execution Roadmap & Sprint Breakdown

```mermaid
gantt
    title ReFind Backend Engineering Roadmap
    dateFormat  YYYY-MM-DD
    section Sprint 1: Identity & Core
    Auth & Token Blacklist (V1)           :done, s1a, 2026-09-01, 2026-09-07
    Media Upload & Storage (V2)          :done, s1b, 2026-09-07, 2026-09-12
    User Profiles, Address, Prefs (V3)   :done, s1c, 2026-09-12, 2026-09-20
    section Sprint 2: Core Domain
    Migration V4: Categories & Locations :active, s2a, 2026-09-23, 2026-09-25
    Category & Location REST APIs        :s2b, 2026-09-25, 2026-09-27
    Migration V5: Items & Item Media     :s2c, 2026-09-27, 2026-09-30
    Item Submission & Search APIs        :s2d, 2026-09-30, 2026-10-04
    section Sprint 3: Intelligence
    Migration V7: Item Matches Schema    :s3a, 2026-10-04, 2026-10-06
    Weighted Matching Algorithm Engine   :s3b, 2026-10-06, 2026-10-10
    Matches REST API & Auto-Trigger      :s3c, 2026-10-10, 2026-10-13
    section Sprint 4: Verification
    Migration V6: Claims & Handover      :s4a, 2026-10-13, 2026-10-16
    Secret Identifier Verification Flow  :s4b, 2026-10-16, 2026-10-20
    Approval-Gated Contact Disclosure    :s4c, 2026-10-20, 2026-10-23
    Handover OTP & Karma Badges          :s4d, 2026-10-23, 2026-10-26
    section Sprint 5: Notifications
    Migration V8: Notifications Table    :s5a, 2026-10-26, 2026-10-28
    In-App Notifications Hub             :s5b, 2026-10-28, 2026-10-31
    Resend Email Transactional Templates :s5c, 2026-10-31, 2026-11-03
    Firebase Cloud Messaging Push        :s5d, 2026-11-03, 2026-11-06
    section Sprint 6: Admin & Polish
    Admin Moderation & Analytics Hub     :s6a, 2026-11-06, 2026-11-10
    Integration Testing & Benchmark      :s6b, 2026-11-10, 2026-11-15
    University Demo & Defense Prep       :s6c, 2026-11-15, 2026-11-20
```

---

## 🔌 API Endpoint Inventory (Existing vs. Scheduled)

### 🟢 Completed & Functional Endpoints (Sprint 1)
- `POST /api/v1/auth/register` — Register new user account
- `POST /api/v1/auth/login` — Login with email/phone & password
- `POST /api/v1/auth/refresh` — Refresh access token using refresh token
- `POST /api/v1/auth/logout` — Blacklist current token in Redis
- `GET /api/v1/users/me` — Get current user profile
- `PUT /api/v1/users/me` — Update full name, bio, secondary phone
- `POST /api/v1/users/me/setup` — Initial onboarding setup
- `GET /api/v1/users/me/dashboard` — User dashboard counters & completeness score
- `PATCH /api/v1/users/me/password` — Change account password
- `POST /api/v1/users/me/avatar` — Upload and attach profile avatar
- `GET /api/v1/users/me/addresses` — List user saved locations
- `POST /api/v1/users/me/addresses` — Add home/campus/work address
- `PATCH /api/v1/users/me/addresses/{id}/default` — Set primary address
- `DELETE /api/v1/users/me/addresses/{id}` — Remove address
- `GET /api/v1/users/me/preferences` — Get notification & privacy disclosure settings
- `PUT /api/v1/users/me/preferences` — Update notification & privacy settings
- `POST /api/v1/users/me/deactivate` — Soft deactivate account and terminate sessions
- `GET /api/v1/users/{id}/public` — Get safe public user profile
- `GET /api/v1/admin/users` — Search/filter users (Admin only)
- `GET /api/v1/admin/users/{id}` — Get full user audit profile (Admin only)
- `PATCH /api/v1/admin/users/{id}/status` — Update account status (ACTIVE, BLOCKED, etc.)
- `PATCH /api/v1/admin/users/{id}/role` — Update role (ROLE_USER, ROLE_ADMIN)
- `POST /api/v1/media/upload` — Upload multipart media file (avatars/items)

### 🟡 Scheduled Endpoints (Sprint 2 through Sprint 6)

#### Categories & Locations (Sprint 2)
- `GET /api/v1/categories` — Public list of all active item categories
- `GET /api/v1/locations/districts` — Public list of 64 districts
- `GET /api/v1/locations/thanas?district={name}` — Public cascading thanas for selected district
- `POST /api/v1/admin/categories` — Admin create/update category
- `POST /api/v1/admin/locations` — Admin add location/campus anchor

#### Items Module (Sprint 2)
- `POST /api/v1/items` — Submit Lost or Found item (with secret identifier)
- `GET /api/v1/items` — Search & filter items (by query, type, category, district, thana, date range)
- `GET /api/v1/items/{id}` — Get item details (masks contact & secret identifier)
- `PUT /api/v1/items/{id}` — Update item details (owner only)
- `DELETE /api/v1/items/{id}` — Cancel or archive item (owner only)
- `GET /api/v1/items/me` — List current user's submitted items

#### Smart Matching Engine (Sprint 3)
- `GET /api/v1/items/{id}/matches` — Get ranked matched candidates for an item
- `POST /api/v1/items/matches/{matchId}/dismiss` — Dismiss a false positive match

#### Claims & Handover Workflow (Sprint 4)
- `POST /api/v1/items/{id}/claims` — Submit claim with proof description & secret answer
- `GET /api/v1/items/{id}/claims` — Item poster view of submitted claims
- `GET /api/v1/claims/me` — Current user's submitted claims
- `PATCH /api/v1/claims/{claimId}/approve` — Finder approves claim (triggers contact unlock)
- `PATCH /api/v1/claims/{claimId}/reject` — Finder rejects claim
- `POST /api/v1/claims/{claimId}/handover/verify` — Submit 4-digit PIN to confirm physical reunion
- `POST /api/v1/claims/{claimId}/handover/feedback` — Submit rating and testimonial

#### Notifications (Sprint 5)
- `GET /api/v1/notifications` — List user notifications (paginated)
- `PATCH /api/v1/notifications/{id}/read` — Mark notification as read
- `PATCH /api/v1/notifications/read-all` — Mark all notifications as read
- `POST /api/v1/notifications/devices` — Register FCM device push token

#### Admin Analytics & Dispute Moderation (Sprint 6)
- `GET /api/v1/admin/analytics/overview` — System KPIs (total lost, found, reunited %, recovery time)
- `PATCH /api/v1/admin/items/{id}/status` — Force update item status / remove inappropriate content
- `GET /api/v1/admin/claims/disputed` — Review disputed claims for manual arbitration

---

## 🔒 Security, Privacy & Integrity Architecture

1. **Zero Contact Exposure:** Unauthenticated scrapers and opportunistic users cannot scrape emails or phone numbers. Contact details are guarded in the database and only disclosed to authenticated users upon affirmative claim approval.
2. **Encrypted Credentials & Stateless Tokens:** Passwords hashed with BCrypt (strength 12). Short-lived JWT access tokens backed by Upstash Redis token blacklisting for instant revocation upon logout or admin ban.
3. **Role-Based Access Control (RBAC):** Clean segregation between `ROLE_USER` and `ROLE_ADMIN` with Spring Security method security (`@PreAuthorize("hasRole('ADMIN')")`).
4. **Rate Limiting & Anti-Scraping:** Upstash Redis token bucket filter to limit submission of claims and searches, mitigating DoS attacks.
5. **Data Protection Compliance:** Soft-deletion and account deactivation options allowing users to withdraw their posted items and addresses anytime.

---

## 🎓 Academic Defense & Project Presentation Guidelines

For the academic presentation and project defense with **Mr. Miftahul Sheikh**:
1. **Highlight the Distinction:** Contrast ReFind against plain classified ads (e.g. Bikroy) or Facebook posts:
   - Automated point-based matching vs. manual keyword searching.
   - Secret Identifier challenge vs. blind trust.
   - Privacy-gated contact unlock vs. exposing personal numbers to scammers.
2. **Live Demonstration Flow:**
   - *Step 1:* Student A logs in and reports a "Found Laptop" in "Dhanmondi, Dhaka" with a secret identifier question: *"What color is the sticker on the back?"*
   - *Step 2:* Student B logs in and reports a "Lost Laptop" in "Dhanmondi, Dhaka".
   - *Step 3:* Demonstrate the Smart Matching Engine calculating an 85% match instantly and alerting Student B.
   - *Step 4:* Student B submits a claim with the correct sticker color. Student A reviews and approves the claim.
   - *Step 5:* Show the contacts unlocking dynamically and enter the 4-digit Handover PIN to mark the item as Reunited!
3. **Architecture Strengths:** Java 21 modern patterns, Spring Boot 4.x, PostgreSQL relational integrity, Flyway version-controlled migrations, Upstash Redis caching, and OpenAPI Swagger documentation.

---
*Document maintained by the ReFind Engineering & Architecture Team.*
