# SmartRoute - Indoor Campus Wayfinding System

Enterprise-Grade Java 17 Spring Boot Backend for Multi-Floor Campus Indoor Navigation.

---

## 1. Project Title & Overview
**SmartRoute** is a production-ready indoor wayfinding backend designed for multi-building, multi-floor corporate and university campuses. It models campus infrastructure as a directed weighted graph, providing shortest-path routing, turn-by-turn navigation instructions, dynamic constraint policy filtering (wheelchair accessibility, time closures, congestion multipliers), nearest POI search, and multi-stop navigation.

---

## 2. Technical Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.3 (Spring Web, Spring Security, Spring Data JPA, Spring Actuator)
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Security**: JWT (jjwt 0.12.5), BCrypt Password Hashing
- **Observability**: Micrometer, Prometheus, Actuator, SLF4J
- **API Spec**: OpenAPI 3.0, Swagger UI
- **Build & Containers**: Maven 3.9, Docker, Docker Compose
- **Testing**: JUnit 5, Mockito

---

## 3. High-Level Architecture & Policy Engine
SmartRoute enforces strict layered separation of concerns:
- **`controller`**: REST API endpoints, input validation, DTO mapping.
- **`service`**: Transaction management, Redis caching, graph reloading.
- **`algorithm`**: Pure in-memory graph models (`Graph`, `Node`, `Edge`), Dijkstra algorithm solver with `PriorityQueue`.
- **`policy`**: Extensible Chain of Responsibility engine (`AccessibilityPolicy`, `TimeWindowPolicy`, `SecurityPolicy`, `CongestionPolicy`).
- **`repository` & `entity`**: PostgreSQL JPA persistence entities decoupled from domain graph traversal.

```
+-----------------------------------------------------------------------+
|                         REST API Layer (Controllers)                  |
|          AuthController | RouteController | AdminController           |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                           Service Layer                               |
|        RouteService | CacheService | AuthService | AdminService        |
+-----------------------------------------------------------------------+
                                   |
            +----------------------+----------------------+
            |                                             |
            v                                             v
+-----------------------+                    +--------------------------+
| Domain Algorithm Layer|                    |    Persistence Layer     |
| DijkstraRoutingAlgo   |                    | Spring Data Repositories |
| PolicyChain Evaluator |                    | JPA Entities             |
| InstructionGenerator  |                    | PostgreSQL Database      |
+-----------------------+                    +--------------------------+
            |                                             |
            +----------------------+----------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                    Infrastructure & Cross-Cutting                     |
|           Redis Cache | JWT Security | Micrometer Metrics             |
+-----------------------------------------------------------------------+
```

---

## 4. Graph Model & Algorithm Complexity

### Time Complexity: $O((V + E) \log V)$
- **Min-Heap Extractions**: Extracting the minimum cost node takes $O(\log V)$, executed at most $V$ times $\to O(V \log V)$.
- **Edge Relaxations**: Traversal and updating candidate neighbor distances takes $O(\log V)$ per edge, executed at most $E$ times $\to O(E \log V)$.
- **Total Time Complexity**: $O((V + E) \log V)$.

### Space Complexity: $O(V + E)$
- Adjacency list graph storage requires $O(V + E)$ memory.
- PriorityQueue and distance maps require $O(V)$ memory.

---

## 5. Caching & Cache Invalidation Strategy
Routes are cached in Redis using a version-aware key:
`route:v{graphVersion}:start_{startId}:dest_{destId}:wheel_{wheelchair}:pref_{preference}:cong_{considerCongestion}:tb_{timeBucket}`

### Cache Invalidation Mechanism
PostgreSQL contains a single-row table `graph_versions` guarded by `CHECK (id = 1)`. When an admin creates or modifies a node or edge, PostgreSQL executes an atomic increment:
`UPDATE graph_versions SET version = version + 1 WHERE id = 1;`
The application reloads the active graph and increments its volatile `graphVersion` variable. Subsequent cache checks generate keys prefixed with `v2`, instantly invalidating all `v1` cache entries without requiring expensive Redis wildcard key scans.

---

## 6. Quick Start & Docker Deployment

### Prerequisites
- Docker & Docker Compose
- Java 17 & Maven (for local running without Docker)

### Run via Docker Compose
```bash
docker-compose up --build
```

The application will launch on `http://localhost:8080`.
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Actuator Health**: `http://localhost:8080/actuator/health`
- **Prometheus Metrics**: `http://localhost:8080/actuator/prometheus`

---

## 7. Sample API Requests & Credentials

### Default User Credentials
- **Standard User**: Username: `user`, Password: `password`
- **Admin User**: Username: `admin`, Password: `adminpass`

### Sample Auth Login Request
`POST /api/v1/auth/login`
```json
{
  "username": "user",
  "password": "password"
}
```

### Sample Route Request
`POST /api/v1/routes`
`Authorization: Bearer <JWT_TOKEN>`
```json
{
  "startNodeId": "n1000000-0000-0000-0000-000000000001",
  "destinationNodeId": "n2000000-0000-0000-0000-000000000003",
  "wheelchairRequired": true,
  "requestedTime": "14:30:00",
  "preference": "FASTEST_ROUTE",
  "considerCongestion": true
}
```

### Sample Turn-by-Turn Route Response
```json
{
  "status": "SUCCESS",
  "totalDistanceMeters": 55.0,
  "totalEstimatedTimeSeconds": 49,
  "wheelchairAccessible": true,
  "path": [
    {
      "step": 1,
      "fromNodeName": "Reception",
      "toNodeName": "Corridor A",
      "instruction": "Start at Reception and walk along Corridor A for 15 meters",
      "distanceMeters": 15.0,
      "travelTimeSeconds": 12,
      "floorName": "Floor 1",
      "buildingName": "Main Academic Block"
    },
    {
      "step": 2,
      "fromNodeName": "Lift L1 (F1)",
      "toNodeName": "Lift L1 (F2)",
      "instruction": "Take Lift L1 (F1) to Floor 2 (Lift L1 (F2))",
      "distanceMeters": 10.0,
      "travelTimeSeconds": 25,
      "floorName": "Floor 2",
      "buildingName": "Main Academic Block"
    }
  ],
  "nodeIds": [
    "n1000000-0000-0000-0000-000000000001",
    "n1000000-0000-0000-0000-000000000002",
    "n1000000-0000-0000-0000-000000000005",
    "n2000000-0000-0000-0000-000000000002",
    "n2000000-0000-0000-0000-000000000003"
  ]
}
```
