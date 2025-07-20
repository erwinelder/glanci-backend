package com.glanci.categoryCollection.data.model

data class CategoryCollectionWithAssociationsDataModel(
    val collection: CategoryCollectionDataModel,
    val associations: List<CategoryCollectionCategoryAssociationDataModel>
) {

    fun asPair(): Pair<CategoryCollectionDataModel, List<CategoryCollectionCategoryAssociationDataModel>> {
        return collection to associations.takeUnless { collection.deleted }.orEmpty()
    }

}
