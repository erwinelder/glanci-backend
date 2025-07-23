package com.glanci.core.data.repository

import com.glanci.core.domain.model.app.TableName

interface UpdateTimeRepository {

    fun getUpdateTime(userId: Int, tableName: TableName): Long?

    fun saveUpdateTime(userId: Int, tableName: TableName, timestamp: Long)

}