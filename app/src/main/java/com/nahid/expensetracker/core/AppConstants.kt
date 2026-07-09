package com.nahid.expensetracker.core

import kotlinx.serialization.json.Json

object AppConstants {
    const val DEV_MODE = true

    const val ENABLE_LOG = DEV_MODE
    const val AUTO_LOGIN = DEV_MODE

    const val DATABASE_VERSION: Int = 1
    const val DB_NAME: String = "Expanse_Database"
    const val PREF_NAME: String = "Expanse Tracker"
    const val APP_MARGIN: Int = 8
    const val WEB_CLIENT_ID =
        "447605416637-ckeujgaavdn7frnigrd0g2745vqjc5qa.apps.googleusercontent.com"

    val json = Json { ignoreUnknownKeys = true }


}