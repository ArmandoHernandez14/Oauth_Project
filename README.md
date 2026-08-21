# OAuth Server

A Spring Boot OAuth/JWT authentication service with local JSON file storage — built as a learning project, deployable to a Raspberry Pi K3s cluster.

> **Storage note:** Users are stored in `src/main/resources/data/users.json`. This works well at `replicas: 1`, but is **not safe to scale** past a single replica — each pod would get its own independent copy of the file. See [Known Limitations](#known-limitations).

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.1** (Web, Security, Validation)
- **JJWT 0.12.6** (`io.jsonwebtoken`) for JWT signing/validation
- **Jackson** for JSON file read/write and (de)serialization
- **Docker** (multi-arch: amd64 + arm64)
- **Kubernetes / K3s** (tested on a Raspberry Pi cluster)

---

## Project Structure

```
src/main/java/com/example/oauthserver/
├── OauthServerApplication.java
├── config/
│   ├── JacksonConfig.java            # ObjectMapper bean
│   └── PasswordConfig.java           # PasswordEncoder bean (BCrypt)
├── entity/
│   └── User.java                     # id, username, password, role
├── repository/
│   └── FileUserRepository.java       # Reads/writes data/users.json
├── service/
│   └── UserService.java              # Registration logic, password hashing
├── security/
│   ├── SecurityConfig.java           # Filter chain, permitAll rules, stateless sessions
│   ├── JwtAuthenticationFilter.java  # Validates Bearer tokens per-request
│   ├── JwtService.java               # Token generation/validation
│   ├── JwtProperties.java            # Binds jwt.* properties
│   ├── RefreshTokenService.java      # Refresh token generation/validation
│   └── CustomUserDetailsService.java # Loads users for Spring Security
└── controller/
    ├── AuthController.java           # /auth/register
    ├── LoginController.java          # /auth/login, /auth/refresh
    └── ProtectedApiController.java   # /api/hello, /api/me (requires a valid token)

src/main/resources/
├── application.properties
└── data/users.json                   # User storage (created automatically on first run)
```

---

## API Endpoints

### Register
```
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "yourpassword"
}
```
Passwords are hashed with BCrypt (`PasswordConfig`) before being stored — never stored in plain text. New users are assigned `ROLE_USER` by default.

### Login
```
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "yourpassword"
}
```
Response:
```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "john"
}
```

### Refresh
```
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "..."
}
```

### Protected endpoints
Require `Authorization: Bearer <accessToken>`:
```
GET /api/hello   → plain text welcome message
GET /api/me      → { "username": "...", "authorities": [...] }
```

All other routes not under `/auth/**` require a valid access token (`SecurityConfig` permits `/auth/**` and requires authentication for `anyRequest()`).

---

## Configuration

`application.properties`:
```properties
spring.application.name=oauth-server
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration

jwt.secret=ThisIsMyVeryLongSuperSecretKeyForJwtSigning1234567890
jwt.access-expiration=3600000
jwt.refresh-expiration=604800000
```

**Before deploying anywhere beyond your own machine**, make these environment-based instead of hardcoded, and use a real, strong secret:
```properties
jwt.secret=${JWT_SECRET:ThisIsMyVeryLongSuperSecretKeyForJwtSigning1234567890}
jwt.access-expiration=${JWT_ACCESS_EXPIRATION:3600000}
jwt.refresh-expiration=${JWT_REFRESH_EXPIRATION:604800000}
```

**Important:** `jwt.secret` must decode to at least 256 bits for HMAC-SHA algorithms, or the app throws `WeakKeyException` the first time a token is generated. Generate a safe one with:
```bash
openssl rand -base64 32
```

---

## Running Locally

```bash
mvn clean package
mvn spring-boot:run
```
The app starts on `http://localhost:8080`. `data/users.json` is created automatically on first run if it doesn't already exist.

---

## Building the Docker Image

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/oauth-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build the jar first:
```bash
mvn clean package
```

### Multi-arch build (required for Raspberry Pi / ARM64)

If building from an amd64 machine and deploying to ARM64 nodes:
```bash
docker buildx create --use --name multiarch-builder

docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t <your-dockerhub-username>/oauth-server:1.0 \
  --push .
```
Make sure you're logged in (`docker login`) and use your **real** Docker Hub username, not a placeholder — the image reference in your Deployment YAML must match exactly.

---

## Deploying to Kubernetes (K3s)

### 1. Create the JWT Secret
```bash
kubectl create secret generic oauth-secrets \
  --from-literal=JWT_SECRET='<a-real-256-bit-secret>' \
  --from-literal=JWT_ACCESS_EXPIRATION='3600000' \
  --from-literal=JWT_REFRESH_EXPIRATION='604800000'
```

### 2. Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth-server
spec:
  replicas: 1
  selector:
    matchLabels:
      app: oauth-server
  template:
    metadata:
      labels:
        app: oauth-server
    spec:
      containers:
        - name: oauth-server
          image: <your-dockerhub-username>/oauth-server:1.0
          ports:
            - containerPort: 8080
              name: http
          envFrom:
            - secretRef:
                name: oauth-secrets
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
              cpu: "500m"
```

### 3. Service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: oauth-service
spec:
  selector:
    app: oauth-server
  ports:
    - name: http
      port: 8080
      targetPort: 8080
  type: NodePort
```
`NodePort` exposes the service on every node's own IP (needed for LAN/off-cluster access). Use `ClusterIP` if it will only ever be called from inside the cluster.

### 4. Apply and verify
```bash
kubectl apply -f oauth-deployment.yaml
kubectl apply -f oauth-service.yaml

kubectl get pods -o wide
kubectl get svc oauth-service
kubectl logs -l app=oauth-server
```

---

## Accessing the Service

**Inside the cluster:** `oauth-service:8080` (ClusterIP + internal DNS)

**On your local network:**
```
http://<pi-lan-ip>:<nodeport>/auth/register
```
Get the LAN IP with `hostname -I` on the Pi, and the NodePort with `kubectl get svc oauth-service`.

**From outside your network**, roughly in order of setup effort:
- **Cloudflare Tunnel** — `cloudflared tunnel --url http://localhost:<nodeport>` gives an instant public HTTPS URL, no router config or domain required
- **Tailscale** — private mesh network; install on the Pi and any client device (note: on macOS, client apps like Bruno need Local Network permission granted under System Settings → Privacy & Security)
- **Router port forwarding** — exposes the Pi directly to the internet; avoid without TLS in front of it

---

## Checking Stored Users

```bash
kubectl exec -it $(kubectl get pods -l app=oauth-server -o jsonpath='{.items[0].metadata.name}') -- cat data/users.json
```

---

## Known Limitations

- **Single-replica only.** `users.json` lives inside each pod's own filesystem — with `replicas: 2+`, each pod keeps an independent copy, so a user registered on one pod won't be recognized by another. Migrate to a shared datastore (e.g. PostgreSQL) before scaling.
- **No HTTPS/TLS.** The app serves plain HTTP. Any external exposure should terminate TLS in front of it (Cloudflare Tunnel does this automatically; raw port forwarding does not).
- **403 vs. 401 on failed auth.** There's no custom `AuthenticationEntryPoint`, so Spring Security's stateless default returns `403 Forbidden` on authentication failures rather than the more conventional `401 Unauthorized`.
- **Malformed/expired tokens on `JwtAuthenticationFilter`** should be caught explicitly (`io.jsonwebtoken.JwtException`, `UsernameNotFoundException`) so a bad token on a public route doesn't throw and get reported as a 403 — verify this is in place in `JwtAuthenticationFilter` before relying on it with real client traffic.
