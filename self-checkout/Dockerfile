# Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Copy package files first for better layer caching
COPY package.json pnpm-lock.yaml ./

# Install pnpm and dependencies
RUN npm install -g pnpm && pnpm install --frozen-lockfile

# Copy source code
COPY . .

# Build the application
RUN pnpm build

# Runtime stage
FROM node:20-alpine AS runner

WORKDIR /app

RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -S nextjs && adduser -S nextjs -G nextjs

# Copy necessary files from build stage
COPY --from=build /app/public ./public
COPY --from=build /app/.next ./.next
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/package.json ./

# Change ownership to non-root user
RUN chown -R nextjs:nextjs /app

USER nextjs

# Expose port
EXPOSE 3000

# Set environment variables
ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Run the application
CMD ["node_modules/.bin/next", "start"]
