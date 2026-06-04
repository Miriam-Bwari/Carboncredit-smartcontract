from pydantic import BaseModel
from typing import List, Literal


class GeoJsonPolygon(BaseModel):
    type: Literal["Polygon"] = "Polygon"
    # coordinates is an array of linear rings. 
    # For a simple polygon, it's a single array with the outer ring.
    # Each ring is an array of points [longitude, latitude].
    coordinates: List[List[List[float]]]
