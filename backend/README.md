# Mittagstisch - backend

## Getting started

```bash
# all commands used in ./backend
cd backend
```

Create environment files for `development mode` and `production mode`.

```bash
cp src/main/resources/application.yml src/main/resources/application-dev.yml
cp src/main/resources/application.yml src/main/resources/application-prod.yml
```

**Note**: These files will not be under version control but listed in .gitignore.

## Usage

### Run in development mode

```bash
# short
./mvnw

# long
./mvnw spring-boot:run -Pdev
```

### Run in production mode

```bash
# short
./mvnw -Pprod

# long
./mvnw spring-boot:run -Pprod
```

### Package and run in production mode

```bash
# package
./mvnw clean package

# package without tests
./mvnw clean package -DskipTests

# place the `application-prod.yml` aside the mittagstisch-1.0.0-SNAPSHOT.jar and run the jar
cp src/main/resources/application-prod.yml target/application-prod.yml
cd target
java -jar mittagstisch-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```
