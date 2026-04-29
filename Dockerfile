# ─────────────────────────────────────────────────────────────
# Stage 1 — Build Tailwind CSS
# ─────────────────────────────────────────────────────────────
FROM node:22-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci --include=dev

COPY . .

# Compile Tailwind to output.css
RUN npx tailwindcss -i ./views/input.css -o ./public/output.css --minify

# ─────────────────────────────────────────────────────────────
# Stage 2 — Runtime
# Prod dependencies only, no dev tools
# ─────────────────────────────────────────────────────────────
FROM node:22-alpine AS runtime

WORKDIR /app

# Non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY package*.json ./
RUN npm ci --omit=dev

# Copy app source and compiled CSS from builder
COPY --from=builder /app/public/output.css ./public/output.css
COPY . .

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 5000

CMD ["node", "app.js"]