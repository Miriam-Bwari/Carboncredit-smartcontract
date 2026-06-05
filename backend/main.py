from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from database.connection import engine, Base
from database import models
from routers import farmers, farms, carbon, advice, agents, payments, weather

models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Shamba Guard API",
    description="AI-powered carbon credit platform for smallholder farmers",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # restrict to specific domain in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(farmers.router,  prefix="/api/farmers",  tags=["Farmers"])
app.include_router(agents.router,   prefix="/api/agents",   tags=["Agents"])
app.include_router(farms.router,    prefix="/api/farms",    tags=["Farms"])
app.include_router(carbon.router,   prefix="/api/carbon",   tags=["Carbon"])
app.include_router(advice.router,   prefix="/api/advice",   tags=["AI Advice"])
app.include_router(payments.router, prefix="/api/payments", tags=["Payments"])
app.include_router(weather.router,  prefix="/api/weather",  tags=["Weather"])


@app.get("/")
def root():
    return {"message": "Shamba Guard API is running", "version": "1.0.0"}


@app.get("/health")
def health():
    return {"status": "ok", "database": "MySQL"}