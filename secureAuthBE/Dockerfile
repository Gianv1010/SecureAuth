#fase di build (compila e crea il jar)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copio prima solo i file necessari per scaricare le dipendenze (cache migliore)
COPY pom.xml ./
# comando per scaricare le dipendenza. -DskipTests --> evita i test durante la build
RUN mvn -q -DskipTests dependency:go-offline

# Copio il sorgente e buildo
COPY src ./src
#buila il progetto nel file .jar che viene utilizzato durante l'avvio dell'app
RUN mvn -DskipTests package

#fase di runtime (solo JRE, più leggera)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copio il jar prodotto nella fase di build. Questa cartella trget viene creata da maven quando si fa la build
# e dice di prendere il file da FROM maven:3.9-eclipse-temurin-21 che ho nominato come build, perchè senza --from prenderebbe il file dal mio pc locale
# e dalla build, nel percorso inidicato, prende il file .jar creato da maven e lo inserisce nella directory corrente (workdir) rinominandolo app.jar

COPY --from=build /app/target/*.jar app.jar

# Spring Boot di default ascolta su 8080
EXPOSE 8080

# Avvio applicazione
ENTRYPOINT ["java","-jar","app.jar"]
