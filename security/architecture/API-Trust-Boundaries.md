# API Trust Boundaries

## External Boundary

Internet users communicate with APIs through HTTPS.

## Application Boundary

Backend services validate:

- Authentication
- Authorization
- Input validation

## Database Boundary

Only authorized backend services may access databases.

## Blockchain Boundary

Only approved smart contract interactions are permitted.

## Monitoring Boundary

Security events crossing trust boundaries shall be logged.