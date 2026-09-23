# SmartRoute - Complete Production Deployment Guide

This guide details how to deploy the **SmartRoute Backend Service** and **Interactive Web Dashboard** to production environments.

---

## 1. Live Frontend Deployment (GitHub Pages)

The interactive campus navigation dashboard is served directly from `index.html`.

### How to Enable GitHub Pages (1-Click Setup)
1. Go to your repository on GitHub:
   👉 **`https://github.com/vamshikrishna72/MoveInSync_Case-Study`**
2. Click on **Settings** $\to$ **Pages** (in the left sidebar).
3. Under **Build and deployment**:
   - **Source**: Select `Deploy from a branch`
   - **Branch**: Select `main` and folder `/ (root)`
4. Click **Save**.

Your live frontend web dashboard will be available at:
👉 **`https://vamshikrishna72.github.io/MoveInSync_Case-Study/`**

---

## 2. Full Containerized Backend Deployment (Docker Compose)

To deploy the Spring Boot API, PostgreSQL Database, and Redis Cache on any cloud VM (AWS EC2, DigitalOcean, Linode, GCP, Azure, or Hetzner):

### Step 1: Provision Cloud Instance
Create a Linux VM (Ubuntu 22.04 LTS recommended) with at least 2 GB RAM and 1 vCPU.

### Step 2: Install Docker & Docker Compose
```bash
sudo apt-get update
sudo apt-get install -y docker.io docker-compose
sudo systemctl enable --now docker
```

### Step 3: Clone Repository & Deploy
```bash
git clone https://github.com/vamshikrishna72/MoveInSync_Case-Study.git
cd MoveInSync_Case-Study
docker-compose up -d --build
```

### Step 4: Verify Deployment
```bash
docker-compose ps
curl http://localhost:8080/actuator/health
```

---

## 3. Managed Cloud Deployment (Render.com / Railway.app)

### Option A: Deploying on Render.com
1. Log in to [Render.com](https://render.com).
2. Click **New +** $\to$ **Blueprint**.
3. Connect your GitHub repository `vamshikrishna72/MoveInSync_Case-Study`.
4. Render will read `docker-compose.yml` or `Dockerfile` and deploy the service automatically.

### Option B: Deploying on Railway.app
1. Log in to [Railway.app](https://railway.app).
2. Click **New Project** $\to$ **Deploy from GitHub repo**.
3. Select `MoveInSync_Case-Study`.
4. Add a PostgreSQL database and a Redis database from the Railway marketplace.
5. Set Environment Variables:
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}`
   - `SPRING_DATASOURCE_USERNAME`: `${PGUSER}`
   - `SPRING_DATASOURCE_PASSWORD`: `${PGPASSWORD}`
   - `SPRING_REDIS_HOST`: `${REDISHOST}`
   - `SPRING_REDIS_PORT`: `${REDISPORT}`
