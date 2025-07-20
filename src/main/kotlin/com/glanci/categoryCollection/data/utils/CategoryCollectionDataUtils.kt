package com.glanci.categoryCollection.data.utils

import com.glanci.categoryCollection.data.model.CategoryCollectionCategoryAssociationDataModel
import com.glanci.categoryCollection.data.model.CategoryCollectionDataModel
import com.glanci.categoryCollection.data.model.CategoryCollectionWithAssociationsDataModel


fun List<CategoryCollectionWithAssociationsDataModel>.divide(
): Pair<List<CategoryCollectionDataModel>, List<CategoryCollectionCategoryAssociationDataModel>> {
    return map { it.asPair() }
        .unzip()
        .let { it.first to it.second.flatten() }
}

fun List<CategoryCollectionDataModel>.zipWithAssociations(
    associations: List<CategoryCollectionCategoryAssociationDataModel>
): List<CategoryCollectionWithAssociationsDataModel> {
    return associateWith { collection ->
            if (!collection.deleted) associations.filter { it.collectionId == collection.id } else emptyList()
        }
        .map { (collection, associations) ->
            CategoryCollectionWithAssociationsDataModel(collection = collection, associations = associations)
        }
}
