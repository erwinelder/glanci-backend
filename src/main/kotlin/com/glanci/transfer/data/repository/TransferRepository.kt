package com.glanci.transfer.data.repository

import com.glanci.transfer.data.model.TransferDataModel

interface TransferRepository {

    fun upsertTransfers(transfers: List<TransferDataModel>)

    fun getTransfersAfterTimestamp(userId: Int, timestamp: Long): List<TransferDataModel>

}