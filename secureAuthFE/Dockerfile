#fase di build
FROM node:20-alpine AS build
#directory di lavoro all'interno dell'immagine
WORKDIR /app

#Copio prima solo i file delle dipendenze
COPY package.json package-lock.json ./
#serve per installare le dipendenze in modo riproducibile e pulito
RUN npm ci

# Copio il resto del codice
COPY . .
#produce la cartella dist/ in cui dentro c'è html, js e css, trasformando le pagine in statiche
#in questo comando i file jsx ecc vengono convertiti in pagine statiche e il codice viene ottimizzato
RUN npm run build

#fase di runtime
#utilizzo nginx (server web) apline è la versione piu leggera
FROM nginx:alpine 
#copia i file compilati in dist/ nella fase di build alla cartella che nginx utilizzerà per servire i file
COPY --from=build /app/dist /usr/share/nginx/html
#il container sarà in ascolto sulla porta 80
EXPOSE 80
