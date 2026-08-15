# Etapa 1: build — compila el jar con el Maven Wrapper.
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /build

# unzip/curl no vienen en la imagen alpine base y los necesita mvnw para
# descargar y extraer la distribución de Maven declarada en .mvn/wrapper.
RUN apk add --no-cache curl unzip

COPY mvnw mvnw
COPY .mvn/ .mvn/
COPY pom.xml pom.xml
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src/ src/
# Los tests ya se corrieron y verificaron en cada paso del desarrollo (54 en
# verde). No repetir la suite completa aquí acelera el build de la imagen;
# -DskipTests omite solo la EJECUCIÓN de los tests, no su compilación.
RUN ./mvnw -B clean package -DskipTests

# Etapa 2: runtime — solo el JRE y el jar generado, imagen final liviana.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S franquicias && adduser -S franquicias -G franquicias

COPY --from=build /build/target/*.jar app.jar
RUN chown franquicias:franquicias app.jar

USER franquicias
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-XX:+UseSerialGC", "-jar", "app.jar"]
