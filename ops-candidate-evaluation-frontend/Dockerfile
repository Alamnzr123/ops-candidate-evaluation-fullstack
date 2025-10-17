# Build stage (Node 20)
FROM node:20-bullseye AS builder
WORKDIR /app

# Allow setting API base at build time; default to relative path so browser uses same origin
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

# copy package files and install
COPY ops-candidate-evaluation-frontend/package*.json ./ 
RUN npm ci --no-audit --no-fund

# copy sources and build
COPY ops-candidate-evaluation-frontend/ .
RUN npm run build

# Runtime (nginx)
FROM nginx:stable-alpine
COPY ops-candidate-evaluation-frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]