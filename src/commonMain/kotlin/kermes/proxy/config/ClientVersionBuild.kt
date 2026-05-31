package kermes.proxy.config

import kotlinx.serialization.Serializable

@Serializable
enum class ClientVersionBuild(
    val build: Int,
    val majorVersion: Int
) {
    Zero(
        build = 0,
        majorVersion = 0,
    ),

    V1_9_4_5086(
        build = 5086,
        majorVersion = 1,
    ),
    V1_12_1_5875(
        build = 5875,
        majorVersion = 1,
    ),
    V1_12_2_6005(
        build = 6005,
        majorVersion = 1,
    ),
    V1_12_3_6141(
        build = 6141,
        majorVersion = 1,
    ),

    V2_0_1_6180(
        build = 6180,
        majorVersion = 2,
    ),
    V2_0_3_6299(
        build = 6299,
        majorVersion = 2,
    ),
    V2_0_6_6337(
        build = 6337,
        majorVersion = 2,
    ),
    V2_1_0_6692(
        build = 6692,
        majorVersion = 2,
    ),
    V2_1_1_6739(
        build = 6739,
        majorVersion = 2,
    ),
    V2_1_2_6803(
        build = 6803,
        majorVersion = 2,
    ),
    V2_1_3_6898(
        build = 6898,
        majorVersion = 2,
    ),
    V2_2_0_7272(
        build = 7272,
        majorVersion = 2,
    ),
    V2_2_2_7318(
        build = 7318,
        majorVersion = 2,
    ),
    V2_2_3_7359(
        build = 7359,
        majorVersion = 2,
    ),
    V2_3_0_7561(
        build = 7561,
        majorVersion = 2,
    ),
    V2_3_2_7741(
        build = 7741,
        majorVersion = 2,
    ),
    V2_3_3_7799(
        build = 7799,
        majorVersion = 2,
    ),
    V2_4_0_8089(
        build = 8089,
        majorVersion = 2,
    ),
    V2_4_1_8125(
        build = 8125,
        majorVersion = 2,
    ),
    V2_4_2_8209(
        build = 8209,
        majorVersion = 2,
    ),
    V2_4_3_8606(
        build = 8606,
        majorVersion = 2,
    ),

    V3_0_2_9056(
        build = 9056,
        majorVersion = 3,
    ),
    V3_0_3_9183(
        build = 9183,
        majorVersion = 3,
    ),
    V3_0_8_9464(
        build = 9464,
        majorVersion = 3,
    ),
    V3_0_8a_9506(
        build = 9506,
        majorVersion = 3,
    ),
    V3_0_9_9551(
        build = 9551,
        majorVersion = 3,
    ),
    V3_1_0_9767(
        build = 9767,
        majorVersion = 3,
    ),
    V3_1_1_9806(
        build = 9806,
        majorVersion = 3,
    ),
    V3_1_1a_9835(
        build = 9835,
        majorVersion = 3,
    ),
    V3_1_2_9901(
        build = 9901,
        majorVersion = 3,
    ),
    V3_1_3_9947(
        build = 9947,
        majorVersion = 3,
    ),
    V3_2_0_10192(
        build = 10192,
        majorVersion = 3,
    ),
    V3_2_0a_10314(
        build = 10314,
        majorVersion = 3,
    ),
    V3_2_2_10482(
        build = 10482,
        majorVersion = 3,
    ),
    V3_2_2a_10505(
        build = 10505,
        majorVersion = 3,
    ),
    V3_3_0_10958(
        build = 10958,
        majorVersion = 3,
    ),
    V3_3_0a_11159(
        build = 11159,
        majorVersion = 3,
    ),
    V3_3_2_11403(
        build = 11403,
        majorVersion = 3,
    ),
    V3_3_3_11685(
        build = 11685,
        majorVersion = 3,
    ),
    V3_3_3a_11723(
        build = 11723,
        majorVersion = 3,
    ),
    V3_3_5_12213(
        build = 12213,
        majorVersion = 3,
    ),
    V3_3_5a_12340(
        build = 12340,
        majorVersion = 3,
    ),

    V4_0_1_13164(
        build = 13164,
        majorVersion = 4,
    ),
    V4_0_1a_13205(
        build = 13205,
        majorVersion = 4,
    ),
    V4_0_3_13329(
        build = 13329,
        majorVersion = 4,
    ),
    V4_0_6_13596(
        build = 13596,
        majorVersion = 4,
    ),
    V4_0_6a_13623(
        build = 13623,
        majorVersion = 4,
    ),
    V4_1_0_13914(
        build = 13914,
        majorVersion = 4,
    ),
    V4_1_0a_14007(
        build = 14007,
        majorVersion = 4,
    ),
    V4_2_0_14333(
        build = 14333,
        majorVersion = 4,
    ),
    V4_2_0a_14480(
        build = 14480,
        majorVersion = 4,
    ),
    V4_2_2_14545(
        build = 14545,
        majorVersion = 4,
    ),
    V4_3_0_15005(
        build = 15005,
        majorVersion = 4,
    ),
    V4_3_0a_15050(
        build = 15050,
        majorVersion = 4,
    ),
    V4_3_2_15211(
        build = 15211,
        majorVersion = 4,
    ),
    V4_3_3_15354(
        build = 15354,
        majorVersion = 4,
    ),
    V4_3_4_15595(
        build = 15595,
        majorVersion = 4,
    ),
    V5_0_4_16016(
        build = 16016,
        majorVersion = 5,
    ),
    V5_0_5_16048(
        build = 16048,
        majorVersion = 5,
    ),
    V5_0_5a_16057(
        build = 16057,
        majorVersion = 5,
    ),
    V5_0_5b_16135(
        build = 16135,
        majorVersion = 5,
    ),
    V5_1_0_16309(
        build = 16309,
        majorVersion = 5,
    ),
    V5_1_0a_16357(
        build = 16357,
        majorVersion = 5,
    ),
    V5_2_0_16650(
        build = 16650,
        majorVersion = 5,
    ),
    V5_2_0_16669(
        build = 16669,
        majorVersion = 5,
    ),
    V5_2_0_16683(
        build = 16683,
        majorVersion = 5,
    ),
    V5_2_0_16685(
        build = 16685,
        majorVersion = 5,
    ),
    V5_2_0_16701(
        build = 16701,
        majorVersion = 5,
    ),
    V5_2_0_16709(
        build = 16709,
        majorVersion = 5,
    ),
    V5_2_0_16716(
        build = 16716,
        majorVersion = 5,
    ),
    V5_2_0_16733(
        build = 16733,
        majorVersion = 5,
    ),
    V5_2_0_16769(
        build = 16769,
        majorVersion = 5,
    ),
    V5_2_0_16826(
        build = 16826,
        majorVersion = 5,
    ),
    V5_3_0_16981(
        build = 16981,
        majorVersion = 5,
    ),
    V5_3_0_16983(
        build = 16983,
        majorVersion = 5,
    ),
    V5_3_0_16992(
        build = 16992,
        majorVersion = 5,
    ),
    V5_3_0_17055(
        build = 17055,
        majorVersion = 5,
    ),
    V5_3_0_17116(
        build = 17116,
        majorVersion = 5,
    ),
    V5_3_0_17128(
        build = 17128,
        majorVersion = 5,
    ),
    V5_4_0_17359(
        build = 17359,
        majorVersion = 5,
    ),
    V5_4_0_17371(
        build = 17371,
        majorVersion = 5,
    ),
    V5_4_0_17399(
        build = 17399,
        majorVersion = 5,
    ),
    V5_4_1_17538(
        build = 17538,
        majorVersion = 5,
    ),
    V5_4_2_17658(
        build = 17658,
        majorVersion = 5,
    ),
    V5_4_2_17688(
        build = 17688,
        majorVersion = 5,
    ),
    V5_4_7_17898(
        build = 17898,
        majorVersion = 5,
    ),
    V5_4_7_17930(
        build = 17930,
        majorVersion = 5,
    ),
    V5_4_7_17956(
        build = 17956,
        majorVersion = 5,
    ),
    V5_4_7_18019(
        build = 18019,
        majorVersion = 5,
    ),
    V5_4_8_18291(
        build = 18291,
        majorVersion = 5,
    ),
    V5_4_8_18414(
        build = 18414,
        majorVersion = 5,
    ),
    V6_0_2_19033(
        build = 19033,
        majorVersion = 6,
    ),
    V6_0_2_19034(
        build = 19034,
        majorVersion = 6,
    ),
    V6_0_3_19103(
        build = 19103,
        majorVersion = 6,
    ),
    V6_0_3_19116(
        build = 19116,
        majorVersion = 6,
    ),
    V6_0_3_19243(
        build = 19243,
        majorVersion = 6,
    ),
    V6_0_3_19342(
        build = 19342,
        majorVersion = 6,
    ),
    V6_1_0_19678(
        build = 19678,
        majorVersion = 6,
    ),
    V6_1_0_19702(
        build = 19702,
        majorVersion = 6,
    ),
    V6_1_2_19802(
        build = 19802,
        majorVersion = 6,
    ),
    V6_1_2_19831(
        build = 19831,
        majorVersion = 6,
    ),
    V6_1_2_19865(
        build = 19865,
        majorVersion = 6,
    ),
    V6_2_0_20173(
        build = 20173,
        majorVersion = 6,
    ),
    V6_2_0_20182(
        build = 20182,
        majorVersion = 6,
    ),
    V6_2_0_20201(
        build = 20201,
        majorVersion = 6,
    ),
    V6_2_0_20216(
        build = 20216,
        majorVersion = 6,
    ),
    V6_2_0_20253(
        build = 20253,
        majorVersion = 6,
    ),
    V6_2_0_20338(
        build = 20338,
        majorVersion = 6,
    ),
    V6_2_2_20444(
        build = 20444,
        majorVersion = 6,
    ),
    V6_2_2a_20490(
        build = 20490,
        majorVersion = 6,
    ),
    V6_2_2a_20574(
        build = 20574,
        majorVersion = 6,
    ),
    V6_2_3_20726(
        build = 20726,
        majorVersion = 6,
    ),
    V6_2_3_20779(
        build = 20779,
        majorVersion = 6,
    ),
    V6_2_3_20886(
        build = 20886,
        majorVersion = 6,
    ),
    V6_2_4_21315(
        build = 21315,
        majorVersion = 6,
    ),
    V6_2_4_21336(
        build = 21336,
        majorVersion = 6,
    ),
    V6_2_4_21343(
        build = 21343,
        majorVersion = 6,
    ),
    V6_2_4_21345(
        build = 21345,
        majorVersion = 6,
    ),
    V6_2_4_21348(
        build = 21348,
        majorVersion = 6,
    ),
    V6_2_4_21355(
        build = 21355,
        majorVersion = 6,
    ),
    V6_2_4_21463(
        build = 21463,
        majorVersion = 6,
    ),
    V6_2_4_21676(
        build = 21676,
        majorVersion = 6,
    ),
    V6_2_4_21742(
        build = 21742,
        majorVersion = 6,
    ),
    V7_0_3_22248(
        build = 22248,
        majorVersion = 7,
    ),
    V7_0_3_22267(
        build = 22267,
        majorVersion = 7,
    ),
    V7_0_3_22277(
        build = 22277,
        majorVersion = 7,
    ),
    V7_0_3_22280(
        build = 22280,
        majorVersion = 7,
    ),
    V7_0_3_22289(
        build = 22289,
        majorVersion = 7,
    ),
    V7_0_3_22293(
        build = 22293,
        majorVersion = 7,
    ),
    V7_0_3_22345(
        build = 22345,
        majorVersion = 7,
    ),
    V7_0_3_22396(
        build = 22396,
        majorVersion = 7,
    ),
    V7_0_3_22410(
        build = 22410,
        majorVersion = 7,
    ),
    V7_0_3_22423(
        build = 22423,
        majorVersion = 7,
    ),
    V7_0_3_22445(
        build = 22445,
        majorVersion = 7,
    ),
    V7_0_3_22498(
        build = 22498,
        majorVersion = 7,
    ),
    V7_0_3_22522(
        build = 22522,
        majorVersion = 7,
    ),
    V7_0_3_22566(
        build = 22566,
        majorVersion = 7,
    ),
    V7_0_3_22594(
        build = 22594,
        majorVersion = 7,
    ),
    V7_0_3_22624(
        build = 22624,
        majorVersion = 7,
    ),
    V7_0_3_22747(
        build = 22747,
        majorVersion = 7,
    ),
    V7_0_3_22810(
        build = 22810,
        majorVersion = 7,
    ),
    V7_1_0_22900(
        build = 22900,
        majorVersion = 7,
    ),
    V7_1_0_22908(
        build = 22908,
        majorVersion = 7,
    ),
    V7_1_0_22950(
        build = 22950,
        majorVersion = 7,
    ),
    V7_1_0_22989(
        build = 22989,
        majorVersion = 7,
    ),
    V7_1_0_22995(
        build = 22995,
        majorVersion = 7,
    ),
    V7_1_0_22996(
        build = 22996,
        majorVersion = 7,
    ),
    V7_1_0_23171(
        build = 23171,
        majorVersion = 7,
    ),
    V7_1_0_23222(
        build = 23222,
        majorVersion = 7,
    ),
    V7_1_5_23360(
        build = 23360,
        majorVersion = 7,
    ),
    V7_1_5_23420(
        build = 23420,
        majorVersion = 7,
    ),
    V7_2_0_23706(
        build = 23706,
        majorVersion = 7,
    ),
    V7_2_0_23826(
        build = 23826,
        majorVersion = 7,
    ),
    V7_2_0_23835(
        build = 23835,
        majorVersion = 7,
    ),
    V7_2_0_23836(
        build = 23836,
        majorVersion = 7,
    ),
    V7_2_0_23846(
        build = 23846,
        majorVersion = 7,
    ),
    V7_2_0_23852(
        build = 23852,
        majorVersion = 7,
    ),
    V7_2_0_23857(
        build = 23857,
        majorVersion = 7,
    ),
    V7_2_0_23877(
        build = 23877,
        majorVersion = 7,
    ),
    V7_2_0_23911(
        build = 23911,
        majorVersion = 7,
    ),
    V7_2_0_23937(
        build = 23937,
        majorVersion = 7,
    ),
    V7_2_0_24015(
        build = 24015,
        majorVersion = 7,
    ),
    V7_2_5_24330(
        build = 24330,
        majorVersion = 7,
    ),
    V7_2_5_24367(
        build = 24367,
        majorVersion = 7,
    ),
    V7_2_5_24414(
        build = 24414,
        majorVersion = 7,
    ),
    V7_2_5_24415(
        build = 24415,
        majorVersion = 7,
    ),
    V7_2_5_24430(
        build = 24430,
        majorVersion = 7,
    ),
    V7_2_5_24461(
        build = 24461,
        majorVersion = 7,
    ),
    V7_2_5_24742(
        build = 24742,
        majorVersion = 7,
    ),
    V7_3_0_24920(
        build = 24920,
        majorVersion = 7,
    ),
    V7_3_0_24931(
        build = 24931,
        majorVersion = 7,
    ),
    V7_3_0_24956(
        build = 24956,
        majorVersion = 7,
    ),
    V7_3_0_24970(
        build = 24970,
        majorVersion = 7,
    ),
    V7_3_0_24974(
        build = 24974,
        majorVersion = 7,
    ),
    V7_3_0_25021(
        build = 25021,
        majorVersion = 7,
    ),
    V7_3_0_25195(
        build = 25195,
        majorVersion = 7,
    ),
    V7_3_2_25383(
        build = 25383,
        majorVersion = 7,
    ),
    V7_3_2_25442(
        build = 25442,
        majorVersion = 7,
    ),
    V7_3_2_25455(
        build = 25455,
        majorVersion = 7,
    ),
    V7_3_2_25477(
        build = 25477,
        majorVersion = 7,
    ),
    V7_3_2_25480(
        build = 25480,
        majorVersion = 7,
    ),
    V7_3_2_25497(
        build = 25497,
        majorVersion = 7,
    ),
    V7_3_2_25549(
        build = 25549,
        majorVersion = 7,
    ),

    V7_3_5_25848(
        build = 25848,
        majorVersion = 7,
    ),
    V7_3_5_25860(
        build = 25860,
        majorVersion = 7,
    ),
    V7_3_5_25864(
        build = 25864,
        majorVersion = 7,
    ),
    V7_3_5_25875(
        build = 25875,
        majorVersion = 7,
    ),
    V7_3_5_25881(
        build = 25881,
        majorVersion = 7,
    ),
    V7_3_5_25901(
        build = 25901,
        majorVersion = 7,
    ),
    V7_3_5_25928(
        build = 25928,
        majorVersion = 7,
    ),
    V7_3_5_25937(
        build = 25937,
        majorVersion = 7,
    ),
    V7_3_5_25944(
        build = 25944,
        majorVersion = 7,
    ),
    V7_3_5_25946(
        build = 25946,
        majorVersion = 7,
    ),
    V7_3_5_25950(
        build = 25950,
        majorVersion = 7,
    ),
    V7_3_5_25961(
        build = 25961,
        majorVersion = 7,
    ),
    V7_3_5_25996(
        build = 25996,
        majorVersion = 7,
    ),
    V7_3_5_26124(
        build = 26124,
        majorVersion = 7,
    ),
    V7_3_5_26365(
        build = 26365,
        majorVersion = 7,
    ),
    V7_3_5_26654(
        build = 26654,
        majorVersion = 7,
    ),
    V7_3_5_26755(
        build = 26755,
        majorVersion = 7,
    ),
    V7_3_5_26822(
        build = 26822,
        majorVersion = 7,
    ),
    V7_3_5_26899(
        build = 26899,
        majorVersion = 7,
    ),
    V7_3_5_26972(
        build = 26972,
        majorVersion = 7,
    ),

    V8_0_1_27101(
        build = 27101,
        majorVersion = 8,
    ),
    V8_0_1_27144(
        build = 27144,
        majorVersion = 8,
    ),
    V8_0_1_27165(
        build = 27165,
        majorVersion = 8,
    ),
    V8_0_1_27178(
        build = 27178,
        majorVersion = 8,
    ),
    V8_0_1_27219(
        build = 27219,
        majorVersion = 8,
    ),
    V8_0_1_27291(
        build = 27291,
        majorVersion = 8,
    ),
    V8_0_1_27326(
        build = 27326,
        majorVersion = 8,
    ),
    V8_0_1_27355(
        build = 27355,
        majorVersion = 8,
    ),
    V8_0_1_27356(
        build = 27356,
        majorVersion = 8,
    ),
    V8_0_1_27366(
        build = 27366,
        majorVersion = 8,
    ),
    V8_0_1_27377(
        build = 27377,
        majorVersion = 8,
    ),
    V8_0_1_27404(
        build = 27404,
        majorVersion = 8,
    ),
    V8_0_1_27481(
        build = 27481,
        majorVersion = 8,
    ),
    V8_0_1_27547(
        build = 27547,
        majorVersion = 8,
    ),
    V8_0_1_27602(
        build = 27602,
        majorVersion = 8,
    ),
    V8_0_1_27791(
        build = 27791,
        majorVersion = 8,
    ),
    V8_0_1_27843(
        build = 27843,
        majorVersion = 8,
    ),
    V8_0_1_27980(
        build = 27980,
        majorVersion = 8,
    ),
    V8_0_1_28153(
        build = 28153,
        majorVersion = 8,
    ),

    V8_1_0_28724(
        build = 28724,
        majorVersion = 8,
    ),
    V8_1_0_28768(
        build = 28768,
        majorVersion = 8,
    ),
    V8_1_0_28807(
        build = 28807,
        majorVersion = 8,
    ),
    V8_1_0_28822(
        build = 28822,
        majorVersion = 8,
    ),
    V8_1_0_28833(
        build = 28833,
        majorVersion = 8,
    ),
    V8_1_0_29088(
        build = 29088,
        majorVersion = 8,
    ),
    V8_1_0_29139(
        build = 29139,
        majorVersion = 8,
    ),
    V8_1_0_29235(
        build = 29235,
        majorVersion = 8,
    ),
    V8_1_0_29285(
        build = 29285,
        majorVersion = 8,
    ),
    V8_1_0_29297(
        build = 29297,
        majorVersion = 8,
    ),
    V8_1_0_29482(
        build = 29482,
        majorVersion = 8,
    ),
    V8_1_0_29600(
        build = 29600,
        majorVersion = 8,
    ),
    V8_1_0_29621(
        build = 29621,
        majorVersion = 8,
    ),

    V8_1_5_29683(
        build = 29683,
        majorVersion = 8,
    ),
    V8_1_5_29701(
        build = 29701,
        majorVersion = 8,
    ),
    V8_1_5_29704(
        build = 29704,
        majorVersion = 8,
    ),
    V8_1_5_29705(
        build = 29705,
        majorVersion = 8,
    ),
    V8_1_5_29718(
        build = 29718,
        majorVersion = 8,
    ),
    V8_1_5_29732(
        build = 29732,
        majorVersion = 8,
    ),
    V8_1_5_29737(
        build = 29737,
        majorVersion = 8,
    ),
    V8_1_5_29814(
        build = 29814,
        majorVersion = 8,
    ),
    V8_1_5_29869(
        build = 29869,
        majorVersion = 8,
    ),
    V8_1_5_29896(
        build = 29896,
        majorVersion = 8,
    ),
    V8_1_5_29981(
        build = 29981,
        majorVersion = 8,
    ),
    V8_1_5_30477(
        build = 30477,
        majorVersion = 8,
    ),
    V8_1_5_30706(
        build = 30706,
        majorVersion = 8,
    ),

    V8_2_0_30898(
        build = 30898,
        majorVersion = 8,
    ),
    V8_2_0_30918(
        build = 30918,
        majorVersion = 8,
    ),
    V8_2_0_30920(
        build = 30920,
        majorVersion = 8,
    ),
    V8_2_0_30948(
        build = 30948,
        majorVersion = 8,
    ),
    V8_2_0_30993(
        build = 30993,
        majorVersion = 8,
    ),
    V8_2_0_31229(
        build = 31229,
        majorVersion = 8,
    ),
    V8_2_0_31429(
        build = 31429,
        majorVersion = 8,
    ),
    V8_2_0_31478(
        build = 31478,
        majorVersion = 8,
    ),

    V8_2_5_31921(
        build = 31921,
        majorVersion = 8,
    ),
    V8_2_5_31958(
        build = 31958,
        majorVersion = 8,
    ),
    V8_2_5_31960(
        build = 31960,
        majorVersion = 8,
    ),
    V8_2_5_31961(
        build = 31961,
        majorVersion = 8,
    ),
    V8_2_5_31984(
        build = 31984,
        majorVersion = 8,
    ),
    V8_2_5_32028(
        build = 32028,
        majorVersion = 8,
    ),
    V8_2_5_32144(
        build = 32144,
        majorVersion = 8,
    ),
    V8_2_5_32185(
        build = 32185,
        majorVersion = 8,
    ),
    V8_2_5_32265(
        build = 32265,
        majorVersion = 8,
    ),
    V8_2_5_32294(
        build = 32294,
        majorVersion = 8,
    ),
    V8_2_5_32305(
        build = 32305,
        majorVersion = 8,
    ),
    V8_2_5_32494(
        build = 32494,
        majorVersion = 8,
    ),
    V8_2_5_32580(
        build = 32580,
        majorVersion = 8,
    ),
    V8_2_5_32638(
        build = 32638,
        majorVersion = 8,
    ),
    V8_2_5_32722(
        build = 32722,
        majorVersion = 8,
    ),
    V8_2_5_32750(
        build = 32750,
        majorVersion = 8,
    ),
    V8_2_5_32978(
        build = 32978,
        majorVersion = 8,
    ),

    V8_3_0_33062(
        build = 33062,
        majorVersion = 8,
    ),
    V8_3_0_33073(
        build = 33073,
        majorVersion = 8,
    ),
    V8_3_0_33084(
        build = 33084,
        majorVersion = 8,
    ),
    V8_3_0_33095(
        build = 33095,
        majorVersion = 8,
    ),
    V8_3_0_33115(
        build = 33115,
        majorVersion = 8,
    ),
    V8_3_0_33169(
        build = 33169,
        majorVersion = 8,
    ),
    V8_3_0_33237(
        build = 33237,
        majorVersion = 8,
    ),
    V8_3_0_33369(
        build = 33369,
        majorVersion = 8,
    ),
    V8_3_0_33528(
        build = 33528,
        majorVersion = 8,
    ),
    V8_3_0_33724(
        build = 33724,
        majorVersion = 8,
    ),
    V8_3_0_33775(
        build = 33775,
        majorVersion = 8,
    ),
    V8_3_0_33941(
        build = 33941,
        majorVersion = 8,
    ),
    V8_3_0_34220(
        build = 34220,
        majorVersion = 8,
    ),
    V8_3_0_34601(
        build = 34601,
        majorVersion = 8,
    ),
    V8_3_0_34769(
        build = 34769,
        majorVersion = 8,
    ),
    V8_3_0_34963(
        build = 34963,
        majorVersion = 8,
    ),

    V8_3_7_35249(
        build = 35249,
        majorVersion = 8,
    ),
    V8_3_7_35284(
        build = 35284,
        majorVersion = 8,
    ),
    V8_3_7_35435(
        build = 35435,
        majorVersion = 8,
    ),
    V8_3_7_35662(
        build = 35662,
        majorVersion = 8,
    ),

    V9_0_1_36216(
        build = 36216,
        majorVersion = 9,
    ),
    V9_0_1_36228(
        build = 36228,
        majorVersion = 9,
    ),
    V9_0_1_36230(
        build = 36230,
        majorVersion = 9,
    ),
    V9_0_1_36247(
        build = 36247,
        majorVersion = 9,
    ),
    V9_0_1_36272(
        build = 36272,
        majorVersion = 9,
    ),
    V9_0_1_36322(
        build = 36322,
        majorVersion = 9,
    ),
    V9_0_1_36372(
        build = 36372,
        majorVersion = 9,
    ),
    V9_0_1_36492(
        build = 36492,
        majorVersion = 9,
    ),
    V9_0_1_36577(
        build = 36577,
        majorVersion = 9,
    ),

    V9_0_2_36639(
        build = 36639,
        majorVersion = 9,
    ),
    V9_0_2_36665(
        build = 36665,
        majorVersion = 9,
    ),
    V9_0_2_36671(
        build = 36671,
        majorVersion = 9,
    ),
    V9_0_2_36710(
        build = 36710,
        majorVersion = 9,
    ),
    V9_0_2_36734(
        build = 36734,
        majorVersion = 9,
    ),
    V9_0_2_36751(
        build = 36751,
        majorVersion = 9,
    ),
    V9_0_2_36753(
        build = 36753,
        majorVersion = 9,
    ),
    V9_0_2_36839(
        build = 36839,
        majorVersion = 9,
    ),
    V9_0_2_36949(
        build = 36949,
        majorVersion = 9,
    ),
    V9_0_2_37106(
        build = 37106,
        majorVersion = 9,
    ),
    V9_0_2_37142(
        build = 37142,
        majorVersion = 9,
    ),
    V9_0_2_37176(
        build = 37176,
        majorVersion = 9,
    ),
    V9_0_2_37474(
        build = 37474,
        majorVersion = 9,
    ),

    V9_0_5_37503(
        build = 37503,
        majorVersion = 9,
    ), // PTR
    V9_0_5_37862(
        build = 37862,
        majorVersion = 9,
    ),
    V9_0_5_37864(
        build = 37864,
        majorVersion = 9,
    ),
    V9_0_5_37893(
        build = 37893,
        majorVersion = 9,
    ),
    V9_0_5_37899(
        build = 37899,
        majorVersion = 9,
    ),
    V9_0_5_37988(
        build = 37988,
        majorVersion = 9,
    ),
    V9_0_5_38134(
        build = 38134,
        majorVersion = 9,
    ),
    V9_0_5_38556(
        build = 38556,
        majorVersion = 9,
    ),

    V9_1_0_39185(
        build = 39185,
        majorVersion = 9,
    ),
    V9_1_0_39226(
        build = 39226,
        majorVersion = 9,
    ),
    V9_1_0_39229(
        build = 39229,
        majorVersion = 9,
    ),
    V9_1_0_39262(
        build = 39262,
        majorVersion = 9,
    ),
    V9_1_0_39282(
        build = 39282,
        majorVersion = 9,
    ),
    V9_1_0_39289(
        build = 39289,
        majorVersion = 9,
    ),
    V9_1_0_39291(
        build = 39291,
        majorVersion = 9,
    ),
    V9_1_0_39318(
        build = 39318,
        majorVersion = 9,
    ),
    V9_1_0_39335(
        build = 39335,
        majorVersion = 9,
    ),
    V9_1_0_39427(
        build = 39427,
        majorVersion = 9,
    ),
    V9_1_0_39497(
        build = 39497,
        majorVersion = 9,
    ),
    V9_1_0_39498(
        build = 39498,
        majorVersion = 9,
    ),
    V9_1_0_39584(
        build = 39584,
        majorVersion = 9,
    ),
    V9_1_0_39617(
        build = 39617,
        majorVersion = 9,
    ),
    V9_1_0_39653(
        build = 39653,
        majorVersion = 9,
    ),
    V9_1_0_39804(
        build = 39804,
        majorVersion = 9,
    ),
    V9_1_0_40000(
        build = 40000,
        majorVersion = 9,
    ),
    V9_1_0_40120(
        build = 40120,
        majorVersion = 9,
    ),
    V9_1_0_40443(
        build = 40443,
        majorVersion = 9,
    ),
    V9_1_0_40593(
        build = 40593,
        majorVersion = 9,
    ),
    V9_1_0_40725(
        build = 40725,
        majorVersion = 9,
    ),

    V9_1_5_40772(
        build = 40772,
        majorVersion = 9,
    ),
    V9_1_5_40871(
        build = 40871,
        majorVersion = 9,
    ),
    V9_1_5_40906(
        build = 40906,
        majorVersion = 9,
    ),
    V9_1_5_40944(
        build = 40944,
        majorVersion = 9,
    ),
    V9_1_5_40966(
        build = 40966,
        majorVersion = 9,
    ),
    V9_1_5_41031(
        build = 41031,
        majorVersion = 9,
    ),
    V9_1_5_41079(
        build = 41079,
        majorVersion = 9,
    ),
    V9_1_5_41288(
        build = 41288,
        majorVersion = 9,
    ),
    V9_1_5_41323(
        build = 41323,
        majorVersion = 9,
    ),
    V9_1_5_41359(
        build = 41359,
        majorVersion = 9,
    ),
    V9_1_5_41488(
        build = 41488,
        majorVersion = 9,
    ),
    V9_1_5_41793(
        build = 41793,
        majorVersion = 9,
    ),
    V9_1_5_42010(
        build = 42010,
        majorVersion = 9,
    ),

    // Classic
    V1_13_2_31446(
        build = 31446,
        majorVersion = 1,
    ), // name reservation
    V1_13_2_31650(
        build = 31650,
        majorVersion = 1,
    ), // launch
    V1_13_2_31687(
        build = 31687,
        majorVersion = 1,
    ),
    V1_13_2_31727(
        build = 31727,
        majorVersion = 1,
    ),
    V1_13_2_31830(
        build = 31830,
        majorVersion = 1,
    ),
    V1_13_2_31882(
        build = 31882,
        majorVersion = 1,
    ),
    V1_13_2_32089(
        build = 32089,
        majorVersion = 1,
    ),
    V1_13_2_32421(
        build = 32421,
        majorVersion = 1,
    ),
    V1_13_2_32600(
        build = 32600,
        majorVersion = 1,
    ),

    V1_13_3_32790(
        build = 32790,
        majorVersion = 1,
    ),
    V1_13_3_32836(
        build = 32836,
        majorVersion = 1,
    ),
    V1_13_3_32887(
        build = 32887,
        majorVersion = 1,
    ),
    V1_13_3_33155(
        build = 33155,
        majorVersion = 1,
    ),
    V1_13_3_33302(
        build = 33302,
        majorVersion = 1,
    ), // bwl release
    V1_13_3_33485(
        build = 33485,
        majorVersion = 1,
    ), // ptr
    V1_13_3_33526(
        build = 33526,
        majorVersion = 1,
    ),

    V1_13_4_33491(
        build = 33491,
        majorVersion = 1,
    ), // ptr
    V1_13_4_33598(
        build = 33598,
        majorVersion = 1,
    ), // both live and ptr
    V1_13_4_33645(
        build = 33645,
        majorVersion = 1,
    ), // both live and ptr
    V1_13_4_33728(
        build = 33728,
        majorVersion = 1,
    ), // both live and ptr
    V1_13_4_33920(
        build = 33920,
        majorVersion = 1,
    ), // both live and ptr
    v1_13_4_34219(
        build = 34219,
        majorVersion = 1,
    ),
    v1_13_4_34266(
        build = 34266,
        majorVersion = 1,
    ),
    v1_13_4_34600(
        build = 34600,
        majorVersion = 1,
    ),
    v1_13_4_34835(
        build = 34835,
        majorVersion = 1,
    ),

    v1_13_5_34713(
        build = 34713,
        majorVersion = 1,
    ), // ptr
    v1_13_5_34911(
        build = 34911,
        majorVersion = 1,
    ), // ptr
    v1_13_5_35000(
        build = 35000,
        majorVersion = 1,
    ), // both live and ptr
    v1_13_5_35186(
        build = 35186,
        majorVersion = 1,
    ),
    v1_13_5_35395(
        build = 35395,
        majorVersion = 1,
    ),
    v1_13_5_35663(
        build = 35663,
        majorVersion = 1,
    ),
    v1_13_5_35705(
        build = 35705,
        majorVersion = 1,
    ),
    v1_13_5_35753(
        build = 35753,
        majorVersion = 1,
    ),
    v1_13_5_36035(
        build = 36035,
        majorVersion = 1,
    ),
    v1_13_5_36307(
        build = 36307,
        majorVersion = 1,
    ),
    v1_13_5_36325(
        build = 36325,
        majorVersion = 1,
    ),

    v1_13_6_36149(
        build = 36149,
        majorVersion = 1,
    ), // ptr
    v1_13_6_36231(
        build = 36231,
        majorVersion = 1,
    ), // ptr
    v1_13_6_36310(
        build = 36310,
        majorVersion = 1,
    ), // ptr
    V1_13_6_36324(
        build = 36324,
        majorVersion = 1,
    ), // ptr
    V1_13_6_36497(
        build = 36497,
        majorVersion = 1,
    ), // ptr
    V1_13_6_36524(
        build = 36524,
        majorVersion = 1,
    ), // ptr
    V1_13_6_36611(
        build = 36611,
        majorVersion = 1,
    ), // ptr
    V1_13_6_36670(
        build = 36670,
        majorVersion = 1,
    ), // ptr
    V1_13_6_36714(
        build = 36714,
        majorVersion = 1,
    ), // both live and ptr
    V1_13_6_36935(
        build = 36935,
        majorVersion = 1,
    ), // both live and ptr
    V1_13_6_37497(
        build = 37497,
        majorVersion = 1,
    ),

    // Some-Changes Policy Begins
    V1_13_7_37279(
        build = 37279,
        majorVersion = 1,
    ), // ptr
    V1_13_7_37429(
        build = 37429,
        majorVersion = 1,
    ), // ptr
    V1_13_7_37892(
        build = 37892,
        majorVersion = 1,
    ), // ptr
    V1_13_7_38178(
        build = 38178,
        majorVersion = 1,
    ), // ptr
    V1_13_7_38296(
        build = 38296,
        majorVersion = 1,
    ), // ptr
    V1_13_7_38363(
        build = 38363,
        majorVersion = 1,
    ), // both live and ptr
    V1_13_7_38386(
        build = 38386,
        majorVersion = 1,
    ),
    V1_13_7_38475(
        build = 38475,
        majorVersion = 1,
    ),
    V1_13_7_38631(
        build = 38631,
        majorVersion = 1,
    ), // last version before tbc pre patch
    V1_13_7_38704(
        build = 38704,
        majorVersion = 1,
    ),
    V1_13_7_39605(
        build = 39605,
        majorVersion = 1,
    ), // both live and ptr
    V1_13_7_39692(
        build = 39692,
        majorVersion = 1,
    ),

    // Classic-Era rebased upon TBC client
    V1_14_0_39802(
        build = 39802,
        majorVersion = 1,
    ), // ptr
    V1_14_0_39958(
        build = 39958,
        majorVersion = 1,
    ), // ptr
    V1_14_0_40140(
        build = 40140,
        majorVersion = 1,
    ), // ptr
    V1_14_0_40179(
        build = 40179,
        majorVersion = 1,
    ), // ptr
    V1_14_0_40237(
        build = 40237,
        majorVersion = 1,
    ), // ptr
    V1_14_0_40347(
        build = 40347,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_0_40441(
        build = 40441,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_0_40618(
        build = 40618,
        majorVersion = 1,
    ),

    // Classic SoM
    V1_14_1_40487(
        build = 40487,
        majorVersion = 1,
    ), // ptr
    V1_14_1_40594(
        build = 40594,
        majorVersion = 1,
    ), // ptr
    V1_14_1_40666(
        build = 40666,
        majorVersion = 1,
    ), // ptr
    V1_14_1_40688(
        build = 40688,
        majorVersion = 1,
    ), // ptr
    V1_14_1_40800(
        build = 40800,
        majorVersion = 1,
    ), // ptr
    V1_14_1_40818(
        build = 40818,
        majorVersion = 1,
    ), // ptr
    V1_14_1_40926(
        build = 40926,
        majorVersion = 1,
    ), // ptr
    V1_14_1_40962(
        build = 40962,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_1_41009(
        build = 41009,
        majorVersion = 1,
    ), // ptr
    V1_14_1_41030(
        build = 41030,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_1_41077(
        build = 41077,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_1_41137(
        build = 41137,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_1_41243(
        build = 41243,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_1_41511(
        build = 41511,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_1_41794(
        build = 41794,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_1_42032(
        build = 42032,
        majorVersion = 1,
    ), // live

    V1_14_2_41858(
        build = 41858,
        majorVersion = 1,
    ), // ptr
    V1_14_2_41959(
        build = 41959,
        majorVersion = 1,
    ), // ptr
    V1_14_2_42065(
        build = 42065,
        majorVersion = 1,
    ), // ptr
    V1_14_2_42082(
        build = 42082,
        majorVersion = 1,
    ), // ptr
    V1_14_2_42214(
        build = 42214,
        majorVersion = 1,
    ), // both live and ptr
    V1_14_2_42597(
        build = 42597,
        majorVersion = 1,
    ), // both live and ptr

    // TBC Classic
    V2_5_1_38598(
        build = 38598,
        majorVersion = 2,
    ), // ptr
    V2_5_1_38644(
        build = 38644,
        majorVersion = 2,
    ),
    V2_5_1_38707(
        build = 38707,
        majorVersion = 2,
    ), // pre patch
    V2_5_1_38741(
        build = 38741,
        majorVersion = 2,
    ),
    V2_5_1_38757(
        build = 38757,
        majorVersion = 2,
    ),
    V2_5_1_38835(
        build = 38835,
        majorVersion = 2,
    ), // launch
    V2_5_1_38892(
        build = 38892,
        majorVersion = 2,
    ),
    V2_5_1_38921(
        build = 38921,
        majorVersion = 2,
    ),
    V2_5_1_38988(
        build = 38988,
        majorVersion = 2,
    ),
    V2_5_1_39170(
        build = 39170,
        majorVersion = 2,
    ),
    V2_5_1_39475(
        build = 39475,
        majorVersion = 2,
    ),
    V2_5_1_39603(
        build = 39603,
        majorVersion = 2,
    ),
    V2_5_1_39640(
        build = 39640,
        majorVersion = 2,
    ),

    V2_5_2_39570(
        build = 39570,
        majorVersion = 2,
    ), // ptr
    V2_5_2_39618(
        build = 39618,
        majorVersion = 2,
    ), // ptr
    V2_5_2_39926(
        build = 39926,
        majorVersion = 2,
    ), // ptr
    V2_5_2_40011(
        build = 40011,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_2_40045(
        build = 40045,
        majorVersion = 2,
    ), // live
    V2_5_2_40203(
        build = 40203,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_2_40260(
        build = 40260,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_2_40422(
        build = 40422,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_2_40488(
        build = 40488,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_2_40617(
        build = 40617,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_2_40892(
        build = 40892,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_2_41446(
        build = 41446,
        majorVersion = 2,
    ), // live
    V2_5_2_41510(
        build = 41510,
        majorVersion = 2,
    ), // live

    V2_5_3_41402(
        build = 41402,
        majorVersion = 2,
    ), // ptr
    V2_5_3_41531(
        build = 41531,
        majorVersion = 2,
    ), // ptr
    V2_5_3_41750(
        build = 41750,
        majorVersion = 2,
    ), // ptr
    V2_5_3_41812(
        build = 41812,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_3_42083(
        build = 42083,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_3_42328(
        build = 42328,
        majorVersion = 2,
    ), // both live and ptr
    V2_5_3_42598(
        build = 42598,
        majorVersion = 2,
    ), // live

    // WotLK Classic
    V3_4_3_54261(
        build = 54261,
        majorVersion = 3
    ); // WotLK Classic 3.4.3

    fun toBestLegacyVersion(): ClientVersionBuild = when (majorVersion) {
        1 -> V1_12_1_5875
        2 -> V2_4_3_8606
        3 -> V3_3_5a_12340
        else -> Zero
    }
    
    companion object {
        fun fromBuildOrNull(
            build: Int
        ): ClientVersionBuild? = ClientVersionBuild.entries.firstOrNull {
            it.build == build 
        } 
    }
}
