# Harmoni POS Menu Service

Catalogue and configuration service for the Harmoni POS platform. It owns the
brand → chain → store hierarchy, the menu (categories, products, SKUs,
customizations), price tiers, promotions, tables/floors, services and kitchen
routing. It also answers the order service's internal questions: "is this basket
still sellable?" and "what does this basket cost after promotions?".

The service is reached through the **gateway**, which validates the JWT and
forwards the caller identity (`X-Username`, `X-Roles`, `X-Permissions`,
`X-Scopes`). Internal endpoints under `/internal/**` are for sibling services and
are reachable only on the internal network.

## Tech stack

| Concern | Choice |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.2.4 (Web MVC on Jetty, Security, Validation) |
| Persistence | MyBatis 3.5.9 + `mybatis-spring-boot-starter` 3.0.4, PageHelper |
| Database | MySQL (`db-harmoni-master`) |
| HTTP client | Spring WebFlux `WebClient` |
| Tokens | JJWT 0.12.5 |
| API docs | springdoc-openapi 2.6.0 (Swagger UI) |
| Encryption | `mybatis-encrypt-spring-boot-starter` (AES) |
| Images | ImgBB upload API |
| Build | Maven (`./mvnw` or `mvn`), GraalVM native plugin |

## Architecture

```
com.harmoni.pos
├── http
│   ├── controller        # REST controllers, grouped by module
│   ├── filter            # PermissionInterceptor (opt-in RBAC)
│   ├── handler           # @RestControllerAdvice exception handlers
│   ├── response          # RestAPIResponse
│   └── utils             # PaginationUtils
├── business.service
│   ├── brand, chain, store, category, product, sku, customization,
│   │   tier, promotion, table, floor, service, kitchen, user, ...
│   ├── promotion.engine  # PromotionEngine, rules, targets, stacking
│   ├── promotion.pricing # PromotionPricingService
│   ├── validation        # OrderItemValidationService
│   └── rest              # RestClientService (WebClient wrapper)
├── menu
│   ├── mapper            # MyBatis mapper interfaces
│   └── model             # entities, enums, DTOs
├── component             # SecurityConfig, HeaderAuthoritiesFilter,
│                         # CallerScope* (organisation scope)
├── config                # DbMenuSQLConfig, DbTransactionSQLConfig,
│                         # AuthUrlProperties, ImgbbProperties, JwtProperties,
│                         # RbacProperties, WebMvcConfig
├── exception             # business exceptions
└── transaction           # order-side models (reserved)
```

`DbMenuSQLConfig` is the primary datasource (`db-harmoni-master`).
`DbTransactionSQLConfig` is also configured (`db-harmoni-pos-transaction`) but
the order tables are owned by the order service; the menu service's mappers all
live under `com.harmoni.pos.menu.mapper`.

## Data model

The catalogue lives in `db-harmoni-master`.

| Area | Tables |
| --- | --- |
| Organisation | `brands`, `chains`, `stores`, `store_tiers` |
| Menu | `categories`, `products`, `skus`, `product_images` |
| Customizations | `customizations`, `customization_options`, `product_customizations`, `sku_customization_options` |
| Pricing | `tiers`, `tier_prices`, `sku_tier_prices`, `customization_option_tier_prices`, `tier_menus`, `tier_services` |
| Promotions | `promotions`, `promotion_rules`, `promotion_targets`, `promotion_scope`, `promotion_schedules`, `promotion_special_prices` |
| Operations | `floors`, `tables`, `services`, `sub_services`, `store_service_types` |
| Kitchen | `kitchen_stations`, `kitchen_printers`, `kitchen_category_routings`, `kitchen_product_routings` |
| Users | `users`, `roles` |

The `sql/` directory holds hand-applied migrations (`001`–`004`). There is no
migration tool; run them in order against `db-harmoni-master`. `004` drops the
retired `sku_stock` table. Device addresses are globally unique across live
devices; soft-deleted addresses can be reused. Apply
`database/migrations/2026_11_25_devices_global_address.sql` after
`2026_11_20_devices_address.sql`.

## Domain model

