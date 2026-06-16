# Project Refactoring: Multi-Module Gradle Structure

## Summary
Successfully refactored the book-manager project from a monolithic single-module structure into a clean 5-module Gradle multi-project build. The refactoring eliminates circular compilation dependencies, ensures jOOQ-generated code is version-controlled, and provides clear separation of concerns.

## Modules Overview

### 1. **:domain** (Domain Layer)
- **Location**: `domain/`
- **Purpose**: Contains domain entities, value objects, and repository interfaces
- **Kotlin Sources**: `domain/src/main/kotlin/com/book_manager/domain/`
  - `entity/` - Domain entities (AuthorEntity, BookEntity)
  - `port/` - Repository interfaces (IAuthorRepository, IBookRepository)
  - `vo/` - Value objects (BirthDate, Price, BookPublicationStatus, Version)
- **Dependencies**: 
  - `org.jetbrains.kotlin:kotlin-stdlib`
- **No external framework dependencies** - Pure domain logic

### 2. **:generated** (jOOQ Generated Code)
- **Location**: `generated/`
- **Purpose**: Holds jOOQ-generated Java code for type-safe database access
- **Java Sources**: `generated/src/main/java/com/book_manager/jooq/`
  - Core: Tables, Keys, Public, Indexes, DefaultCatalog
  - `enums/` - Generated enums (BookPublicationStatusCode)
  - `tables/` - Table DAO classes (Author, Book, BookAuthor, FlywaySchemaHistory)
  - `tables/records/` - Generated record classes
- **Dependencies**:
  - `org.jooq:jooq:3.19.32`
- **Why Separate Module**:
  - ✅ Avoids circular dependency between compileJava and compileKotlin
  - ✅ Generated code is version-controlled in git
  - ✅ Clear boundary between generated and manual code
  - ✅ Easily regenerable: just replace the Java source files and rebuild

### 3. **:repository** (Data Access Layer)
- **Location**: `repository/`
- **Purpose**: Implements repository interfaces using jOOQ
- **Kotlin Sources**: `repository/src/main/kotlin/com/book_manager/repository/`
  - AuthorRepository.kt - Author data access implementation
  - BookRepository.kt - Book data access implementation
- **Dependencies**:
  - `project(':domain')` - Implement domain repository interfaces
  - `project(':generated')` - Use jOOQ-generated code
  - `org.springframework.boot:spring-boot-starter-jooq:4.0.6`
  - `org.springframework.boot:spring-boot-starter-data-jpa:4.0.6`
  - `org.jooq:jooq:3.19.32`
  - `org.postgresql:postgresql` (runtime)

### 4. **:application** (Application Services Layer)
- **Location**: `application/`
- **Purpose**: Contains application services and use cases
- **Kotlin Sources**: `application/src/main/kotlin/com/book_manager/application/`
  - AuthorAppService.kt, BookAppService.kt - Business logic
  - `command/` - Application commands (RegisterAuthorCommand, etc.)
  - `dto/` - Data transfer objects
  - `port/` - Application service interfaces
- **Dependencies**:
  - `project(':domain')` - Use domain models
  - `project(':repository')` - Inject repository implementations
  - `org.springframework:spring-context:6.1.14`
  - `org.springframework:spring-tx:6.1.14`
  - Spring transactions and dependency injection

### 5. **:controller** (Presentation/Controller Layer) ⭐ **RUNNABLE APP**
- **Location**: `controller/`
- **Purpose**: Spring Boot application entry point with REST controllers
- **Kotlin Sources**: `controller/src/main/kotlin/com/book_manager/`
  - `BookManagerApplication.kt` - Spring Boot application class
  - `controller/` - REST endpoints
    - AuthorController.kt, BookController.kt
  - Request/Response DTOs
- **Dependencies**:
  - `project(':application')` - Use application services
  - `org.springframework.boot:spring-boot-starter-web:4.0.6`
  - Kotlin reflect
- **Spring Boot Configuration**:
  - Application Main Class: `com.book_manager.BookManagerApplicationKt`
  - Creates boot jar: `controller/build/libs/controller.jar`
- **How to Run**:
  ```bash
  cd /Users/Auphyroc99/book-manager
  ./gradlew :controller:bootRun
  ```

### 6. **Root Project** (book-manager)
- **Purpose**: Aggregator and common configuration
- **Plugins**: 
  - Kotlin JVM
  - Kotlin Spring plugin
  - Spring Dependency Management (v1.1.7)
  - **NOT** Spring Boot application plugin (only :controller has it)
- **Dependencies**: Aggregates all modules at runtime
- **Repositories**: Provides mavenCentral() to all subprojects
- **Java Toolchain**: JDK 17

## Dependency Graph

