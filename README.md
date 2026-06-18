# Book Manager

このプロジェクトは、書籍と著者の管理を行うアプリケーションです。

## セットアップ

### 必要な環境
- Docker
- Docker Compose
- Java 11 以上
- Gradle

## 実行コマンド

### DBインスタンスを起動
```bash
docker compose --profile up -d
```

### DBインスタンスを起動 + flywayでDBスキーマを更新
```bash
docker compose --profile migrate up -d
```

### DBインスタンスを起動 + flywayでDBスキーマをリセット・更新
```bash
docker compose --profile rebuild up -d
```

### JOOQファイルを自動生成
```bash
./gradlew generateJooq
```

### テスト実行
```bash
./gradlew test
```

### アプリケーション起動
```bash
./gradlew bootRun
```

## プロジェクト構造

- `application/` - アプリケーション層
- `controller/` - コントローラー層
- `domain/` - ドメイン層
- `infra/` - インフラストラクチャ層
- `generated/` - JOOQ自動生成ファイル
- `migration/` - Flywayマイグレーションファイル

## API エンドポイント

### 著者管理 (`/authors`)

| メソッド | エンドポイント | 説明 |
|---------|--------------|------|
| POST | `/authors` | 新しい著者を登録 |
| PUT | `/authors` | 著者情報を更新 |
| GET | `/authors/{id}` | 指定IDの著者を取得 |

### 書籍管理 (`/books`)

| メソッド | エンドポイント | 説明 |
|---------|--------------|------|
| POST | `/books` | 新しい書籍を登録 |
| PUT | `/books` | 書籍情報を更新 |
| POST | `/books/publish` | 書籍を公開状態に更新 |
| GET | `/books/{id}` | 指定IDの書籍を取得 |
| POST | `/books/search` | 書籍を検索条件に基づいて検索 |

