package com.example.weatherjourney.features.weather.data.repository

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.weatherjourney.features.weather.data.workers.SuccessWorker
import com.example.weatherjourney.features.weather.domain.repository.RefreshRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SUCCESS_CONNECTIVITY_WORK_NAME = "successConnectivityWorkName"

class DefaultRefreshRepository(@ApplicationContext context: Context) : RefreshRepository {

    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosForUniqueWorkLiveData(SUCCESS_CONNECTIVITY_WORK_NAME).asFlow()
            .map { it.first() }

    override fun startWhenConnectivitySuccess() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val builder = OneTimeWorkRequestBuilder<SuccessWorker>().setConstraints(constraints)

        workManager.beginUniqueWork(
            SUCCESS_CONNECTIVITY_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            builder.build()
        ).enqueue()
    }
}