```
controller (BOOT APP)
  ├── application
  │   ├── domain (pure business logic)
  │   └── repository
  │       ├── generated (jOOQ Java-only)
  │       ├── domain
  │       └── Spring/JPA/jOOQ dependencies
  └── Spring Web starter

root build.gradle (applies to all)
  ├── Manages repositories
  ├── Provides common Spring dependencies
  └── Includes all modules
```

## Key Improvements

### 1. ✅ **Eliminated Circular Dependency**
- **Before**: `:repository:compileKotlin` → `:repository:compileJava` → `:repository:compileKotlin` (❌ CYCLE)
- **After**: `:repository:compileKotlin` → `:generated:assemble` (separate module compilation)

### 2. ✅ **Version-Controlled Generated Code**
- jOOQ-generated Java files are committed to git (not ignored in .gitignore)
- Located in: `generated/src/main/java/com/book_manager/jooq/`
- Easy to regenerate: replace files in `generated/` and rebuild
- Prevents "works on my machine" issues

### 3. ✅ **Clear Separation of Concerns**
- **Domain** - Pure business logic, no frameworks
- **Repository** - Data access, Spring Data/jOOQ integration
- **Application** - Business services and orchestration
- **Controller** - HTTP endpoints and request/response
- **Generated** - Database access code (auto-generated)

### 4. ✅ **Runnable from Controller Module**
- `./gradlew :controller:build` creates boot jar
- `./gradlew :controller:bootRun` runs the app
- Controller module configures main class: `com.book_manager.BookManagerApplicationKt`

### 5. ✅ **Independent Module Compilation**
- Each module can be compiled independently:
  ```bash
  ./gradlew :domain:build
  ./gradlew :repository:build
  ./gradlew :application:build
  ./gradlew :controller:build
  ./gradlew :generated:build
  ```
- Faster incremental builds in IDE

## Build Configuration

### settings.gradle
```groovy
include ':application'
include ':controller'
include ':domain'
include ':repository'
include ':generated'
```

### Each Module's build.gradle
- **domain**: Kotlin + stdlib only
- **generated**: Java only (no plugins, just jOOQ dependency)
- **repository**: Kotlin + Spring Boot JPA/jOOQ + Spring Dependency Management
- **application**: Kotlin + Spring Context + Spring TX + Spring Dependency Management
- **controller**: Kotlin + Spring Boot Web + Spring Plugin (includes main class config)

### Root build.gradle
- Provides repositories to all subprojects
- Kotlin JVM + Spring plugin + dependency management
- Aggregates all modules for top-level build
- **NO Boot plugin** (only controller is the boot app)

## Migration Path

If regenerating jOOQ code:
1. Update database schema with Flyway migrations
2. Run jOOQ code generator (previously configured in root, now would be a build script)
3. Replace files in `generated/src/main/java/com/book_manager/jooq/`
4. Run `./gradlew clean build`

The `:generated` module is intentionally kept separate so that regeneration doesn't trigger full rebuilds of repository Kotlin code.

## Testing

To run all tests:
```bash
./gradlew test
```

To build without tests:
```bash
./gradlew build -x test
```

Note: Test files are located in each module's `src/test/` directory.

## File Structure Locations

```
book-manager/
├── settings.gradle                           (includes 5 submodules)
├── build.gradle                              (root, NOT boot app)
├── application/                              (app services)
│   ├── build.gradle
│   └── src/main/kotlin/com/book_manager/application/
├── controller/                               ⭐ (BOOT APP - run from here)
│   ├── build.gradle                          (defines main class)
│   └── src/main/kotlin/com/book_manager/
├── domain/                                   (pure domain logic)
│   ├── build.gradle
│   └── src/main/kotlin/com/book_manager/domain/
├── repository/                               (data access)
│   ├── build.gradle
│   └── src/main/kotlin/com/book_manager/repository/
└── generated/                                (jOOQ generated Java)
    ├── build.gradle
    └── src/main/java/com/book_manager/jooq/
        ├── Tables.java
        ├── Keys.java
        ├── enums/
        ├── tables/
        └── tables/records/
```

## Commands Reference

### Build Commands
```bash
# Full clean build
./gradlew clean build -x test

# Build specific module
./gradlew :controller:build

# Build all modules
./gradlew :domain:build :repository:build :application:build :controller:build

# View module structure
./gradlew projects
```

### Run Commands
```bash
# Run Spring Boot app from controller
./gradlew :controller:bootRun

# Run all tests
./gradlew test

# Build without tests
./gradlew build -x test
```

### Dependency Commands
```bash
# Show dependencies for a module
./gradlew :repository:dependencies

# Show task list for a module
./gradlew :controller:tasks
```

## Notes

- All modules use Kotlin 2.2.21
- Spring Boot version: 4.0.6
- jOOQ version: 3.19.32
- Java toolchain: JDK 17
- Database: PostgreSQL (runtime dependency)
- No circular dependencies ✅
- All modules compile independently ✅
- Generated code version-controlled ✅

