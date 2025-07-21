package com.glanci.account.mapper

import com.glanci.account.data.model.AccountDataModel
import com.glanci.account.shared.dto.AccountCommandDto
import com.glanci.account.shared.dto.AccountQueryDto


fun AccountCommandDto.toDataModel(userId: Int): AccountDataModel {
    return AccountDataModel(
        userId = userId,
        id = id,
        orderNum = orderNum,
        name = name,
        currency = currency,
        balance = balance,
        color = color,
        hide = hide,
        hideBalance = hideBalance,
        withoutBalance = withoutBalance,
        timestamp = timestamp,
        deleted = deleted
    )
}


fun AccountDataModel.toQueryDto(): AccountQueryDto {
    return AccountQueryDto(
        userId = userId,
        id = id,
        orderNum = orderNum,
        name = name,
        currency = currency,
        balance = balance,
        color = color,
        hide = hide,
        hideBalance = hideBalance,
        withoutBalance = withoutBalance,
        timestamp = timestamp,
        deleted = deleted
    )
}
