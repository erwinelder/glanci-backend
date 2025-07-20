package com.glanci.categoryCollection.mapper

import com.glanci.categoryCollection.data.model.CategoryCollectionCategoryAssociationDataModel
import com.glanci.categoryCollection.data.model.CategoryCollectionDataModel
import com.glanci.categoryCollection.data.model.CategoryCollectionWithAssociationsDataModel
import com.glanci.categoryCollection.shared.dto.CategoryCollectionCategoryAssociationDto
import com.glanci.categoryCollection.shared.dto.CategoryCollectionDto
import com.glanci.categoryCollection.shared.dto.CategoryCollectionWithAssociationsDto


fun CategoryCollectionDto.toDataModel(userId: Int): CategoryCollectionDataModel {
    return CategoryCollectionDataModel(
        userId = userId,
        id = id,
        orderNum = orderNum,
        type = type,
        name = name,
        timestamp = timestamp,
        deleted = deleted
    )
}

fun CategoryCollectionCategoryAssociationDto.toDataModel(
    userId: Int
): CategoryCollectionCategoryAssociationDataModel {
    return CategoryCollectionCategoryAssociationDataModel(
        userId = userId,
        collectionId = collectionId,
        categoryId = categoryId
    )
}

fun CategoryCollectionWithAssociationsDto.toDataModel(userId: Int): CategoryCollectionWithAssociationsDataModel {
    return CategoryCollectionWithAssociationsDataModel(
        collection = collection.toDataModel(userId = userId),
        associations = associations.map { it.toDataModel(userId = userId) }
    )
}


fun CategoryCollectionDataModel.toDto(): CategoryCollectionDto {
    return CategoryCollectionDto(
        id = id,
        orderNum = orderNum,
        type = type,
        name = name,
        timestamp = timestamp,
        deleted = deleted
    )
}

fun CategoryCollectionCategoryAssociationDataModel.toDto(): CategoryCollectionCategoryAssociationDto {
    return CategoryCollectionCategoryAssociationDto(
        collectionId = collectionId,
        categoryId = categoryId
    )
}

fun CategoryCollectionWithAssociationsDataModel.toDto(): CategoryCollectionWithAssociationsDto {
    return CategoryCollectionWithAssociationsDto(
        collection = collection.toDto(),
        associations = associations.map { it.toDto() }
    )
}
