# FreshSupply – Food Management System

A small food-distribution back office (think a tiny Sysco): manage a **product catalog**, **suppliers**,
**customers** and **orders**, with automatic stock control and tier-based discounts.
Built to refresh Spring Boot knowledge – clean layering, OOP, dependency injection and a few classic design patterns.

| Layer    | Tech                                                         |
|----------|--------------------------------------------------------------|
| Backend  | Java 21, Spring Boot 3.3, Spring Data JPA (Hibernate), Bean Validation |
| Database | SQLite (single file `fms.db`, created automatically)         |
| Frontend | React 18, Vite 5, React Router 6 (no UI framework)           |

## Prerequisites

- **JDK 21** (`java -version`)
- **Maven 3.9+** (`mvn -v`)
- **Node.js 18+** and npm (`node -v`)

## Run it

Open two terminals.

**1. Backend** (http://localhost:8080)

```bash
cd backend
mvn spring-boot:run
```

On first start it creates `backend/fms.db` and loads demo data (4 suppliers, 10 products, 3 customers, 1 order).

**2. Frontend** (http://localhost:5173)

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The Vite dev server proxies `/api/*` to the backend.

To start with a clean database, stop the backend and delete `backend/fms.db`.

## Run the tests

```bash
cd backend
mvn test
```

Includes unit tests (discount strategies, order state rules) and an end-to-end test that drives the REST API against a real in-memory SQLite database.

## Production-style build (optional)

```bash
cd backend && mvn clean package          # -> target/fms-backend-1.0.0.jar
java -jar target/fms-backend-1.0.0.jar
cd ../frontend && npm run build           # -> frontend/dist (static files)
```

## Project structure

```
backend/src/main/java/com/freshsupply/fms
├── controller/   REST endpoints (thin; only HTTP concerns)
├── service/      Business use-cases (@Transactional); OrderService is an interface + impl
├── domain/       JPA entities with behaviour (Product stock rules, Order lifecycle)
├── repository/   Spring Data JPA interfaces
├── dto/          Java records for requests/responses (entities never leave the service layer)
├── mapper/       Entity <-> DTO conversion
├── pricing/      Discount strategies + factory
├── event/        Low-stock domain event + listener
├── exception/    Domain exceptions + @RestControllerAdvice
└── config/       CORS, demo data seeder
frontend/src
├── api/          fetch wrapper + one module per resource
├── hooks/        useAsync (loading / error / reload)
├── components/   Layout, Modal, Field, StatusBadge, ErrorBanner …
└── pages/        Dashboard, Products, Orders, NewOrder, Customers, Suppliers
```

## Concepts to look at

| Concept | Where |
|---|---|
| **Dependency Injection** (constructor injection everywhere, no field `@Autowired`) | every `@Service`, `@RestController`, `OrderServiceImpl` |
| **Programming to an interface** | `OrderService` / `OrderServiceImpl`; controller depends on the interface |
| **Strategy** | `pricing/DiscountStrategy` + `Standard/Silver/GoldDiscountStrategy` |
| **Factory** (Spring injects `List<DiscountStrategy>`; Open/Closed: add a tier = add a class) | `DiscountStrategyFactory` |
| **Builder** | `Product.Builder` |
| **Static factory method** | `Order.createFor(customer)` |
| **State (enum-based)** | `OrderStatus.canTransitionTo`, `Order.transitionTo` |
| **Observer** (Spring application events) | `LowStockEvent` → `LowStockAlertListener` |
| **Repository / DTO / Mapper** | `repository/`, `dto/`, `mapper/` |
| **Rich domain model / encapsulation** | `Product.decreaseStock`, `Order.addItem` |
| **Centralised error handling** | `GlobalExceptionHandler` |

## Business rules

- Placing an order reserves stock; insufficient stock → `409 Conflict`.
- Customer tier discounts: Standard 0 %, Silver 5 %, Gold 10 %.
- Order lifecycle: `PENDING → CONFIRMED → SHIPPED → DELIVERED`; `PENDING`/`CONFIRMED` can be `CANCELLED` (stock is returned).
- Order lines snapshot product name and price, so later price changes do not rewrite history.
- A product at or below its reorder level is flagged "low stock" and logs a warning when an order drops it there.
- Suppliers, customers and products that are still referenced cannot be deleted.

## REST API

| Method | Path | Description |
|---|---|---|
| GET | `/api/dashboard` | Counts, low-stock count, revenue |
| GET/POST | `/api/products` | List (`?search=`, `?lowStock=true`) / create |
| GET/PUT/DELETE | `/api/products/{id}` | Read / update / delete |
| POST | `/api/products/{id}/restock` | `{ "quantity": 10 }` |
| GET/POST | `/api/suppliers`, `/api/customers` | List / create |
| GET/PUT/DELETE | `/api/suppliers/{id}`, `/api/customers/{id}` | Read / update / delete |
| GET/POST | `/api/orders` | List / place `{ "customerId": 1, "items": [{ "productId": 3, "quantity": 5 }] }` |
| GET | `/api/orders/{id}` | Order with lines |
| PATCH | `/api/orders/{id}/status` | `{ "status": "CONFIRMED" }` |

Quick check:

```bash
curl http://localhost:8080/api/products
curl -X POST http://localhost:8080/api/orders -H 'Content-Type: application/json' \
     -d '{"customerId":1,"items":[{"productId":1,"quantity":2}]}'
```

## Ideas to extend it (good practice)

- Add Spring Security + JWT and roles (admin / sales).
- Add pagination and sorting (`Pageable`) to the list endpoints.
- Replace `ddl-auto: update` with Flyway migrations.
- Add a `PricingRule` for bulk discounts as another `DiscountStrategy`.
- Containerise with a `Dockerfile` / `docker-compose.yml`.

## Troubleshooting

- **Port 8080 or 5173 already in use** – change `server.port` in `application.yml` or `server.port` in `vite.config.js` (and the proxy target).
- **`database is locked`** – make sure only one backend instance uses `fms.db`.
- **Frontend shows "Failed to fetch"** – the backend is not running on port 8080.
