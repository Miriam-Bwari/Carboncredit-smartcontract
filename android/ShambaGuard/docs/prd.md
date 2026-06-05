# 🌿 Shamba Guard — Product Requirements Document (PRD)

> *Your farm, watched from space. Protected by code.*

---

| Field | Detail |
|---|---|
| **Product Name** | Shamba Guard |
| **Version** | v1.0 — MVP |
| **Document Status** | 🟢 Active |
| **Theme** | Carbon & Data + Finance & Resilience |
| **Target Market** | Kenya (East Africa) → Globally Scalable |
| **Build Timeline** | 8 Weeks MVP + 2 Weeks Demo Polish |
| **Team Size** | 4 Engineers |
| **Last Updated** | 2026 |

---

## 📋 Table of Contents

1. [Product Vision](#1-product-vision)
2. [The Problem We Are Solving](#2-the-problem-we-are-solving)
3. [User Roles & Personas](#3-user-roles--personas)
4. [System Architecture Overview](#4-system-architecture-overview)
5. [Backend & ML Requirements](#5-backend--ml-requirements)
6. [Blockchain Requirements](#6-blockchain-requirements)
7. [Android Requirements](#7-android-requirements)
8. [Security Requirements](#8-security-requirements)
9. [Notification Strategy](#9-notification-strategy)
10. [API Contract Reference](#10-api-contract-reference)
11. [Non-Functional Requirements](#11-non-functional-requirements)
12. [Zero-Budget Tech Stack](#12-zero-budget-tech-stack)
13. [Sprint Plan](#13-sprint-plan)
14. [MVP Scope vs Post-MVP](#14-mvp-scope-vs-post-mvp)
15. [Business Model Summary](#15-business-model-summary)
16. [Definition of Done](#16-definition-of-done)

---

## 1. Product Vision

### What Is Shamba Guard?

Shamba Guard is a **parametric climate insurance + early drought warning + carbon credit platform** for African smallholder farmers. It uses satellite imagery to:

1. **Predict** drought 14 days in advance and alert farmers via SMS in Kiswahili
2. **Protect** insured farmers by automatically triggering M-Pesa payouts the moment satellite data confirms a drought — no claim form, no assessor, no waiting
3. **Reward** farmers who conserve their land with verified carbon credits paid via M-Pesa

### Why Shamba Guard Exists

When drought hits and crops fail, farmers cut trees to sell charcoal to survive. This destroys soil carbon, degrades land, and makes the next drought worse. It is a rational survival decision that creates an irrational environmental cycle.

The KES 2,000 payout is not just insurance money. **It is the economic alternative to cutting a tree.** When a farmer receives money before she reaches for the axe, the tree stays standing. That tree sequesters carbon. That carbon becomes a verified credit. That credit pays her again.

```
Drought detected (satellite)
        ↓
Early warning SMS → farmer plants drought-resistant crops
        ↓
Drought confirmed → smart contract fires M-Pesa payout (30 seconds)
        ↓
Farmer survives without cutting trees
        ↓
Trees stay standing → soil carbon preserved → NDVI stays high
        ↓
12 months of satellite data → carbon credit minted on blockchain
        ↓
Corporate buyer purchases credit → M-Pesa payout to farmer
```

### The Two-Sentence Pitch

> *"When satellite data confirms drought on Mary's farm in Ukambani, her M-Pesa receives KES 2,400 automatically in 30 seconds. No claim form. No assessor visit. No waiting — because a smart contract does not need office hours."*

---

## 2. The Problem We Are Solving

### 2.1 The Farmer's Reality

| Pain Point | Current Reality | Shamba Guard Solution |
|---|---|---|
| No advance drought warning | National forecasts cover entire counties — useless for a specific valley | 14-day hyperlocal drought prediction via Sentinel-2 + CHIRPS |
| No accessible crop insurance | Less than 3% of 4.5M farm households are insured | Parametric insurance via M-Pesa — KES 50/month entry tier |
| Survival deforestation | Farmers cut trees when crops fail → land degrades each season | M-Pesa payout removes the economic need to cut trees |
| Carbon market exclusion | Carbon audits cost $50,000–$200,000 — inaccessible for smallholders | Satellite-verified carbon credits minted automatically — zero audit cost to farmer |
| No planting intelligence | Wrong crop planted = full season loss | SMS crop recommendation based on seasonal forecast |

### 2.2 The Environmental Flywheel

```
Without Shamba Guard:
Drought → Crop failure → Tree cutting → Soil degradation → 
Worse drought → More crop failure → More tree cutting (loop)

With Shamba Guard:
Drought → Payout received → No tree cutting → 
Carbon preserved → Credit earned → Farmer income rises → 
More farmers insure → Larger pool → More payouts → Loop reversed
```

---

## 3. User Roles & Personas

Shamba Guard has **three distinct user roles** accessed through a **single Android application** with role-based navigation. Role is assigned at account creation and stored in the backend.

---

### 3.1 Admin

| Field | Detail |
|---|---|
| **Who** | Shamba Guard platform operations team |
| **Device** | Android (admin-specific screens) |
| **Auth** | Email + password + mandatory MFA |
| **Data Scope** | Full platform visibility |
| **Count (MVP)** | 1–3 people maximum |

**Admin Responsibilities:**
- Approve or reject field agent registration requests
- Monitor platform-wide pool health (balance vs total coverage liability)
- View all registered farm polygons on a Kenya map with NDVI overlay
- View real-time drought trigger status across all active farms
- Monitor and flag suspicious agent activity (fraud detection)
- View revenue analytics — premiums collected, payouts fired, platform fees
- Manage reinsurance partner thresholds and coverage limits
- Suspend agent or farmer accounts when required

> ⚠️ **Critical Rule:** Admin cannot manually trigger a payout. Only the verified satellite oracle can trigger payouts. This rule removes the admin fraud vector and is enforced at the smart contract level.

---

### 3.2 Field Agent

| Field | Detail |
|---|---|
| **Who** | Agricultural extension officers, cooperative managers, NGO field workers |
| **Device** | Android |
| **Auth** | Phone number + OTP + biometric on device |
| **Data Scope** | Only farmers they personally registered |
| **Incentive** | KES 50–100 commission per successfully onboarded farmer who completes first premium |

**Agent Responsibilities:**
- Register new farmers on their behalf (national ID, photo, phone, GPS farm polygon)
- Log quarterly farm practice data (crop type, tillage method, tree count, irrigation)
- Capture GPS-tagged, timestamped evidence photos using CameraX
- Educate farmers on coverage tiers and assist with first M-Pesa premium payment
- Work fully offline — all data queues locally and syncs automatically when connected
- View personal commission dashboard and farmer portfolio

> 💡 **Why Agents Exist:** Most smallholder farmers in Ukambani and Meru do not have smartphones or will not self-onboard a fintech app. The agent is the human trust bridge between Shamba Guard and the farmer.

---

### 3.3 Farmer

| Field | Detail |
|---|---|
| **Who** | Smallholder farmers — primary beneficiary |
| **Device** | Smartphone (app) OR basic phone (SMS only) |
| **Auth** | Phone number + OTP only (no password friction) |
| **Data Scope** | Own farm data only |
| **Entry Point** | Self-register (smartphone) OR registered by agent (basic phone) |

**Two Farmer Sub-Types:**

| Sub-Type | Device | Experience |
|---|---|---|
| **Smartphone Farmer** | Android phone | Full app experience — dashboard, NDVI chart, drought gauge, policy management |
| **Basic Phone Farmer** | Feature phone only | SMS-only — receives early warnings, payout notifications, carbon credit updates in Kiswahili |

**Farmer Journey:**
1. Registered (self or via agent) → farm polygon drawn on map
2. Selects coverage tier → pays first premium via M-Pesa STK Push
3. Policy activates → receives SMS confirmation
4. Receives 14-day drought early warning → SMS with crop recommendation
5. Drought confirmed by satellite → M-Pesa payout fires automatically in 30 seconds
6. Over 12 months → carbon credit minted → M-Pesa carbon income received

---

## 4. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     ANDROID APPLICATION                          │
│   ┌──────────┐      ┌──────────────┐      ┌───────────────┐    │
│   │  Admin   │      │ Field Agent  │      │    Farmer     │    │
│   │  Screens │      │   Screens    │      │   Screens     │    │
│   └────┬─────┘      └──────┬───────┘      └──────┬────────┘    │
└────────┼────────────────────┼─────────────────────┼─────────────┘
         │                    │                      │
         └────────────────────┼──────────────────────┘
                              │ HTTPS + JWT + Certificate Pinning
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     FASTAPI BACKEND                              │
│   Auth Service │ Farm Service │ Oracle Service │ Carbon Service  │
│                    PostgreSQL + PostGIS                          │
│                    Celery + Redis (async jobs)                   │
└──────────────────────────┬──────────────────────────────────────┘
              ┌────────────┼─────────────────┐
              ▼            ▼                 ▼
┌─────────────────┐ ┌─────────────┐ ┌──────────────────────────┐
│   AI/ML ENGINE  │ │  BLOCKCHAIN │ │  NOTIFICATION SERVICE    │
│ Google Earth    │ │ Polygon     │ │  Africa's Talking SMS    │
│ Engine          │ │ ShambaPool  │ │  Firebase Push (Android) │
│ Sentinel-2 NDVI │ │ .sol        │ │  WhatsApp Business API   │
│ CHIRPS Rainfall │ │ CarbonCredit│ │  (basic phone fallback)  │
│ Drought Model   │ │ .sol        │ └──────────────────────────┘
│ Carbon Model    │ │ IPFS/Pinata │
└─────────────────┘ └─────────────┘
              │
              ▼
┌──────────────────────────────────────┐
│         M-PESA DARAJA API            │
│  STK Push (premium collection)       │
│  B2C API (payout disbursement)       │
└──────────────────────────────────────┘
```

---

## 5. Backend & ML Requirements

> **Owner:** Backend/ML Engineer  
> **Stack:** Python, FastAPI, PostgreSQL + PostGIS, Celery, Redis, Google Earth Engine, Sentinel-2, CHIRPS, scikit-learn, Web3.py  
> **Hosting:** Railway.app free tier → Hetzner CX11 VPS (~$6/month)

---

### 5.1 Authentication Service

> **MVP Decision (Locked):** Phone + Password auth for Farmers and Agents. OTP-only login is a Post-MVP improvement. Admin accounts remain email + password + MFA.

#### Actual Backend Endpoints (MVP)

- `POST /api/farmers/register` — Register a new Farmer account
- `POST /api/farmers/login` — Farmer login, returns JWT
- `POST /api/agents/register` — Register a new Agent account (status: PENDING until admin approves)
- `POST /api/agents/login` — Agent login (requires `is_active = true`, set by admin on approval)

#### Registration Request Body (Both Farmer and Agent)

```json
{
  "full_name": "Jane Muthoni",
  "phone_number": "+254712345678",
  "password": "securepassword",
  "county": "Kitui"
}
```

> **Field Note:** The location field is `county` (e.g., "Kitui", "Meru", "Machakos"), not `region`. This matches the backend database model.

#### Registration Response

```json
// Farmer: POST /api/farmers/register
{ "message": "Farmer registered successfully", "farmer_id": "uuid" }

// Agent: POST /api/agents/register
{ "message": "Agent registered successfully", "agent_id": "uuid" }
```

#### Login Request Body

```json
{
  "phone_number": "+254712345678",
  "password": "securepassword"
}
```

#### Login Response (Both Farmer and Agent)

```json
{
  "access_token": "eyJ...",
  "token_type": "bearer",
  "role": "Farmer",
  "user_id": "uuid"
}
```

- Role must be embedded in JWT payload — Android reads role on login and routes to the correct home screen
- All tokens must be invalidated on password change or account suspension

#### Registration Flow — Farmer vs Agent

```
Farmer Registration:
  1. Account creation (name, phone, county, password) → POST /api/farmers/register
  2. Farm boundary drawing (Google Maps polygon)
  3. Farm practices form (crop type, tillage, water source, tree count)
  4. Coverage tier selection + M-Pesa STK Push
  → Lands on FarmerDashboard

Agent Registration:
  1. Account creation (name, phone, county, password) → POST /api/agents/register
  2. AgentPendingScreen shown — explains admin must approve before login
  → Admin approves in backend → Agent can then log in
  → Lands on AgentDashboard
```

#### Business Rules

- One phone number maps to exactly one farmer account — duplicate phone rejected at registration
- Agent accounts require admin approval before activation — `is_active` flag set to `true` by admin
- Admin accounts require MFA (Post-MVP — not yet implemented on backend)
- Post-MVP: OTP-only login (Africa's Talking SMS) to replace password-based auth

---

### 5.2 Farm Management Service

#### Requirements

- `POST /api/v1/farms` — Register new farm (polygon GeoJSON, farmer ID, practice data)
- `GET /api/v1/farms/{farm_id}` — Get farm details + latest carbon/NDVI report
- `GET /api/v1/farms/agent/{agent_id}` — Get all farms registered by a specific agent
- `PUT /api/v1/farms/{farm_id}/practices` — Update quarterly farm practice log
- `POST /api/v1/farms/{farm_id}/evidence` — Upload geo-tagged evidence photo (S3 or Supabase storage)
- `GET /api/v1/farms/admin/all` — Admin only — all farms with filters (region, status, risk level)

#### Data Model — Farm

```
Farm {
  farm_id: UUID
  farmer_id: UUID (FK)
  agent_id: UUID (FK)
  polygon: Geometry (PostGIS — POLYGON type)
  area_hectares: Float
  region: String (e.g., "Ukambani", "Meru")
  practices: {
    crop_type: String
    tillage_method: Enum [NO_TILL, CONVENTIONAL, MINIMUM]
    tree_count: Integer
    irrigation_source: Enum [RAIN_FED, RIVER, BOREHOLE, NONE]
  }
  carbon_status: Enum [PENDING, VERIFIED, MINTED, RETIRED]
  created_at: Timestamp
  updated_at: Timestamp
}
```

#### Polygon Validation Rules

- Minimum farm area: 0.1 hectares — reject smaller polygons (likely coordinate errors)
- Maximum farm area: 50 hectares — flag larger polygons for manual admin review
- Polygon must be within Kenya's bounding box — reject coordinates outside country bounds
- Self-intersecting polygons must be rejected (PostGIS `ST_IsValid` check)
- One farmer can have a maximum of 5 registered farm polygons in MVP

---

### 5.3 Satellite Intelligence & Drought Engine

This is the core AI layer. It runs as an **async Celery task**, not a synchronous API call. Processing time per farm is 30–120 seconds depending on imagery availability.

#### Trigger Schedule

- Every registered farm polygon is analysed every **5 days** (aligned to Sentinel-2 revisit cycle)
- Analysis runs as a scheduled Celery beat task at 02:00 EAT (low-traffic window)
- New farm polygons trigger an immediate first analysis on registration

#### The Analysis Pipeline

```python
def analyse_farm(farm_id: str, polygon: GeoJSON):

    # Step 1 — Pull Sentinel-2 imagery via Google Earth Engine
    # Cloud-masked composite of past 21 days
    imagery = gee.get_sentinel2_composite(
        polygon=polygon,
        days_back=21,
        cloud_cover_max=20  # reject heavily clouded imagery
    )

    # Step 2 — Compute NDVI
    # NDVI = (NIR - RED) / (NIR + RED)
    # Sentinel-2 bands: B8 (NIR), B4 (RED)
    # Range: -1 to 1
    # Healthy vegetation: > 0.5
    # Moderate stress: 0.2 – 0.5
    # Severe stress / drought: < 0.2
    ndvi_mean = compute_ndvi(imagery.B8, imagery.B4)
    ndvi_trend = compute_ndvi_trend(farm_id, days=60)  # declining = concerning

    # Step 3 — Pull CHIRPS rainfall data
    # Climate Hazards Group InfraRed Precipitation with Stations
    # 5km resolution, free, no auth required
    rainfall_21d = chirps.get_accumulated_rainfall(
        lat=polygon.centroid.lat,
        lng=polygon.centroid.lng,
        days=21
    )
    rainfall_seasonal = chirps.get_seasonal_anomaly(lat, lng)  # vs historical baseline

    # Step 4 — Pull SoilGrids soil moisture context
    soil_data = soilgrids.get(lat, lng)  # soil type affects drought sensitivity

    # Step 5 — Run drought classifier
    # Input features: ndvi_mean, ndvi_trend, rainfall_21d,
    #                 rainfall_seasonal_anomaly, soil_type, month_of_year
    drought_score = classifier.predict_proba([
        ndvi_mean, ndvi_trend, rainfall_21d,
        rainfall_seasonal, soil_data.field_capacity, month
    ])
    # Output: Float 0.0 – 1.0 (probability of drought conditions)

    # Step 6 — 14-day forecast (early warning model)
    forecast_score = forecast_model.predict([
        chirps.get_forecast(lat, lng, days=14),
        ndvi_trend,
        season_indicator
    ])

    # Step 7 — Determine actions
    if forecast_score > 0.65:
        trigger_early_warning(farm_id)       # SMS alert to farmer

    if drought_score > 0.80 and rainfall_21d < 40:
        trigger_insurance_oracle(farm_id)    # Fires smart contract payout

    # Step 8 — Store results
    store_farm_report(farm_id, ndvi_mean, rainfall_21d, drought_score, forecast_score)
```

#### Drought Trigger Thresholds (MVP defaults — tunable by admin)

| Condition | Threshold | Action |
|---|---|---|
| Forecast drought risk | > 0.65 probability | Send 14-day early warning SMS |
| Active drought confirmed | NDVI < 0.30 AND rainfall < 40mm/21 days | Trigger insurance payout oracle |
| High carbon confidence | NDVI > 0.50 sustained for 90 days | Queue carbon credit minting |

#### Fallback — If GEE Access Is Delayed

If Google Earth Engine research access approval takes longer than expected, fallback to direct Sentinel-2 tile download from **Copernicus Open Access Hub** (scihub.copernicus.eu). No approval required. Tiles are available as GeoTIFF — process with `rasterio` + `numpy` locally.

---

### 5.4 Insurance Oracle Service

The oracle service is the bridge between the Python AI engine and the blockchain smart contract. It must be treated as the highest-security service in the backend.

#### Requirements

- When drought is confirmed, oracle must:
    1. Generate a verification report JSON (farm_id, ndvi_value, rainfall_mm, drought_score, satellite_timestamp, computation_hash)
    2. Upload report to IPFS via Pinata → receive `ipfs_cid`
    3. Sign the trigger data with the oracle private key (ECDSA)
    4. Submit signed trigger to `ShambaPool.sol` via Web3.py
    5. Store `tx_hash` + `ipfs_cid` in PostgreSQL against the farm record
    6. Send M-Pesa payout confirmation SMS to farmer

- Oracle private key must **never** appear in code — stored in environment secret / HSM
- All oracle submissions must be logged with full audit trail
- Failed submissions must retry with exponential backoff (max 3 attempts)
- Alert admin via push notification if oracle submission fails after all retries

#### Oracle Security Rules

- Smart contract rejects any `triggerPayout` call without valid oracle ECDSA signature
- Oracle key rotation must be supported — new key can be registered by admin multi-sig
- Rate limit: maximum 500 payout triggers per 24 hours (flags mass-event attacks)
- If >30% of all active policies trigger within 24 hours → pause oracle, alert admin (possible coordinate spoofing attack)

---

### 5.5 Carbon Credit Service

#### Requirements

- `GET /api/v1/carbon/{farm_id}/report` — Returns 12-month NDVI trend + estimated carbon tonnes
- `POST /api/v1/carbon/{farm_id}/mint` — Initiates carbon credit minting pipeline (called internally by Celery after 90-day NDVI threshold met)
- `GET /api/v1/carbon/{farm_id}/credits` — Returns all minted credits, status, and M-Pesa payout history

#### Carbon Estimation Model

```
Inputs:
- NDVI mean (90-day sustained average)
- Biomass density estimate (from NDVI → biomass regression)
- Tree canopy coverage % (Sentinel-2 detectable at 10m)
- Agent-reported tree count (ground truth cross-reference)
- Farm area in hectares

Output:
- Estimated above-ground carbon stock (tonnes CO2e)
- Confidence score (0–100)
- Eligible for credit minting: True/False (requires confidence > 70)

Training data sources:
- ICRAF (World Agroforestry Centre, Nairobi) — open datasets
- FAO global soil carbon data
- Kenya Forest Service ground truth plots
```

---

### 5.6 M-Pesa Integration

#### Premium Collection (Farmer → Platform)

- Use **Daraja STK Push (Lipa Na M-Pesa Online)** for farmer premium payments
- Android app calls backend `/api/v1/payments/stk-push` with farmer phone + amount
- Backend calls Daraja API → pushes STK prompt to farmer's phone
- Daraja callback updates payment status in database
- On successful payment → activate/renew policy in smart contract

#### Payout Disbursement (Platform → Farmer)

- Use **Daraja B2C API** for drought payouts and carbon credit payments
- Backend calls B2C with farmer phone + amount (triggered by oracle confirmation)
- All B2C transactions logged with Daraja transaction ID for audit
- Failed B2C calls must alert admin immediately and retry within 15 minutes

> 💡 **Note:** At MVP/hackathon stage, use **Daraja sandbox** (free). Production requires Safaricom Go-Live approval. Apply during Week 6 — approval takes 5–10 business days.

---

### 5.7 Backend Non-Functional Requirements

- All endpoints must respond within **2 seconds** for synchronous calls
- Satellite analysis jobs are **async only** — never block an HTTP response
- Database queries on farm polygons must use **PostGIS spatial indexes**
- API must handle **graceful degradation** — if GEE is unavailable, cache last known NDVI and notify admin
- All database migrations tracked with **Alembic**
- API documentation auto-generated with **FastAPI Swagger UI** at `/docs`

---

## 6. Blockchain Requirements

> **Owner:** Blockchain Engineer  
> **Stack:** Solidity, Polygon (Mumbai testnet → Mainnet), Hardhat, IPFS/Pinata, ERC-1155, Web3.py (backend integration), Ethers.js  
> **Deployment:** Polygon Mumbai (development) → Polygon Mainnet (production)

---

### 6.1 Why Polygon, Not Ethereum Mainnet

| Factor | Ethereum Mainnet | Polygon |
|---|---|---|
| Gas per transaction | $5–$50 | $0.001–$0.01 |
| Payout viability | ❌ Gas > KES 2,000 payout | ✅ Gas is negligible |
| EVM compatibility | ✅ | ✅ (same Solidity code) |
| Bridge to Ethereum | N/A | ✅ Available when needed |
| Speed | ~15 seconds | ~2 seconds |

---

### 6.2 Contract 1 — ShambaPool.sol

This is the insurance treasury. It holds the mutual premium pool and executes automatic payouts.

#### Core Functions

```solidity
contract ShambaPool is ReentrancyGuard, Ownable {

    struct Policy {
        address farmer;           // farmer's custodial wallet
        bytes32 polygonHash;      // keccak256 of farm polygon coordinates
        uint256 premiumPaidWei;   // premium deposited
        uint256 coverageAmountWei;// max payout amount
        uint8 tier;               // 1, 2, or 3
        bool active;
        uint256 createdAt;
        uint256 expiresAt;        // 30 days from last premium payment
    }

    address public trustedOracle; // only this address can trigger payouts

    // Called by backend when farmer pays premium via M-Pesa
    // Backend converts KES premium to MATIC equivalent before calling
    function purchasePolicy(
        address farmer,
        bytes32 polygonHash,
        uint8 tier
    ) external payable onlyBackend

    // Called ONLY by the verified oracle when drought is confirmed
    // satelliteDataCID = IPFS hash of the satellite verification report
    function triggerPayout(
        uint256 policyId,
        bytes32 satelliteDataCID,
        bytes memory oracleSignature  // ECDSA — contract validates signature
    ) external onlyOracle nonReentrant

    // Pool health check — backend queries this before issuing new policies
    function getPoolHealthRatio() external view returns (uint256)
    // Returns: (pool_balance / total_coverage_liability) * 100
    // Must be > 150% for new policies to be issued (150% coverage ratio)

    // Governance — parameter changes require 3-of-5 multisig
    function updateTriggerThreshold(uint8 newNDVI, uint8 newRainfallMM)
        external onlyMultisig

    function updateOracleAddress(address newOracle)
        external onlyMultisig

    event PolicyPurchased(uint256 policyId, address farmer, uint8 tier);
    event PayoutExecuted(uint256 policyId, address farmer, uint256 amount, bytes32 ipfsCID);
    event PolicyExpired(uint256 policyId);
}
```

#### Coverage Ratio Rule (Critical)

The smart contract **must enforce** that total outstanding coverage never exceeds the pool balance by more than 1.5x. This prevents pool insolvency.

```
Pool balance: 100,000 MATIC equivalent
Max total coverage that can be issued: 150,000 MATIC equivalent
If coverage ratio drops below 150% → new policy purchases paused automatically
```

---

### 6.3 Contract 2 — CarbonCredit.sol

Manages the minting and trading of verified carbon credit tokens.

#### Core Functions

```solidity
contract CarbonCredit is ERC1155, Ownable {

    struct CreditMetadata {
        bytes32 farmId;
        uint256 carbonTonnes;       // e.g., 420 = 4.20 tonnes (2 decimal precision)
        uint8 confidenceScore;      // 0–100 from Python engine
        bytes32 ipfsVerificationCID;// IPFS hash of satellite verification report
        uint256 issuedAt;
        bool retired;               // retired = permanently removed from circulation
    }

    // Only verified backend can mint new credits
    function mintCredits(
        address farmer,
        uint256 farmId,
        uint256 carbonTonnes,
        uint8 confidence,
        bytes32 ipfsCID
    ) external onlyMinter

    // Corporate buyer calls this to permanently retire (offset) a credit
    function retireCredits(uint256 tokenId, uint256 amount)
        external

    // Returns full verification details for any credit token
    function getCreditMetadata(uint256 tokenId)
        external view returns (CreditMetadata memory)

    event CreditMinted(uint256 tokenId, address farmer, uint256 tonnes, bytes32 ipfsCID);
    event CreditRetired(uint256 tokenId, address buyer, uint256 amount, uint256 timestamp);
}
```

#### Why ERC-1155, Not ERC-721

- ERC-1155 supports **batch minting** — one transaction can mint credits for 100 farmers simultaneously
- Gas cost for batch mint is 80% lower than 100 individual ERC-721 mints
- Each token ID represents one farm's seasonal credit batch — fungible within a batch

---

### 6.4 Contract 3 — ShambaOracle.sol

A lightweight contract that manages oracle authentication. Separating oracle logic from the pool contract is a security best practice.

```solidity
contract ShambaOracle {

    mapping(address => bool) public authorizedOracles;
    address public admin;

    function verifyAndRelay(
        uint256 policyId,
        bytes32 satelliteDataCID,
        bytes memory signature
    ) external onlyOracle {
        // Verify ECDSA signature matches trustedOracle key
        require(isValidSignature(policyId, satelliteDataCID, signature), "Invalid oracle signature");
        // Relay to ShambaPool
        IShambaPool(poolAddress).triggerPayout(policyId, satelliteDataCID, signature);
    }
}
```

---

### 6.5 IPFS Evidence Storage

Every satellite verification report must be stored on IPFS **before** the blockchain transaction is submitted. The IPFS CID stored on-chain is the immutable audit trail.

#### Report Structure (uploaded to IPFS as JSON)

```json
{
  "farm_id": "uuid",
  "farmer_phone_hash": "sha256_of_phone",
  "analysis_timestamp": "2026-04-15T06:14:00Z",
  "polygon_hash": "keccak256_of_coordinates",
  "satellite_source": "Sentinel-2",
  "imagery_date_range": "2026-03-25 to 2026-04-15",
  "ndvi_mean": 0.24,
  "ndvi_trend": "declining",
  "chirps_rainfall_mm": 31.4,
  "chirps_period_days": 21,
  "drought_score": 0.87,
  "trigger_threshold_met": true,
  "coverage_amount_kes": 2400,
  "computation_version": "v1.2.0",
  "oracle_signature": "0x..."
}
```

---

### 6.6 Blockchain Development Checklist

```
Week 1:
[ ] Hardhat project initialized
[ ] ShambaPool.sol skeleton — structs, events, function signatures
[ ] ShambaOracle.sol — oracle authentication
[ ] Deploy both to Polygon Mumbai testnet
[ ] Get Mumbai test MATIC from faucet (mumbaifaucet.com)
[ ] Write 10 unit tests minimum (Hardhat + Chai)

Week 2:
[ ] CarbonCredit.sol — ERC-1155 implementation
[ ] IPFS upload pipeline via Pinata SDK
[ ] Web3.py integration test — Python backend calls contract
[ ] Reentrancy guard on all external functions
[ ] Access control — onlyOracle, onlyBackend, onlyMultisig modifiers

Week 5–6 (Integration):
[ ] End-to-end trigger test — Python oracle → ShambaOracle.sol → ShambaPool.sol
[ ] Payout confirmed on Mumbai block explorer
[ ] Carbon credit mint test — Python backend → CarbonCredit.sol → IPFS CID on-chain

Week 7:
[ ] Slither static analysis — zero critical findings before demo
[ ] Gas optimization review
[ ] Pool coverage ratio enforcement test
[ ] Document all deployed contract addresses
```

---

## 7. Android Requirements

> **Owner:** Android Developer (korryr)  
> **Stack:** Kotlin, Jetpack Compose, Room, Hilt, WorkManager, Retrofit, Google Maps SDK, CameraX, Daraja API, Firebase Cloud Messaging  
> **Architecture:** MVVM + Clean Architecture + Repository Pattern

---

### 7.1 Project Setup Requirements

```kotlin
// Required dependencies in build.gradle.kts

// Core
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

// DI — uses KSP (NOT kapt)
implementation("com.google.dagger:hilt-android")
ksp("com.google.dagger:hilt-android-compiler")

// Local DB — uses KSP (NOT kapt)
implementation("androidx.room:room-runtime")
implementation("androidx.room:room-ktx")
ksp("androidx.room:room-compiler")

// Network
implementation("com.squareup.retrofit2:retrofit")
implementation("com.squareup.okhttp3:okhttp")
implementation("com.squareup.okhttp3:logging-interceptor")

// Background Work
implementation("androidx.work:work-runtime-ktx")

// Maps
implementation("com.google.maps.android:maps-compose")

// Camera
implementation("androidx.camera:camera-compose")

// Security
implementation("androidx.security:security-crypto")
implementation("androidx.biometric:biometric")

// Push Notifications
implementation("com.google.firebase:firebase-messaging-ktx")

// API keys — NEVER in source code
// Store in: keys.properties → BuildConfig
```

> ⚠️ **Security Rule:** No API keys, M-Pesa credentials, or JWT secrets ever committed to Git. Every sensitive value lives in `keys.properties` (gitignored) and accessed via `BuildConfig`.

---

### 7.2 Room Database Schema

```kotlin
// Core entities — all stored locally for offline-first functionality

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val phone: String,
    val role: String,           // ADMIN | AGENT | FARMER
    val name: String,
    val nationalId: String?,    // Agents and farmers only
    val region: String?,
    val isApproved: Boolean,    // Agents require admin approval
    val createdAt: Long
)

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey val farmId: String,
    val farmerId: String,
    val agentId: String,
    val polygonJson: String,    // Serialized GeoJSON polygon
    val areaHectares: Double,
    val region: String,
    val cropType: String,
    val tillageMethod: String,
    val treeCount: Int,
    val carbonStatus: String,   // PENDING | VERIFIED | MINTED | RETIRED
    val syncStatus: String,     // PENDING_SYNC | SYNCED | FAILED
    val lastSyncedAt: Long?,
    val createdAt: Long
)

@Entity(tableName = "policies")
data class PolicyEntity(
    @PrimaryKey val policyId: String,
    val farmId: String,
    val farmerId: String,
    val tier: Int,              // 1, 2, or 3
    val premiumKes: Int,        // 50, 150, or 400
    val coverageKes: Int,       // 2000, 8000, or 25000
    val status: String,         // ACTIVE | EXPIRED | PENDING_PAYMENT
    val activatedAt: Long?,
    val expiresAt: Long?
)

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey val payoutId: String,
    val policyId: String,
    val farmId: String,
    val amountKes: Int,
    val txHash: String,         // Polygon transaction hash
    val ipfsCid: String,        // IPFS verification report link
    val triggeredAt: Long,
    val mpesaRef: String?       // Daraja transaction reference
)

@Entity(tableName = "farm_reports")
data class FarmReportEntity(
    @PrimaryKey val reportId: String,
    val farmId: String,
    val ndviMean: Double,
    val rainfallMm: Double,
    val droughtScore: Double,
    val forecastScore: Double,
    val riskLevel: String,      // LOW | MODERATE | HIGH | CRITICAL
    val recommendation: String, // e.g., "Plant cowpeas instead of maize"
    val generatedAt: Long
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entityType: String,     // FARM | PRACTICE_LOG | EVIDENCE_PHOTO
    val entityId: String,
    val payloadJson: String,    // Serialized payload to POST
    val retryCount: Int = 0,
    val createdAt: Long
)
```

---

### 7.3 Role-Based Navigation

```kotlin
// Single NavDisplay graph with role-based start destination
// Role read from DataStore on app launch

sealed class UserRole { object Admin, Agent, Farmer }

@Composable
fun ShambaGuardNavGraph(role: UserRole) {
    // 1. Determine starting key based on role
    val initialKey = remember(role) {
        when (role) {
            UserRole.Admin  -> AdminHomeKey
            UserRole.Agent  -> AgentHomeKey
            UserRole.Farmer -> FarmerHomeKey
        }
    }
    
    // 2. Initialize manual backStack for Navigation 3
    val backStack = remember { mutableStateListOf<Any>(initialKey) }
    
    // 3. Handle system back events
    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            // Admin screens
            entry<AdminHomeKey> { AdminHomeScreen(...) }
            
            // Agent screens
            entry<AgentHomeKey> { AgentHomeScreen(...) }
            
            // Farmer screens
            entry<FarmerHomeKey> { FarmerDashboardScreen(...) }
            
            // Shared screens
            entry<LoginKey> { LoginScreen(...) }
        }
    )
}
```

---

### 7.4 Screen Requirements by Role

#### Admin Screens

| Screen | Description | Priority |
|---|---|---|
| `AdminHomeScreen` | Overview cards: total farmers, pool balance, active policies, pending agents | P0 |
| `AgentManagementScreen` | List of pending/approved/suspended agents — approve/reject actions | P0 |
| `FarmMapScreen` | All farm polygons on Google Map with NDVI heatmap color overlay | P0 |
| `DroughtMonitorScreen` | Real-time list of farms sorted by drought risk score — trigger status | P0 |
| `PoolHealthScreen` | Pool balance vs total liability — coverage ratio gauge — reinsurance threshold | P0 |
| `CarbonRegistryScreen` | All credits minted, pending, retired — total tonnes, KES value | P1 |
| `RevenueScreen` | Premiums collected, payouts fired, platform fees, MRR trend | P1 |

#### Agent Screens

| Screen | Description | Priority |
|---|---|---|
| `AgentHomeScreen` | My farmer count, today's sync status, pending queue count, earnings summary | P0 |
| `FarmerRegistrationScreen` | National ID, name, phone, GPS polygon, coverage tier selection | P0 |
| `MapPolygonScreen` | Google Maps with tap-to-add polygon points — farmer farm boundary drawing | P0 |
| `FarmPracticesScreen` | Quarterly log: crop type, tillage, tree count, irrigation source | P0 |
| `EvidencePhotosScreen` | CameraX capture — auto-embeds GPS coordinates in EXIF metadata | P0 |
| `SyncStatusScreen` | Offline queue viewer — pending items, retry buttons, last sync timestamp | P0 |
| `AgentEarningsScreen` | Commission per farmer, payout timeline, total earned | P1 |
| `MyFarmersScreen` | List of all registered farmers — status, policy, last activity | P1 |

#### Farmer Screens (Smartphone)

| Screen | Description | Priority |
|---|---|---|
| `FarmerDashboardScreen` | NDVI trend chart (line chart — 60 days), drought risk gauge, policy status banner, latest alert | P0 |
| `EarlyWarningScreen` | 14-day forecast visualization, recommended crop to plant, risk explanation in Kiswahili | P0 |
| `PolicyScreen` | Coverage tier cards (Tier 1/2/3), premium amount, coverage amount, pay via M-Pesa | P0 |
| `PayoutHistoryScreen` | List of received payouts — amount, date, IPFS verification link, Polygon tx hash | P0 |
| `CarbonScreen` | Estimated carbon tonnes, credit status (pending/minted), KES earned, 12-month NDVI chart | P1 |
| `FarmerProfileScreen` | Phone, region, registered farms count, total premiums paid | P1 |

---

### 7.5 WorkManager Offline Sync

```kotlin
class FarmSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Read all PENDING_SYNC items from sync_queue table
            val pendingItems = syncRepository.getPendingQueue()

            pendingItems.forEach { item ->
                when (item.entityType) {
                    "FARM"           -> syncRepository.postFarm(item)
                    "PRACTICE_LOG"   -> syncRepository.postPractices(item)
                    "EVIDENCE_PHOTO" -> syncRepository.uploadPhoto(item)
                }
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }
}

// Register with constraints — only sync when network is available
val syncRequest = PeriodicWorkRequestBuilder<FarmSyncWorker>(15, TimeUnit.MINUTES)
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()
```

---

### 7.6 CameraX Evidence Photo Requirements

```kotlin
// Every photo captured by a field agent must:
// 1. Embed GPS coordinates in EXIF metadata
// 2. Embed timestamp in EXIF metadata
// 3. Store locally in Room before upload attempt
// 4. Be queued in SyncQueue for upload when connected
// 5. Be rejected if GPS location is unavailable (enforce GPS on before camera opens)

fun captureEvidencePhoto(
    imageCapture: ImageCapture,
    location: Location,         // GPS — mandatory, not optional
    farmId: String
) {
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                embedExifMetadata(output.savedUri, location) // GPS + timestamp
                queueForUpload(farmId, output.savedUri)
            }
        }
    )
}
```

---

### 7.7 M-Pesa STK Push Flow (Android)

```kotlin
// Farmer selects coverage tier → taps "Pay with M-Pesa"
// Flow:
// 1. Android calls backend POST /api/v1/payments/stk-push
// 2. Backend calls Daraja STK Push API
// 3. Farmer receives M-Pesa prompt on phone
// 4. Farmer enters PIN
// 5. Daraja sends callback to backend webhook
// 6. Backend updates policy status → sends push notification to Android
// 7. Android receives FCM notification → refreshes policy screen

data class StkPushRequest(
    val farmerId: String,
    val phoneNumber: String,    // Format: 2547XXXXXXXX
    val amountKes: Int,
    val policyTier: Int,
    val farmId: String
)

// Show a "waiting for payment" screen with animated indicator
// Poll policy status every 5 seconds for max 2 minutes
// Timeout with retry option if no callback received
```

---

### 7.8 Android Build Checklist

```
Week 1:
[ ] Project setup: MVVM + Hilt + Room + Compose + Retrofit
[ ] Role-based navigation skeleton (all three NavGraphs)
[ ] Login screen (phone OTP for agent/farmer, email+password+MFA for admin)
[ ] DataStore setup — encrypted session storage
[ ] Room schema: all entities defined, DAO interfaces written
[ ] WorkManager SyncWorker — reads queue, POST to backend

Week 2:
[ ] Agent: FarmerRegistrationScreen + MapPolygonScreen
[ ] Agent: CameraX evidence photos with GPS EXIF
[ ] Agent: SyncStatusScreen — offline queue viewer
[ ] Farmer: PolicyScreen + M-Pesa STK Push flow
[ ] Android Keystore setup for request signing

Week 5 (Integration):
[ ] FarmerDashboardScreen — consumes GET /api/v1/farms/{farm_id}/carbon-report
[ ] EarlyWarningScreen — displays forecast data from backend
[ ] PayoutHistoryScreen — shows tx_hash + IPFS link
[ ] Admin: FarmMapScreen — polygon overlay + NDVI heatmap

Week 7:
[ ] FCM push notifications connected
[ ] Certificate pinning configured
[ ] ProGuard rules verified
[ ] Demo seed data loaded (3 agents, 10 farmers, 2 with active drought alerts)
[ ] Real Meru GPS coordinates used for demo polygon
```

---

## 8. Security Requirements

> **Owner:** Security Engineer (beato)  
> **Scope:** All four layers — Android, Backend API, Blockchain, Oracle  
> **Standard:** OWASP Mobile Top 10 + OWASP API Top 10 + Kenya Data Protection Act 2019

---

### 8.1 Android Security

| Requirement | Implementation | Priority |
|---|---|---|
| No API keys in source code | `keys.properties` → `BuildConfig` | P0 |
| Encrypted session storage | `EncryptedDataStore` — never `SharedPreferences` for sensitive data | P0 |
| Certificate pinning | OkHttp `CertificatePinner` — rejects connections to any server except our backend | P0 |
| Biometric gate on payments | `BiometricPrompt` required before any M-Pesa STK Push action | P0 |
| Android Keystore for signing | Agent payload signing keys stored in `AndroidKeyStore` — never exportable | P0 |
| Root detection | Detect rooted devices — show warning, log to backend for fraud review | P1 |
| ProGuard obfuscation | Enabled on all release builds | P1 |
| Screenshot prevention | `FLAG_SECURE` on screens showing financial data | P1 |
| Minimum SDK | API 26 (Android 8.0) minimum — enforces modern security baseline | P1 |

---

### 8.2 Backend API Security

| Requirement | Implementation | Priority |
|---|---|---|
| JWT expiry | Access token: 15 minutes. Refresh token: 7 days with rotation | P0 |
| Input validation | All farm polygon coordinates validated against Kenya bounding box | P0 |
| PostGIS geometry validation | `ST_IsValid` check on all submitted polygons before storage | P0 |
| Rate limiting | 100 requests/minute per authenticated user — 429 on breach | P0 |
| Anti-Sybil | One national ID → one farmer account. Duplicate detection on registration | P0 |
| SQL injection prevention | SQLAlchemy ORM only — no raw SQL queries | P0 |
| HTTPS only | TLS 1.3 enforced — HTTP connections rejected | P0 |
| CORS policy | Strict origin whitelist — only our Android app and admin domains | P1 |
| Secrets management | All credentials in environment variables — never in codebase | P0 |

---

### 8.3 Oracle Security

The oracle is the highest-value attack target. A compromised oracle can drain the insurance pool.

| Requirement | Implementation | Priority |
|---|---|---|
| ECDSA signing | Every oracle submission signed with private key — contract rejects unsigned calls | P0 |
| Key in environment secret | Oracle private key never in code — stored in Railway/Hetzner environment secret | P0 |
| IPFS evidence independence | Satellite report uploaded to IPFS before on-chain submission — any discrepancy is flagged | P0 |
| Mass-trigger detection | >30% of policies triggering in 24 hours → oracle paused, admin alerted | P0 |
| Dual verification for large payouts | Payouts above KES 10,000 require two independent NDVI readings (24-hour interval) | P1 |
| Replay attack prevention | Each oracle submission includes a unique nonce — contract rejects reused nonces | P0 |

---

### 8.4 Smart Contract Security

| Requirement | Implementation | Priority |
|---|---|---|
| Reentrancy guard | `ReentrancyGuard` on all payout and withdrawal functions | P0 |
| Access control | `onlyOracle`, `onlyBackend`, `onlyMultisig` modifiers on all privileged functions | P0 |
| No admin backdoor | Admin cannot trigger payout or withdraw pool funds unilaterally | P0 |
| Multisig governance | Pool parameter changes require 3-of-5 signature approval | P0 |
| Slither analysis | Zero critical findings on Slither static analysis before any mainnet deployment | P0 |
| Coverage ratio enforcement | Contract rejects new policy creation if pool coverage ratio < 150% | P0 |
| Event logging | All state changes emit events — permanent, queryable audit trail | P1 |

---

### 8.5 Data Privacy (Kenya Data Protection Act 2019)

| Requirement | Implementation |
|---|---|
| Encryption at rest | AES-256 on all personal data columns (name, national ID, phone) using `pgcrypto` |
| Role-based data access | Agents see only their registered farmers. Admins see aggregate data |
| Right to erasure | Farmer can request account deletion — personal data anonymized within 30 days |
| Data minimization | Only collect data required for insurance and carbon verification |
| Privacy policy | In-app, available in Kiswahili and English before any data collection |

---

### 8.6 Security Review Schedule

| Milestone | Review | Who |
|---|---|---|
| End of Week 4 | Smart contract audit — Slither static analysis | Security Engineer |
| End of Week 6 | API penetration test (OWASP API Top 10) | Security Engineer |
| End of Week 7 | Android security review (OWASP Mobile Top 10) | Security Engineer |
| Demo Day | Final security sign-off — all P0 requirements verified | Security Engineer |

---

## 9. Notification Strategy

> **Decision needed by Week 2.** Three options are presented. Recommendation is Option A for MVP.

---

### Option A — Africa's Talking SMS (Recommended for MVP)

| Attribute | Detail |
|---|---|
| **Reach** | All phones including basic feature phones — 100% farmer coverage |
| **Cost** | ~KES 0.40 per SMS in Kenya |
| **Language** | Full Kiswahili support |
| **Setup** | Free sandbox, simple HTTP API, already documented |
| **Limitation** | Paid per message — cost scales with farmer count |

**Use for:** Early warning alerts, payout notifications, carbon credit updates for all farmers regardless of phone type.

---

### Option B — Firebase Cloud Messaging (Supplementary)

| Attribute | Detail |
|---|---|
| **Reach** | Smartphone Android users only |
| **Cost** | Free (unlimited) |
| **Language** | Any |
| **Setup** | Firebase console + FCM SDK in Android app |
| **Limitation** | Requires smartphone + internet connection |

**Use for:** Rich push notifications with charts and deep links for smartphone farmers. Secondary channel after SMS.

---

### Option C — WhatsApp Business API (Post-MVP)

| Attribute | Detail |
|---|---|
| **Reach** | WhatsApp users only (~60% of Kenyan smartphone users) |
| **Cost** | Free for first 1,000 conversations/month — paid after |
| **Language** | Kiswahili supported |
| **Setup** | Meta Business Account + webhook setup — moderate complexity |
| **Limitation** | Requires WhatsApp — excludes basic phone farmers |

**Use for:** Post-MVP rich messaging — photo reports, voice notes, two-way farmer queries.

---

### MVP Decision

> ✅ **Use Africa's Talking for all payout and early warning alerts (basic + smartphone farmers)**  
> ✅ **Use Firebase FCM for push notifications to smartphone farmer app users**  
> ⏳ **WhatsApp Business API — post-MVP Phase 2**

---

### SMS Templates (Kiswahili)

```
EARLY WARNING:
"SHAMBA GUARD: Mvua itakuwa chini sana wiki 2 zijazo katika eneo lako.
Panda: maharagwe ya njano badala ya mahindi. Wasiliana na wakala wako. -ShambGuard"

PAYOUT CONFIRMATION:
"SHAMBA GUARD: Ukame umethibitishwa shambani mwako.
KES [AMOUNT] imetumwa kwa M-Pesa yako [PHONE]. Kumbukumbu: [MPESA_REF]. -ShambGuard"

CARBON CREDIT:
"SHAMBA GUARD: Hongera! Umepata [TONNES] tani za carbon credits.
KES [AMOUNT] itumwa kwa M-Pesa yako wiki hii. Asante kwa kulinda ardhi. -ShambGuard"

POLICY ACTIVATED:
"SHAMBA GUARD: Bima yako imewashwa.
Kiwango: Tier [TIER] | Kiasi: KES [COVERAGE] | Mwisho: [DATE]. -ShambGuard"
```

---

## 10. API Contract Reference

> This is the agreed interface between Android and Backend. Define this before writing integration code. Any changes must be communicated to all team members.

---

### Authentication

> **MVP Note:** Auth uses phone + password. OTP login is Post-MVP. County field is used (not region).

```
POST /api/farmers/register
Body: { "full_name": "...", "phone_number": "+254712345678", "password": "...", "county": "Kitui" }
Response: { "message": "Farmer registered successfully", "farmer_id": "uuid" }

POST /api/farmers/login
Body: { "phone_number": "+254712345678", "password": "..." }
Response: { "access_token": "...", "token_type": "bearer", "role": "Farmer", "user_id": "uuid" }

POST /api/agents/register
Body: { "full_name": "...", "phone_number": "+254712345678", "password": "...", "county": "Meru" }
Response: { "message": "Agent registered successfully", "agent_id": "uuid" }

POST /api/agents/login
Body: { "phone_number": "+254712345678", "password": "..." }
Response: { "access_token": "...", "token_type": "bearer", "role": "Agent", "user_id": "uuid" }
Note: Returns 401 if agent account is not yet approved by admin (is_active = false)
```

---

### Farms

```
POST /api/v1/farms
Auth: Bearer token (Agent only)
Body: {
  "farmer_id": "uuid",
  "polygon": { GeoJSON Polygon },
  "practices": { "crop_type": "maize", "tillage": "NO_TILL", "tree_count": 12 }
}
Response: { "farm_id": "uuid", "area_hectares": 1.8, "analysis_queued": true }

GET /api/v1/farms/{farm_id}/report
Auth: Bearer token
Response: {
  "ndvi_mean": 0.52,
  "rainfall_mm": 74.2,
  "drought_score": 0.21,
  "forecast_score": 0.34,
  "risk_level": "LOW",
  "recommendation": "Conditions stable. Continue current crop.",
  "carbon_tonnes_estimated": 2.4,
  "carbon_status": "PENDING",
  "generated_at": "2026-04-15T06:00:00Z"
}
```

---

### Insurance

```
POST /api/v1/policies/purchase
Auth: Bearer token (Farmer)
Body: { "farm_id": "uuid", "tier": 2 }
Response: { "stk_push_initiated": true, "checkout_request_id": "ws_CO_..." }

GET /api/v1/policies/{farm_id}
Auth: Bearer token
Response: {
  "policy_id": "uuid",
  "tier": 2,
  "premium_kes": 150,
  "coverage_kes": 8000,
  "status": "ACTIVE",
  "expires_at": "2026-05-15"
}

GET /api/v1/payouts/{farmer_id}
Auth: Bearer token
Response: [ {
  "payout_id": "uuid",
  "amount_kes": 8000,
  "tx_hash": "0x...",
  "ipfs_cid": "Qm...",
  "triggered_at": "2026-04-10T06:14:00Z"
} ]
```

---

### Carbon Credits

```
GET /api/v1/carbon/{farm_id}/credits
Auth: Bearer token
Response: [ {
  "token_id": 1,
  "carbon_tonnes": 2.4,
  "confidence_score": 82,
  "ipfs_cid": "Qm...",
  "status": "MINTED",
  "kes_earned": 800,
  "issued_at": "2026-03-01"
} ]
```

---

### Admin

```
GET /api/v1/admin/pool/health
Auth: Admin token
Response: { "pool_balance_kes": 450000, "total_coverage_kes": 280000, "ratio": 1.6, "status": "HEALTHY" }

GET /api/v1/admin/agents/pending
Auth: Admin token
Response: [ { "agent_id": "uuid", "name": "...", "phone": "...", "registered_at": "..." } ]

PUT /api/v1/admin/agents/{agent_id}/approve
Auth: Admin token
Response: { "approved": true }
```

---

## 11. Non-Functional Requirements

| Requirement | Target | Applies To |
|---|---|---|
| API response time | < 2 seconds for all synchronous endpoints | Backend |
| Satellite analysis time | < 5 minutes per farm (async Celery task) | Backend/ML |
| Payout execution time | < 30 seconds from oracle trigger to M-Pesa notification | Blockchain + Backend |
| Android app cold start | < 3 seconds on mid-range device (Tecno, Infinix) | Android |
| Offline functionality | All agent data entry works with zero connectivity | Android |
| Sync reliability | Zero data loss on WorkManager queue — idempotent POST endpoints | Android + Backend |
| Database uptime | 99.9% via Supabase managed PostgreSQL | Backend |
| Smart contract test coverage | > 90% line coverage before mainnet deployment | Blockchain |
| API test coverage | > 80% critical path coverage | Backend |
| Minimum Android version | API 26 (Android 8.0) — covers 95%+ of Kenyan Android devices | Android |

---

## 12. Zero-Budget Tech Stack

| Layer | Resource | Tool / Service | Cost |
|---|---|---|---|
| Satellite Imagery | Sentinel-2 via Google Earth Engine | GEE Research Account | Free |
| Rainfall Data | CHIRPS — Climate Hazards Group | Direct REST API | Free |
| Soil Data | SoilGrids — ISRIC | REST API | Free |
| ML Training Data | ICRAF Open Datasets + FAO | Open download | Free |
| Database | PostgreSQL + PostGIS | Supabase free tier | Free |
| Backend Compute | FastAPI + Celery + Redis | Railway.app → Hetzner CX11 | ~$6/month |
| Blockchain Dev | Solidity + Hardhat + Chai | Open source | Free |
| Testnet | Polygon Mumbai | Free MATIC from faucet | Free |
| Mainnet | Polygon | ~$2 total deploy cost | ~$2 one-time |
| IPFS Storage | Evidence reports + metadata | Pinata free tier (1GB) | Free |
| Maps SDK | Google Maps polygon + heatmap | Free (28k req/month) | Free |
| SMS | Early warning + payout alerts | Africa's Talking sandbox | Free (sandbox) |
| Push Notifications | Android FCM | Firebase free tier | Free |
| M-Pesa | STK Push + B2C | Daraja sandbox | Free (sandbox) |
| CI/CD | Automated builds + tests | GitHub Actions free tier | Free |
| Security Analysis | Smart contract static analysis | Slither (open source) | Free |
| Android Security | Crypto + biometric | Jetpack Security (open source) | Free |
| **Total Monthly** | | | **~$6–8/month** |

---

## 13. Sprint Plan

### Month 1 — Build

| Week | Backend/ML | Blockchain | Android | Security |
|---|---|---|---|---|
| **Week 1** | FastAPI scaffold, auth service, Supabase PostGIS setup, GEE access application | Hardhat setup, ShambaPool.sol skeleton, deploy to Mumbai testnet | Project setup: MVVM + Hilt + Room + Compose, login screen, role navigation | Threat model document — identify all attack surfaces |
| **Week 2** | Farm service CRUD, polygon validation, Celery + Redis setup | ShambaOracle.sol, CarbonCredit.sol, 10 unit tests written | Agent: FarmerRegistration + MapPolygon screens, Room schema complete | Android Keystore setup, certificate pinning configured |
| **Week 3** | Sentinel-2 NDVI pipeline, CHIRPS integration, drought classifier (scikit-learn) | IPFS upload pipeline via Pinata, Web3.py backend integration test | Agent: CameraX evidence photos, WorkManager offline sync | Oracle key management — environment secret setup |
| **Week 4** | Oracle signing service, IPFS report generation, M-Pesa STK Push + B2C | Pool coverage ratio enforcement, reentrancy guards, access control modifiers | Farmer: PolicyScreen + STK Push flow, PayoutHistoryScreen | Smart contract static analysis with Slither (Week 4 milestone) |

### Month 2 — Integrate, Harden, Demo

| Week | Backend/ML | Blockchain | Android | Security |
|---|---|---|---|---|
| **Week 5** | Integration Checkpoint 1: Android ↔ Backend farm sync confirmed end-to-end | Oracle ↔ contract integration test on Mumbai | FarmerDashboard + EarlyWarning screens consuming live backend data | API security review — OWASP API Top 10 checklist |
| **Week 6** | Carbon credit service, 14-day forecast model, Africa's Talking SMS integration | Integration Checkpoint 2: Python oracle → smart contract → payout on Mumbai | Admin screens: FarmMap, DroughtMonitor, PoolHealth. FCM push notifications | Penetration test on staging API |
| **Week 7** | Real Kenyan GPS farm data loaded, performance testing, Daraja Go-Live application | Integration Checkpoint 3: Full journey — register → pay → drought → payout → IPFS link live | Demo seed data, UI polish, real Meru polygon loaded | Android security review — OWASP Mobile Top 10. Final sign-off. |
| **Week 8** | Bug fixes only. No new features. | Bug fixes only. No new features. | Demo rehearsal — 3-minute script x10. No new features. | Final security report document produced |

---

## 14. MVP Scope vs Post-MVP

### ✅ In MVP Scope (Weeks 1–8)

- Android app: Admin + Agent + Farmer screens as specified
- Satellite drought detection (Sentinel-2 + CHIRPS)
- 14-day early warning SMS in Kiswahili
- Parametric insurance — tiered premiums via M-Pesa STK Push
- Automatic payout via smart contract oracle (Polygon Mumbai → testnet demo)
- Carbon credit minting pipeline (ERC-1155, testnet demo)
- IPFS evidence storage for all verifications
- Africa's Talking SMS + Firebase FCM notifications
- Offline-first Android with WorkManager sync
- Full security baseline (Android Keystore, certificate pinning, oracle signing, Slither)

### ⏳ Post-MVP (Phase 2 — After Hackathon)

- Polygon mainnet deployment (production)
- Daraja Go-Live approval (production M-Pesa real transactions)
- ACRE Africa / APA Insurance reinsurance partnership agreement
- WhatsApp Business API notification channel
- IRA Kenya micro-insurance license application
- Carbon credit marketplace (buyer-facing web portal)
- Multi-language support (Kikuyu, Luo, Kalenjin)
- iOS application
- Crop insurance (loss of harvest) as a second product layer
- Verra VM0042 carbon methodology application (18-month process)

---

## 15. Business Model Summary

### Insurance Revenue (Day 1)

| Tier | Monthly Premium | Drought Payout Coverage | Target Farmer |
|---|---|---|---|
| Tier 1 | KES 50/month | KES 2,000 | Subsistence farmer, first-time insured |
| Tier 2 | KES 150/month | KES 8,000 | Small commercial farmer |
| Tier 3 | KES 400/month | KES 25,000 | Cooperative member, multi-acre |

**Platform fee:** 20% of all premiums → operations + reinsurance reserve  
**Mutual pool:** 80% of all premiums → farmer payout reserve

### Where Payouts Come From

At early stage, Shamba Guard operates as the **technology layer** — not the licensed insurer. Payouts come from:

1. **Farmer premiums in the mutual pool** — first line of coverage
2. **Reinsurance partner (ACRE Africa / APA)** — backstops pool deficits above coverage threshold
3. **Smart contract coverage ratio rule** — new policy issuance automatically pauses if pool ratio drops below 150%, preventing insolvency

**Path to owning the float:** After 12 months of proven claims accuracy and pool management, apply for IRA Kenya micro-insurance license (~KES 1M). At that point, Shamba Guard holds and manages the pool directly.

### Revenue Projections

```
1,000 farmers × KES 100 avg = KES 100,000/month
Platform fee (20%)          = KES 20,000 MRR (~$154)

10,000 farmers              = KES 200,000 MRR (~$1,540)
100,000 farmers             = KES 2,000,000 MRR (~$15,400)

Additional streams:
- Carbon credit commission (10%): Active at Month 12+
- Lender intelligence API ($2–5/query): Active at Month 3+
- Reinsurer data licensing: Active at Month 9+
```

---

## 16. Definition of Done

A feature is **done** when all of the following are true:

```
[ ] Code is reviewed and approved by at least one other team member
[ ] Unit tests written and passing
[ ] Works on offline Android device (for agent/farmer features)
[ ] No API keys or secrets committed to Git
[ ] Security Engineer has reviewed any feature that touches payments, auth, or oracle
[ ] Integrated with adjacent layer (not just isolated unit test)
[ ] Demo can be run on a physical Android device without errors
```

A sprint milestone is **done** when:

```
[ ] All P0 requirements for that week are complete
[ ] Integration checkpoint (if applicable) passes end-to-end
[ ] No blocking issues outstanding for the next week's work
[ ] All team members have pulled and built the latest code without errors
```

The project is **demo-ready** when:

```
[ ] 3-minute demo script runs without errors on a physical Android device
[ ] Real Kenyan GPS coordinates used for demo farm polygon
[ ] Drought trigger demo fires on Polygon Mumbai with confirmed tx hash
[ ] IPFS verification report link is live and opens in browser
[ ] M-Pesa sandbox payout notification received on demo device
[ ] Slither analysis shows zero critical smart contract findings
[ ] All P0 security requirements verified by Security Engineer
```

---

> *Built by a team that believes the best climate technology is technology that pays farmers to protect the land they already love.*

---

**Shamba Guard** | *Your farm, watched from space. Protected by code.*  
`shambaguard.co.ke` | `@ShambGuard`