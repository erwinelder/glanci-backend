package com.glanci.transfer.mapper

import com.glanci.transfer.data.model.TransferDataModel
import com.glanci.transfer.shared.dto.TransferCommandDto
import com.glanci.transfer.shared.dto.TransferQueryDto


fun TransferCommandDto.toDataModel(userId: Int): TransferDataModel {
    return TransferDataModel(
        userId = userId,
        id = id,
        date = date,
        senderAccountId = senderAccountId,
        receiverAccountId = receiverAccountId,
        senderAmount = senderAmount,
        receiverAmount = receiverAmount,
        senderRate = senderRate,
        receiverRate = receiverRate,
        includeInBudgets = includeInBudgets,
        timestamp = timestamp,
        deleted = deleted
    )
}


fun TransferDataModel.toQueryDto(): TransferQueryDto {
    return TransferQueryDto(
        userId = userId,
        id = id,
        date = date,
        senderAccountId = senderAccountId,
        receiverAccountId = receiverAccountId,
        senderAmount = senderAmount,
        receiverAmount = receiverAmount,
        senderRate = senderRate,
        receiverRate = receiverRate,
        includeInBudgets = includeInBudgets,
        timestamp = timestamp,
        deleted = deleted
    )
}
