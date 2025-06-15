package com.nargok.sakemap.domain.model.vo

enum class Prefecture(val isoCode: String, val kanji: String) {
    HOKKAIDO("JP-01", "北海道"),
    AOMORI("JP-02", "青森県"),
    IWATE("JP-03", "岩手県"),
    MIYAGI("JP-04", "宮城県"),
    AKITA("JP-05", "秋田県"),
    YAMAGATA("JP-06", "山形県"),
    FUKUSHIMA("JP-07", "福島県"),
    IBARAKI("JP-08", "茨城県"),
    TOCHIGI("JP-09", "栃木県"),
    GUNMA("JP-10", "群馬県"),
    SAITAMA("JP-11", "埼玉県"),
    CHIBA("JP-12", "千葉県"),
    TOKYO("JP-13", "東京都"),
    KANAGAWA("JP-14", "神奈川県"),
    NIIGATA("JP-15", "新潟県"),
    TOYAMA("JP-16", "富山県"),
    ISHIKAWA("JP-17", "石川県"),
    FUKUI("JP-18", "福井県"),
    YAMANASHI("JP-19", "山梨県"),
    NAGANO("JP-20", "長野県"),
    GIFU("JP-21", "岐阜県"),
    SHIZUOKA("JP-22", "静岡県"),
    AICHI("JP-23", "愛知県"),
    MIE("JP-24", "三重県"),
    SHIGA("JP-25", "滋賀県"),
    KYOTO("JP-26", "京都府"),
    OSAKA("JP-27", "大阪府"),
    HYOGO("JP-28", "兵庫県"),
    NARA("JP-29", "奈良県"),
    WAKAYAMA("JP-30", "和歌山県"),
    TOTTORI("JP-31", "鳥取県"),
    SHIMANE("JP-32", "島根県"),
    OKAYAMA("JP-33", "岡山県"),
    HIROSHIMA("JP-34", "広島県"),
    YAMAGUCHI("JP-35", "山口県"),
    TOKUSHIMA("JP-36", "徳島県"),
    KAGAWA("JP-37", "香川県"),
    EHIME("JP-38", "愛媛県"),
    KOCHI("JP-39", "高知県"),
    FUKUOKA("JP-40", "福岡県"),
    SAGA("JP-41", "佐賀県"),
    NAGASAKI("JP-42", "長崎県"),
    KUMAMOTO("JP-43", "熊本県"),
    OITA("JP-44", "大分県"),
    MIYAZAKI("JP-45", "宮崎県"),
    KAGOSHIMA("JP-46", "鹿児島県"),
    OKINAWA("JP-47", "沖縄県");

    companion object {
        private val codeMap = entries.associateBy { it.isoCode }
        fun fromIsoCode(code: String): Prefecture =
            codeMap[code] ?: throw IllegalArgumentException("Invalid ISO code: $code")
    }
}
