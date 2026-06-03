# ai/farming_advisor.py
# Provides AI farming recommendations based on NDVI and crop type

RECOMMENDATIONS = {
    "maize": {
        "low": [
            "Apply organic compost — 2 tonnes per hectare.",
            "Plant Calliandra trees along borders for nitrogen fixation.",
            "Use minimum tillage to preserve soil moisture."
        ],
        "medium": [
            "Add 50 trees per hectare this season.",
            "Apply mulch after harvest to retain moisture.",
            "Intercrop with beans to improve soil nitrogen."
        ],
        "high": [
            "Excellent growth. Maintain current farming practices.",
            "Document tree species for carbon credit verification.",
            "Consider adding fruit trees for extra income."
        ],
    },
    "beans": {
        "low": [
            "Plant cover crops between rows.",
            "Reduce tillage depth to protect soil structure."
        ],
        "medium": [
            "Rotate with maize next season.",
            "Add compost layer to improve soil fertility."
        ],
        "high": [
            "Great crop health. Expand agroforestry gradually."
        ],
    },
}

TREES = {
    "Central": ["Grevillea robusta", "Calliandra", "Avocado"],
    "Rift Valley": ["Acacia", "Melia volkensii", "Moringa"],
    "Eastern": ["Calliandra", "Leucaena", "Mango"],
    "default": ["Calliandra", "Leucaena leucocephala", "Moringa"],
}

def get_recommendations(crop_type: str, ndvi: float, county: str) -> dict:
    """
    Generate farming recommendations based on NDVI and crop type.

    Returns:
        dict: NDVI score, health status, recommendations, and tree suggestions
    """

    # Safety checks
    if ndvi is None:
        return {
            "error": "NDVI value is required"
        }

    # Normalize inputs
    crop = (crop_type or "maize").lower()
    county = county or "default"

    # Determine farm health category
    if ndvi < 0:
        category = "low"
    elif ndvi < 0.3:
        category = "low"
    elif ndvi < 0.6:
        category = "medium"
    else:
        category = "high"

    # Get crop recommendations (fallback to maize if unknown crop)
    crop_data = RECOMMENDATIONS.get(crop, RECOMMENDATIONS["maize"])
    advice = crop_data.get(category, crop_data["medium"])

    # Tree recommendations
    trees = TREES.get(county, TREES["default"])

    return {
        "ndvi_score": round(ndvi, 3),
        "farm_health": category.upper(),
        "recommendations": advice,
        "recommended_trees": trees
    }