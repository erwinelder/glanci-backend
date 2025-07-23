package com.glanci.transfer.shared.service

import com.glanci.request.domain.ResultData
import com.glanci.request.domain.SimpleResult
import com.glanci.request.domain.error.RootError
import com.glanci.transfer.shared.dto.TransferCommandDto
import com.glanci.transfer.shared.dto.TransferQueryDto
import kotlinx.rpc.annotations.Rpc

@Rpc
interface TransferService {

    suspend fun getUpdateTime(token: String): ResultData<Long, RootError>

    suspend fun saveTransfers(
        transfers: List<TransferCommandDto>,
        timestamp: Long,
        token: String
    ): SimpleResult<RootError>

    suspend fun getTransfersAfterTimestamp(
        timestamp: Long,
        token: String
    ): ResultData<List<TransferQueryDto>, RootError>

    suspend fun saveTransfersAndGetAfterTimestamp(
        transfers: List<TransferCommandDto>,
        timestamp: Long,
        localTimestamp: Long,
        token: String
    ): ResultData<List<TransferQueryDto>, RootError>

}