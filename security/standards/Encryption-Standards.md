# Encryption Standards

## 1. Purpose

Define encryption requirements for protecting sensitive data.

## 2. Scope

Applies to data at rest and data in transit.

## 3. Standard Requirements

### Data in Transit

- HTTPS shall be enforced for all communications.
- TLS 1.2 or higher shall be used.

### Data at Rest

- Sensitive data shall be encrypted using industry-standard algorithms.

### Password Storage

- Passwords shall be hashed using bcrypt or Argon2. 

### Key Management

- Encryption keys shall be securely stored and rotated periodically.
- Keys shall never be hardcoded in source code.

## 4. Verification

- Security testing
- Configuration audits

## 5. Exceptions

Require Security Team approval.

## 6. Review and Maintenance

Reviewed annually.