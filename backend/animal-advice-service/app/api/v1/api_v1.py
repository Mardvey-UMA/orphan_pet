from fastapi import APIRouter
from .endpoints import bert,analysis

router = APIRouter()
router.include_router(bert.router, prefix="/bert", tags=["BERT Advice"])
router.include_router(analysis.router, prefix="/analysis", tags=["DeepSeek Analysis"])