- **Organisation**: a brand owns chains; a chain owns stores. A store is the
  unit that sells, and its `store_tiers` row selects the price/menu/service tier
  it uses.
- **Menu**: a category belongs to a brand; a product belongs to a category; a
  product has one or more SKUs. Prices are per tier (`sku_tier_prices`), so the
  same SKU can cost different amounts at different stores.
- **Customizations**: a customization (e.g. "Temperature") has options (e.g.
  "Hot", "Iced"). Products link to customizations through
  `product_customizations`, with optional overrides for required/min/max
  selection. Option prices are per tier.
- **Tiers**: a tier is a named price/menu/service bundle (`PRICE`, `MENU`,
  `SERVICE`). `tier_menus` selects which categories a tier sells; `tier_services`
  selects which services it offers.
- **Promotions**: see below.

## Promotion engine

A promotion has a `promotion_type` and a lifecycle `status`:

| `PromotionType` | Driven by |
| --- | --- |
| `PERCENTAGE` | `promotion_rules` (`discount_value`) |
| `FIXED_AMOUNT` | `promotion_rules` (`discount_value`) |
| `SPECIAL_PRICE` | `promotion_special_prices` (per SKU) |
| `BUY_X_GET_Y` | rules + targets |
| `BUNDLE` | rules + targets |

`PromotionStatus`: `DRAFT`, `SCHEDULED`, `ACTIVE`, `PAUSED`, `EXPIRED`,
`CANCELLED`. Only `ACTIVE` promotions inside their date range and schedule
window are evaluated.

Supporting tables:

- `promotion_rules` — `PERCENTAGE`, `FIXED_AMOUNT`, `MAX_DISCOUNT_AMOUNT`,
  `MIN_QUANTITY`, `MIN_AMOUNT`.
- `promotion_targets` — `CATEGORY`, `PRODUCT` or `SKU` (most specific wins).
- `promotion_scope` — `BRAND`, `CHAIN` or `STORE` the promotion applies to.
- `promotion_schedules` — day-of-week and time window.
- `promotion_special_prices` — per-SKU override price.

`PromotionEngineImpl` evaluates in three stages:

1. **Eligibility** — status, date range, schedule and scope, once per promotion.
2. **Per line** — target match, `MIN_*` guards, then the discount.
3. **Resolution** — the `StackingPolicy` decides which competing discounts
   survive, `MAX_DISCOUNT_AMOUNT` is enforced across the order, and each line is
   clamped so a discount never exceeds the line value.

`StackingPolicy.EXCLUSIVE` (the default) keeps only the largest discount;
`STACKED` sums eligible discounts and scales them down when a cap bites.

`POST /api/v1/promotion/price` is a pure price request: it persists nothing and
returns one priced line per requested line plus the order-wide saving. The order
service calls it at checkout.

## Order validation

`POST /internal/v1/menu/validate-order-items` is the order service's pre-check.
The request carries the store and the basket lines (product, SKU, quantity,
customization choices). The response carries the catalogue's names and prices
for each line. Nothing is persisted and no total is produced; the order service
multiplies quantities and applies promotions. A basket that cannot be sold is
refused outright with the reason.

`GET /internal/v1/menu/scope/stores` returns `{ global, storeIds }` for the
caller, so the order service (which only stores store ids) can expand a
brand/chain scope without duplicating the hierarchy.

## Scope and RBAC

- `CallerScopeFilter` resolves `X-Scopes` once per request into brand, chain and
  store ids (`CallerScopeService` expands `BRAND`/`CHAIN`/`STORE` assignments).
  A missing header keeps the legacy unrestricted behaviour; a malformed header
  becomes an empty scope, never a wider one.
- Controllers use `CallerScopeContext.current()` to filter lists and to
  `requireBrand`/`requireChain`/`requireStore` before reads and writes.
- `HeaderAuthoritiesFilter` builds the `SecurityContext` from the gateway
  headers, and `@PreAuthorize` enforces the module permissions.
- `PermissionInterceptor` is an opt-in second layer: when
  `harmoni.menu.rbac.enforce=true`, it maps the request path to a module and the
  HTTP method to `read`/`write`/`delete`, then requires `module:action` in
  `X-Permissions`. It is disabled by default while RBAC rolls out.

