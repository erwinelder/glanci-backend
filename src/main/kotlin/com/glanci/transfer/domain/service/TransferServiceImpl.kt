package com.glanci.transfer.domain.service

import com.glanci.auth.utils.authorizeAtLeastAsUserResult
import com.glanci.core.domain.service.UpdateTimeService
import com.glanci.request.shared.ResultData
import com.glanci.request.shared.SimpleResult
import com.glanci.request.shared.getDataOrReturn
import com.glanci.request.shared.returnIfError
import com.glanci.request.shared.error.DataError
import com.glanci.request.shared.error.TransferDataError
import com.glanci.transfer.data.repository.TransferRepository
import com.glanci.transfer.mapper.toDataModel
import com.glanci.transfer.mapper.toQueryDto
import com.glanci.transfer.shared.dto.TransferCommandDto
import com.glanci.transfer.shared.dto.TransferQueryDto
import com.glanci.transfer.shared.service.TransferService

class TransferServiceImpl(
    private val transferRepository: TransferRepository,
    private val updateTimeService: UpdateTimeService
) : TransferService {

    override suspend fun getUpdateTime(token: String): ResultData<Long, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }
        return updateTimeService.getUpdateTime(userId = user.id)
    }

    override suspend fun saveTransfers(
        transfers: List<TransferCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return SimpleResult.Error(it) }

        runCatching {
            transferRepository.upsertTransfers(
                transfers = transfers.map { it.toDataModel(userId = user.id) }
            )
        }.onFailure {
            return SimpleResult.Error(TransferDataError.TransfersNotSaved)
        }

        return updateTimeService.saveUpdateTime(timestamp = timestamp, userId = user.id)
    }

    override suspend fun getTransfersAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<TransferQueryDto>, DataError> {
        val user = authorizeAtLeastAsUserResult(token = token).getDataOrReturn { return ResultData.Error(it) }

        val transfers = runCatching {
            transferRepository.getTransfersAfterTimestamp(userId = user.id, timestamp = timestamp)
                .map { it.toQueryDto() }
        }.getOrElse {
            return ResultData.Error(TransferDataError.TransfersNotFetched)
        }

        return ResultData.Success(data = transfers)
    }

    override suspend fun saveTransfersAndGetAfterTimestamp(
        transfers: List<TransferCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<TransferQueryDto>, DataError> {
        saveTransfers(transfers = transfers, timestamp = timestamp, token = token)
            .returnIfError { return ResultData.Error(it) }
        return getTransfersAfterTimestamp(timestamp = localTimestamp, token = token)
    }

}