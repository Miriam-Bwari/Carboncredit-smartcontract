# ai/carbon_calculator.py
# Converts NDVI change into carbon (kg CO2) and carbon credits

# Constants (based on agronomic + IPCC approximations)
BIOMASS_PER_NDVI_PER_HA = 2800   # kg biomass per NDVI unit per hectare
CARBON_FRACTION = 0.47           # ~47% of biomass is carbon (IPCC)
CO2_FACTOR = 3.67                # 1 tonne carbon = 3.67 tonnes CO2
SOIL_CARBON_FACTOR = 0.15        # soil contribution (~15%)

def estimate_carbon_kg(current_ndvi, previous_ndvi, area_hectares) -> dict:
    """
    Estimate carbon sequestration from NDVI change.

    Returns:
        dict: carbon_kg, credits, ndvi_change, optional note
    """

    # Safety checks
    if current_ndvi is None or previous_ndvi is None:
        return {
            "carbon_kg": 0,
            "credits": 0,
            "note": "Missing NDVI data"
        }

    if area_hectares is None or area_hectares <= 0:
        return {
            "carbon_kg": 0,
            "credits": 0,
            "note": "Invalid farm area"
        }

    # NDVI change
    ndvi_change = current_ndvi - previous_ndvi

    # Biomass change estimate
    biomass_change = ndvi_change * BIOMASS_PER_NDVI_PER_HA * area_hectares

    # Carbon conversion (above + soil)
    carbon_above = biomass_change * CARBON_FRACTION * CO2_FACTOR
    carbon_soil = carbon_above * SOIL_CARBON_FACTOR
    total_co2_kg = carbon_above + carbon_soil

    # No improvement case
    if total_co2_kg <= 0:
        return {
            "carbon_kg": 0,
            "credits": 0,
            "ndvi_change": round(ndvi_change, 4),
            "note": "No positive carbon gain"
        }

    # Carbon credits (1 credit = 1 tonne CO2 = 1000 kg)
    credits = round(total_co2_kg / 1000, 4)

    return {
        "carbon_kg": round(total_co2_kg, 2),
        "credits": credits,
        "ndvi_change": round(ndvi_change, 4)
    }