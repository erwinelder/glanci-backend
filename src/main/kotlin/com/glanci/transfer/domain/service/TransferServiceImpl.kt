package com.glanci.transfer.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUser
import com.glanci.core.data.repository.UpdateTimeRepository
import com.glanci.core.domain.dto.TableName
import com.glanci.core.error.UpdateTimeError
import com.glanci.transfer.data.repository.TransferRepository
import com.glanci.transfer.error.TransferError
import com.glanci.transfer.mapper.toDataModel
import com.glanci.transfer.mapper.toQueryDto
import com.glanci.transfer.shared.dto.TransferCommandDto
import com.glanci.transfer.shared.dto.TransferQueryDto
import com.glanci.transfer.shared.service.TransferService

class TransferServiceImpl(
    private val transferRepository: TransferRepository,
    private val updateTimeRepository: UpdateTimeRepository
) : TransferService {

    private val tableName = TableName.Transfer


    override suspend fun getUpdateTime(token: String): Long? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            updateTimeRepository.getUpdateTime(userId = user.id, tableName = tableName) ?: 0
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotFetched() }
            .getOrNull()
    }

    private fun saveUpdateTime(timestamp: Long, userId: Int) {
        runCatching {
            updateTimeRepository.saveUpdateTime(userId = userId, tableName = tableName, timestamp = timestamp)
        }
            .onFailure { throw UpdateTimeError.UpdateTimeNotSaved() }
    }

    override suspend fun saveTransfers(
        transfers: List<TransferCommandDto>,
        timestamp: Long,
        token: String
    ) {
        val user = authorizeAtLeastAsUser(token = token)

        runCatching {
            transferRepository.upsertTransfers(
                transfers = transfers.map { it.toDataModel(userId = user.id) }
            )
        }
            .onFailure { throw TransferError.TransfersNotSaved() }

        saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getTransfersAfterTimestamp(
        timestamp: Long,
        token: String
    ): List<TransferQueryDto>? {
        val user = authorizeAtLeastAsUser(token = token)

        return runCatching {
            transferRepository.getTransfersAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }
            .onFailure { throw TransferError.TransfersNotFetched() }
            .getOrNull()
    }

    override suspend fun saveTransfersAndGetAfterTimestamp(
        transfers: List<TransferCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): List<TransferQueryDto>? {
        saveTransfers(transfers = transfers, timestamp = timestamp, token = token)
        return getTransfersAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}