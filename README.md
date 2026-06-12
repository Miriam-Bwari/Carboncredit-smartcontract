 HEAD
## Foundry

**Foundry is a blazing fast, portable and modular toolkit for Ethereum application development written in Rust.**

Foundry consists of:

- **Forge**: Ethereum testing framework (like Truffle, Hardhat and DappTools).
- **Cast**: Swiss army knife for interacting with EVM smart contracts, sending transactions and getting chain data.
- **Anvil**: Local Ethereum node, akin to Ganache, Hardhat Network.
- **Chisel**: Fast, utilitarian, and verbose solidity REPL.

## Documentation

https://book.getfoundry.sh/

## Usage

### Build

```shell
$ forge build
```

### Test

```shell
$ forge test
```

### Format

```shell
$ forge fmt
```

### Gas Snapshots

```shell
$ forge snapshot
```

### Anvil

```shell
$ anvil
```

### Deploy

```shell
$ forge script script/Counter.s.sol:CounterScript --rpc-url <your_rpc_url> --private-key <your_private_key>
```

### Cast

```shell
$ cast <subcommand>
```

### Help

```shell
$ forge --help
$ anvil --help
$ cast --help
```
<div align="center">

# Shamba Guard
**A Parametric Crop Insurance & Carbon Credit Platform for Smallholder Farmers**

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://www.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Framework](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![Backend](https://img.shields.io/badge/Backend-FastAPI-009688.svg)](https://fastapi.tiangolo.com/)
[![Contracts](https://img.shields.io/badge/Contracts-Solidity-363636.svg)](https://soliditylang.org/)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](docs/CONTRIBUTING.md)

*Protecting yields, predicting droughts, and rewarding sustainable farming*

[Architecture](#project-architecture) • [User Interfaces](#user-interfaces) • [Backend Setup](#backend-local-setup) • [Roadmap](#roadmap) • [Contributing](#contributing)

</div>

Shamba Guard is an end-to-end agricultural platform designed to provide automated, oracle-triggered insurance payouts for farmers in Kenya based on satellite-verified drought metrics. It eliminates the need for manual claim processing and empowers farmers to generate carbon credits through sustainable farming practices.



## Project Architecture
This repository is a full-stack monorepo containing the Android client application, the Python backend services, and the decentralized smart contracts.

### Frontend (Android)
- **Tech Stack:** Kotlin, Jetpack Compose, Material 3, Dagger-Hilt, Room, WorkManager, Retrofit.
- **Key Features:** Offline-first syncing for low-connectivity areas, Google Maps integration for precise farm polygon drawing, CameraX with EXIF metadata for secure evidence capture, and M-Pesa STK Push integration.
- **Documentation & Setup:** Please see the [Android Client README](./android/ShambaGuard/README.md) for detailed prerequisites, architecture documentation, and setup instructions.

### Backend (FastAPI)
- **Tech Stack:** Python, FastAPI, SQLAlchemy, MySQL, Celery, Redis.
- **Key Features:** Secure JWT role-based authentication (Admin, Agent, Farmer), geospatial data processing (GeoJSON polygons), Celery pipelines for background NDVI calculation (via Sentinel-2 APIs), and automated smart contract payout triggers.
- **Documentation:** Review the [FullStack Product Requirements Document](./docs/FullStack_prd.md) for detailed API contracts and system workflows.

### Blockchain & Smart Contracts (Coming Soon)
- **Tech Stack:** Solidity, Polygon (Matic) Network, Chainlink Oracles, IPFS.
- **Key Features:** 
  - **Parametric Insurance Contracts:** Immutable smart contracts holding pool liquidity.
  - **Oracle Triggers:** Integration with decentralized oracles (e.g., Chainlink) to feed verified weather/NDVI data directly into the smart contracts to trigger automated, trustless payouts without manual claims processing.
  - **Decentralized Storage:** Using IPFS for immutable storage of carbon reports and farm evidence photos.
  - **Tokenized Carbon Credits:** Future integration for minting verified carbon credits as NFTs or ERC-20 tokens on the blockchain.

### Security
- **Role-Based Access Control (RBAC):** Strict separation between Admins, Field Agents, and Farmers to prevent unauthorized access.
- **Data Integrity:** EXIF GPS metadata verification on all uploaded evidence photos to prevent spoofing.
- **Key Management:** No hardcoded secrets. Environment variables and secure Android Keystore implementations ensure API keys and JWTs are protected locally and in transit.



## User Interfaces

Below is a preview of the Shamba Guard application across different user roles:

*(Note: Replace the placeholder image links below with actual screenshots once available)*

### Farmer Dashboard & M-Pesa Payment
Demonstrating the intuitive interface for farmers to track their carbon credits, view weather forecasts, and securely pay for their parametric policy via M-Pesa.
```markdown
![Farmer Dashboard Placeholder](docs/images/farmer_dashboard_screenshot.png)
![M-Pesa Policy Screen Placeholder](docs/images/mpesa_policy_screenshot.png)
```

### Field Agent Offline Sync
Showcasing the agent onboarding flow, polygon mapping, and the robust offline-first synchronization queue.
```markdown
![Agent Mapping Placeholder](docs/images/agent_mapping_screenshot.png)
```

### Admin Pool Health Monitor
Highlighting the admin-exclusive overview of total active farmers, pool balances, and pending agent approvals.
```markdown
![Admin Dashboard Placeholder](docs/images/admin_dashboard_screenshot.png)
```



## Backend Local Setup

To run the FastAPI backend locally:

1. **Navigate to the backend directory:**
   ```bash
   cd backend
   ```
2. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```
3. **Configure Environment:**
   Copy the `.env.example` file to create your own `.env` file in the `backend` directory. Fill in your MySQL database URL, Daraja M-Pesa keys, and other external API keys.
4. **Start the server:**
   ```bash
   uvicorn main:app --reload
   ```



## Roadmap

- **Phase 1: MVP Release (Current)** 
  - Android application for Farmers, Agents, and Admins.
  - FastAPI backend with geospatial polygon support and role-based authentication.
- **Phase 2: M-Pesa & Celery Integration**
  - Live STK Push payments for parametric policy premiums.
  - Automated background NDVI health processing via Sentinel-2.
- **Phase 3: Decentralization & Smart Contracts**
  - Deploy liquidity pool smart contracts to Polygon (Matic).
  - Integrate Chainlink Oracles to trigger trustless payouts automatically based on drought index thresholds.
  - Mint tokenized carbon credits.

## Contributing

We welcome contributions from the community! If you're interested in improving Shamba Guard, whether by fixing bugs, enhancing the Android UI, or writing smart contracts:

1. Check the [FullStack PRD](./docs/FullStack_prd.md) for architectural guidelines.
2. Open an issue to discuss your proposed changes.
3. Submit a Pull Request targeting the `main` branch.

## License & Contact
Copyright 2026 Shamba Guard. All Rights Reserved.

For inquiries, partnerships, or support, please reach out to the **Shamba Guard Team**.
 b3ad03a889e8042a9213a9b7202bfbf28bca301b