## API reference

All endpoints require the gateway identity headers. The main groups:

### Organisation

| Method | Path | Authority |
| --- | --- | --- |
| `GET` | `/api/v1/organization/tree` | `organization:read`, `employee:view`, `rbac:manage` |
| `GET/POST` | `/api/v1/brand`, `/api/v1/brand/{id}` | `organization:read` / `organization:write` |
| `GET/POST` | `/api/v1/chain`, `/api/v1/chain/{id}`, `/api/v1/chain/brand/{id}` | `organization:read` / `organization:write` |
| `GET/POST` | `/api/v1/store`, `/api/v1/store/{id}` | `organization:read` / `organization:write` |
| `GET/POST` | `/api/v1/tier`, `/api/v1/tier/{id}`, `/api/v1/tier/brand/{brandId}` | `organization:read` / `organization:write` |
| `GET/PUT` | `/api/v1/tier/menu`, `/api/v1/tier/{tierId}/menu` | `organization:read` / `organization:write` |
| `GET/POST` | `/api/v1/tier/service`, `/api/v1/tier/{tierId}/service` | `organization:read` / `organization:write` |

### Menu

| Method | Path | Authority |
| --- | --- | --- |
| `GET/POST` | `/api/v1/category`, `/api/v1/category/{id}` | `menu:view` / `menu:edit` |
| `GET` | `/api/v1/category/brand/{brandId}`, `/api/v1/category/brand/{brandId}/page` | `menu:view` |
| `GET/POST` | `/api/v1/product`, `/api/v1/product/{id}` | `menu:view` / `menu:edit` |
| `GET` | `/api/v1/product/category/{id}`, `/api/v1/product/category/{id}/price` | `menu:view` |
| `GET` | `/api/v1/product/brand/{brandId}/tier/{tierId}/cursor` | `menu:view` |
| `GET/POST` | `/api/v1/sku`, `/api/v1/sku/price` | `menu:view` / `menu:edit` |
| `GET/POST` | `/api/v1/skutier` | `menu:view` / `menu:edit` |
| `GET/POST` | `/api/v1/customization`, `/api/v1/customization/{id}` | `menu:view` / `menu:edit` |
| `GET/PUT` | `/api/v1/product/{productId}/customization` | `menu:view` / `menu:edit` |
| `POST` | `/api/v1/customization/price` | `menu:view`, `catalog:read`, `order:view`, ... |
| `POST` | `/api/v1/product/image/upload`, `/api/v1/image/upload` | `menu:edit`, `catalog:write` |

### Promotions

| Method | Path | Authority |
| --- | --- | --- |
| `GET/POST` | `/api/v1/promotion` | `menu:view` / `menu:edit` |
| `GET` | `/api/v1/promotion/{id}`, `/api/v1/promotion/code/{code}`, `/api/v1/promotion/redeemable` | `menu:view` |
| `PUT` | `/api/v1/promotion`, `/api/v1/promotion/{id}/status` | `menu:edit` |
| `POST` | `/api/v1/promotion/price` | `menu:view`, `catalog:read`, `order:view`, ... |
| `DELETE` | `/api/v1/promotion/{id}`, `/api/v1/promotion` | `menu:edit`, `catalog:delete` |

### Operations

| Method | Path | Authority |
| --- | --- | --- |
| `GET/POST` | `/api/v1/floor`, `/api/v1/floor/{id}`, `/api/v1/floor/store/{storeId}` | `table:view` / `table:edit` |
| `GET/POST` | `/api/v1/table`, `/api/v1/table/{id}`, `/api/v1/table/store/{storeId}` | `table:view` / `table:edit` |
| `GET/POST` | `/api/v1/service`, `/api/v1/service/{id}` | `terminal:view` / `terminal:edit` |
| `GET/POST` | `/api/v1/stores/{storeId}/devices`, `/api/v1/devices/{id}` | `terminal:view` / `terminal:edit` |
| `GET` | `/api/v1/devices/configuration?address={address}` | `terminal:view`, `operation:read`, `rbac:manage` |
| `POST` | `/api/v1/subservice`, `/api/v1/storeservicetype` | `terminal:edit` |

