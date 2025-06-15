package com.nargok.sakemap.domain.model.vo

import de.huxhorn.sulky.ulid.ULID

enum class DrinkType(val displayName: String) {
    SAKE("日本酒"),
    BEER("ビール"),
    WHISKEY("ウイスキー"),
    SHOCHU("焼酎"),
    WINE("ワイン"),
    VODKA("ウォッカ"),
    GIN("ジン"),
    RUM("ラム"),
    LIQUEUR("リキュール")
}

data class DrinkRecordId private constructor(val value: String) {
    companion object {
        private val ulld = ULID()

        fun create(): DrinkRecordId {
            return DrinkRecordId(ulld.nextULID())
        }

        fun reconstruct(id: String): DrinkRecordId {
            return DrinkRecordId(id)
        }
    }

    override fun toString(): String {
        return value
    }
}