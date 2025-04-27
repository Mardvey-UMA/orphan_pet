import asyncio
from contextlib import asynccontextmanager
from fastapi import FastAPI
from py_eureka_client.eureka_client import EurekaClient
import uvicorn
from sqlalchemy import text

from app.core.config import settings
from app.api.v1 import router as api_router
from fastapi.middleware.cors import CORSMiddleware

from app.core.database import AsyncSessionFactory
from app.services import BERTService


eureka_client = EurekaClient(**settings.EUREKA_SERVER_config)

@asynccontextmanager
async def lifespan(app: FastAPI):
    asyncio.create_task(eureka_client.start())
    async with AsyncSessionFactory() as session:
        await session.execute(text("SELECT 1"))
    BERTService.get_instance()
    yield

app = FastAPI(lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(api_router, prefix=settings.API_V1_STR)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8086)