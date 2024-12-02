# Используем официальный образ OpenJDK
FROM eclipse-temurin:22-jdk-jammy

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем файлы проекта в контейнер
COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Копируем wrapper файлы для Maven
COPY ./mvnw ./mvnw
COPY ./.mvn ./.mvn

# Устанавливаем права на выполнение wrapper
RUN chmod +x ./mvnw

# Скачиваем зависимости и собираем проект
RUN ./mvnw dependency:resolve
RUN ./mvnw package -DskipTests

# Открываем порт для доступа
EXPOSE 8090

# Команда запуска Spring Boot приложения
CMD ["java", "-jar", "target/FKanban-0.0.1-SNAPSHOT.jar"]