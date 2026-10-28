package net.dungeonhub.model.carry_difficulty

/**
 * A discounted price per carry that applies once at least [amount] carries are bought.
 */
data class BulkPriceModel(
    val amount: Int,
    val price: Int
)
