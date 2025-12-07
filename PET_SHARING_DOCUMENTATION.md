# Pet Sharing Feature Documentation

## Overview
The pet sharing feature allows users to securely share access to their pets with other users via time-limited, single-use or multi-use share links.

## Security Features

### 1. **Cryptographically Secure Tokens**
- Uses `SecureRandom` with 32 bytes (256 bits) of entropy
- Tokens are URL-safe Base64 encoded
- Practically impossible to guess or brute-force

### 2. **Time-Limited Links**
- All share links have configurable expiration (1-168 hours / 7 days max)
- Automatic validation on each use
- Expired links are automatically rejected

### 3. **Usage Limits**
- Optional max uses per link (1-100)
- Tracks current usage count
- Automatically deactivates when limit is reached

### 4. **Access Control**
- Only pet owners can create share links
- Only the creator can revoke a link
- Users cannot share pets they don't own
- Prevents duplicate access grants

### 5. **Automatic Cleanup**
- Scheduled task runs daily at 2 AM
- Removes expired/inactive tokens older than 7 days
- Keeps database clean and performant

## API Endpoints

### Create Share Link
```
POST /api/users/{userId}/pets/{petId}/share
```

**Request Body:**
```json
{
  "expirationHours": 24,
  "maxUses": 5
}
```

**Response:**
```json
{
  "token": "xY9zK3mN7pQ1rS5tU8vW2aB4cD6eF0gH",
  "shareUrl": "http://localhost:8080/api/share/xY9zK3mN7pQ1rS5tU8vW2aB4cD6eF0gH",
  "expiresAt": "2025-12-08T15:30:00",
  "maxUses": 5,
  "currentUses": 0
}
```

### Accept Share Link
```
POST /api/users/{userId}/share/{token}/accept
```

**Response:**
```json
{
  "success": true,
  "message": "Successfully added pet to your account",
  "pet": {
    "id": 1,
    "name": "Buddy",
    "kind": "Dog",
    "breed": "Golden Retriever"
  }
}
```

### Revoke Share Link
```
DELETE /api/users/{userId}/share/{token}
```

**Response:** 204 No Content

### Get Active Share Links for a Pet
```
GET /api/users/{userId}/pets/{petId}/share
```

**Response:**
```json
[
  {
    "id": 1,
    "token": "xY9zK3mN7pQ1rS5tU8vW2aB4cD6eF0gH",
    "expiresAt": "2025-12-08T15:30:00",
    "maxUses": 5,
    "currentUses": 2,
    "isActive": true,
    "createdAt": "2025-12-07T15:30:00"
  }
]
```

### Get All User's Share Links
```
GET /api/users/{userId}/share
```

## Database Schema

### pet_share_token Table
```sql
CREATE TABLE pet_share_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    pet_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES user_account(id) ON DELETE CASCADE
);
```

### Indexes
- `idx_pet_share_token_token` - Fast token lookup
- `idx_pet_share_token_pet_id` - Query links by pet
- `idx_pet_share_token_created_by` - Query links by creator
- `idx_pet_share_token_active` - Efficient cleanup queries

## Usage Examples

### 1. Share a Pet for 24 Hours (Single Use)
```bash
curl -X POST http://localhost:8080/api/users/1/pets/5/share \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "expirationHours": 24,
    "maxUses": 1
  }'
```

### 2. Share a Pet for 1 Week (Unlimited Uses)
```bash
curl -X POST http://localhost:8080/api/users/1/pets/5/share \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "expirationHours": 168
  }'
```

### 3. Accept a Share Link
```bash
curl -X POST http://localhost:8080/api/users/2/share/xY9zK3mN7pQ1rS5tU8vW2aB4cD6eF0gH/accept \
  -H "Authorization: Bearer <jwt-token>"
```

### 4. Revoke a Share Link
```bash
curl -X DELETE http://localhost:8080/api/users/1/share/xY9zK3mN7pQ1rS5tU8vW2aB4cD6eF0gH \
  -H "Authorization: Bearer <jwt-token>"
```

## Configuration

Add to your environment variables or `application.properties`:

```properties
# Base URL for share links (frontend redirect URL)
SHARE_BASE_URL=https://yourapp.com/share
```

This allows you to customize the share URL format. The frontend can then extract the token and make the accept API call.

## Frontend Integration Example

### 1. Generate Share Link (Owner)
```javascript
const createShareLink = async (petId, expirationHours, maxUses) => {
  const response = await fetch(`/api/users/${userId}/pets/${petId}/share`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ expirationHours, maxUses })
  });
  
  const data = await response.json();
  // Copy data.shareUrl to clipboard or display to user
  navigator.clipboard.writeText(data.shareUrl);
};
```

### 2. Accept Share Link (Recipient)
```javascript
// When user visits /share/{token}
const acceptShare = async (token) => {
  const response = await fetch(`/api/users/${userId}/share/${token}/accept`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${userToken}`
    }
  });
  
  const result = await response.json();
  if (result.success) {
    // Redirect to pet details page
    window.location.href = `/pets/${result.pet.id}`;
  } else {
    // Show error message
    alert(result.message);
  }
};
```

## Security Best Practices

1. **Always use HTTPS in production** - Prevents token interception
2. **Implement rate limiting** - Prevents brute-force attacks
3. **Require authentication** - All endpoints require valid JWT
4. **Validate ownership** - Verify user owns pet before sharing
5. **Audit logging** - Consider logging share link creation/usage
6. **Short expiration times** - Default to 24 hours or less
7. **Limit max uses** - Consider setting a reasonable default (e.g., 5)

## Migration Steps

1. Ensure your database is running
2. The Flyway migration `V5__create_pet_share_token_table.sql` will run automatically on next application start
3. Restart your application
4. The feature is now available!

## Testing Checklist

- [ ] Create share link as pet owner
- [ ] Accept share link as different user
- [ ] Verify pet appears in recipient's pet list
- [ ] Attempt to accept already-used single-use link (should fail)
- [ ] Attempt to accept expired link (should fail)
- [ ] Revoke active share link
- [ ] Attempt to accept revoked link (should fail)
- [ ] Attempt to create share link for pet you don't own (should fail)
- [ ] Attempt to revoke someone else's share link (should fail)
- [ ] Verify cleanup job runs (wait for scheduled time or trigger manually)
