FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app

# Копируем весь контекст сборки
COPY . .

# Вывод списка файлов для отладки
RUN echo ">>> Список файлов после COPY:" && ls -l

# Делаем Maven wrapper исполняемым
RUN chmod +x mvnw

# Собираем приложение с использованием Maven Wrapper в batch-режиме, пропуская тесты
RUN ./mvnw -B clean package -DskipTests -e

# Вывод содержимого каталога target, чтобы убедиться, что сборка прошла успешно и артефакт создан
RUN echo ">>> Содержимое target:" && ls -l /app/target

FROM eclipse-temurin:23-jre
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