The device configuration response includes the mode-specific fields plus
`deviceMode`, so clients can branch on `CASHIER_POS`, `TABLE_ORDER`,
`SELF_ORDER_KIOSK`, or `QUICK_ORDER`.
| `GET/POST` | `/api/v1/stores/{storeId}/kitchen-stations`, `/api/v1/kitchen-stations/{id}` | `availability:view` / `availability:edit` |
| `GET/POST` | `/api/v1/kitchen-stations/{stationId}/printers`, `/api/v1/kitchen-printers/{id}` | `availability:view` / `availability:edit` |
| `GET/POST` | `/api/v1/stores/{storeId}/kitchen-routings` | `availability:view` / `availability:edit` |

### Users

| Method | Path | Authority |
| --- | --- | --- |
| `POST` | `/api/v1/user` | `employee:edit`, `user:write`, `rbac:manage` |
| `GET` | `/api/v1/user/chain/{chainId}`, `/api/v1/user/page` | `employee:view`, `user:read`, `rbac:manage` |
| `GET` | `/api/v1/user/{username}` | `menu:view`, `catalog:read`, `organization:read`, ... |
| `PUT/DELETE` | `/api/v1/user/{id}` | `employee:edit`, `user:write` / `user:delete` |

### Internal

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/internal/v1/menu/validate-order-items` | Confirm a basket and return catalogue names/prices |
| `GET` | `/internal/v1/menu/scope/stores` | Return the caller's expanded store scope |

Responses use `RestAPIResponse` (`timeStamp`, `httpStatus`, `data`, `error`).
Errors are mapped by `http/handler`: `400` for validation/business input, `403`
for scope/permission denials, `404`/`204` for missing/empty resources, and `500`
for unhandled exceptions.

## Configuration

Key properties in `src/main/resources/application.properties`:

```properties
server.port=8082
server.address=127.0.0.1

spring.datasource.menu.mysql.jdbc-url=jdbc:mysql://localhost:3306/db-harmoni-master
spring.datasource.menu.mysql.username=pos-user
spring.datasource.menu.mysql.password=forlearn27!

harmoni.menu.jwt.secret=<base64 HMAC key>
harmoni.menu.jwt.expired.time=86400000

imgbb.url=https://api.imgbb.com/1/upload
imgbb.apiKey=<imgbb key>

auth.url=http://localhost:8081
auth.url.register=${auth.url}/api/v1/user
auth.url.delete=${auth.url}/api/v1/user
auth.url.update=${auth.url}/api/v1/user
auth.url.userIds=${auth.url}/api/v1/auth/users/ids

harmoni.menu.rbac.enforce=false
```

Swagger UI is at `/swagger-ui.html`; the OpenAPI document is at `/v3/api-docs`.
The checked-in credentials and secrets are development defaults; override them
per environment.

## Running locally

```bash
# 1. Create db-harmoni-master and apply sql/001..002 in order
# 2. Build
./mvnw -DskipTests package

# 3. Run
java -jar target/menu-0.0.1-SNAPSHOT.jar
# or
./mvnw spring-boot:run
```

The service listens on `127.0.0.1:8082`. In the full stack it is reached through
the gateway on port `8080`; the dashboard and other services call the gateway,
not this service directly.

## Testing

```bash
./mvnw test
```

Tests cover the business services, the promotion engine and pricing, order-item
validation, the controllers, the permission interceptor and the type handlers.

## Notes

- **The catalogue is authoritative here.** The order service stores what this
  service returns from `validate-order-items` and `promotion/price`; it never
  prices a line itself.
- **Scope is enforced twice.** `CallerScope` filters/guards data by brand, chain
  and store; `@PreAuthorize` (and optionally `PermissionInterceptor`) enforces
  the module permission.
- **Promotion pricing is pure.** `POST /api/v1/promotion/price` persists nothing
  and cannot be influenced by the caller beyond the lines it is selling.
- **Tier pricing.** SKU and customization-option prices are resolved from the
  operator's store tier, so the same item can cost different amounts per store.
