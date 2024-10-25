package net.dungeonhub.model.carry_difficulty

import net.dungeonhub.structure.model.CreationModel

class CarryDifficultyCreationModel(
    var identifier: String,
    var displayName: String,
    var thumbnailUrl: String?,
    var bulkPrice: Int?,
    var bulkAmount: Int?,
    var priceName: String?,
    var price: Int?,
    var score: Int?
) : CreationModel