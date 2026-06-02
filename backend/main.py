# backend/main.py
# Starts the FastAPI server and connects all routers

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

# Database setup
from database.connection import engine, Base
from database import models

# Import routers (API endpoints)
from routers import farmers, farms, carbon, advice

# Create database tables (safe fallback)
# NOTE: In production, use migrations (Alembic)
models.Base.metadata.create_all(bind=engine)

# Create FastAPI app
app = FastAPI(
    title="Shamba Guard API",
    description="AI-powered carbon credit platform for smallholder farmers",
    version="1.0.0"
)

# CORS configuration (allows Android / frontend access)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # change to specific domain in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(farmers.router, prefix="/api/farmers", tags=["Farmers"])
app.include_router(farms.router, prefix="/api/farms", tags=["Farms"])
app.include_router(carbon.router, prefix="/api/carbon", tags=["Carbon"])
app.include_router(advice.router, prefix="/api/advice", tags=["AI Advice"])

# Root endpoint (test server)
@app.get("/")
def root():
    return {
        "message": "Shamba Guard API is running",
        "version": "1.0.0"
    }

# Health check endpoint
@app.get("/health")
def health():
    return {
        "status": "ok",
        "database": "MySQL"
    }