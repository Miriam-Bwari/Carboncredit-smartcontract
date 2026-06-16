# Authentication Flow

## Login Process

1. User submits credentials.
2. Credentials are validated.
3. JWT token is generated.
4. Token is returned to the client.
5. Client includes token in API requests.

## User Roles

### Farmer
- Farm management access
- Crop recommendations
- Carbon credit tracking

### Agent
- Farmer support
- Field verification
- Data collection

### Administrator
- User management
- Analytics access
- System administration

## Session Controls

- Token expiration enforced.
- Expired tokens require re-authentication.
- Invalid tokens are rejected.