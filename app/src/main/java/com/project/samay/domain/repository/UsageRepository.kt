    package com.project.samay.domain.repository

    import android.app.usage.UsageEvents
    import android.app.usage.UsageStatsManager
    import android.content.Context
    import android.util.Log
    import android.widget.Toast
    import com.project.samay.domain.model.MonitoredApps
    import com.project.samay.domain.usecases.ONE_SESSION
    import com.project.samay.util.calculations.Logic
    import com.project.samay.util.calculations.TimeUtils
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.flow
    import kotlinx.coroutines.flow.flowOf

    class UsageRepository(private val context: Context) {
//        var mapUsage = flowOf(emptyMap<MonitoredApps, Long>())


        fun getData(): Flow<Map<MonitoredApps, Long>> {
            return flow {
                val mapUsageVal = emptyMap<MonitoredApps, Long>().toMutableMap()
                val usageStatsManager =
                    context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val currentTime = System.currentTimeMillis()
                val usageEvents =
                    usageStatsManager.queryEvents(currentTime - 1000 * ONE_SESSION, currentTime)
                val listOfPackageNames = MonitoredApps.entries.map { it.packageName }
                var lastTimeUsed: Long = 0
                val event = UsageEvents.Event()

                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event)
                    Log.i("UsageRepository", "Event found ${event.packageName} ${TimeUtils.convertMillisToString(event.timeStamp)}")
                    if (event.packageName in listOfPackageNames && event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                        mapUsageVal[MonitoredApps.entries.find { it.packageName == event.packageName }!!] =
                            event.timeStamp
                    }
                }
                Log.i("UsageRepository", "Map Usage: ${mapUsageVal.map { it.key to TimeUtils.convertMillisToString(it.value) }}")
                emit(mapUsageVal.toMap())
            }
        }

        fun navigateToApp(packageName: String){
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                context.startActivity(intent)
            }catch (e: Exception){
                Log.e("UsageRepository", "Error: $e")
                Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
            }
        }

    }