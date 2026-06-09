package dev.korryr.shambaguard.ui.features.agent.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.korryr.shambaguard.data.local.dao.FarmDao
import dev.korryr.shambaguard.data.local.dao.UserDao
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmRegisterRequestDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmerRegisterRequestDto
import dev.korryr.shambaguard.core.network.GeoJsonPolygonDto
import timber.log.Timber

@HiltWorker
class FarmSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val farmDao: FarmDao,
    private val userDao: UserDao,
    private val farmApi: FarmApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val pendingFarms = farmDao.getPendingFarmsSync()
            if (pendingFarms.isEmpty()) {
                return Result.success()
            }

            Timber.d("Starting sync for ${pendingFarms.size} farms")

            for (farm in pendingFarms) {
                // Fetch the associated user
                val user = userDao.getUserByIdSync(farm.farmerId) ?: continue

                // 1. Register the Farmer (creates account on backend)
                // We use a dummy password as discussed because the backend schema requires it.
                val farmerDto = FarmerRegisterRequestDto(
                    id = farm.farmerId,
                    fullName = user.name,
                    phoneNumber = user.phone,
                    password = "TempPassword123!",
                    county = "Meru" // Placeholder, should be derived from region
                )

                try {
                    farmApi.registerFarmer(farmerDto)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to register farmer, might already exist or network error")
                }

                // 2. Register the Farm
                val boundaryDto = GeoJsonPolygonDto(
                    type = "Polygon",
                    coordinates = parsePolygonJson(farm.polygonJson)
                )

                val farmDto = FarmRegisterRequestDto(
                    id = farm.farmId,
                    farmerId = farm.farmerId, 
                    name = "${user.name}'s Farm",
                    boundaryCoords = boundaryDto,
                    soilType = "Unknown",
                    cropType = farm.cropType,
                    county = "Meru"
                )

                farmApi.registerFarm(farmDto)

                // 3. Mark as synced locally
                farmDao.updateSyncStatus(
                    farmId = farm.farmId,
                    status = "SYNCED",
                    timestamp = System.currentTimeMillis()
                )
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "FarmSyncWorker failed")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private fun parsePolygonJson(json: String): List<List<List<Double>>> {
        // Very basic naive parsing for the dummy JSON format we created: "[[37.9,-0.02],...]"
        // A real app would use Gson or Kotlinx Serialization.
        val innerList = mutableListOf<List<Double>>()
        try {
            val stripped = json.removePrefix("[").removeSuffix("]")
            val points = stripped.split("],[")
            for (pt in points) {
                val cleanPt = pt.replace("[", "").replace("]", "")
                val coords = cleanPt.split(",")
                if (coords.size == 2) {
                    innerList.add(listOf(coords[0].toDouble(), coords[1].toDouble()))
                }
            }
        } catch (e: Exception) {
            Timber.e("Failed to parse polygon json")
        }
        return listOf(innerList)
    }
}
