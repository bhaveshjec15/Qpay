package com.qpay.android.utils

import android.content.Context
import androidx.core.os.ConfigurationCompat
import java.util.Locale

/**
 * This class will provide a nearby active context.
 * Usually, its should be the visible activity context.
 * In the application start this class contains an application context.
 */

 class CurrentContext(private var context: Context) {
 // private var currentLocale: Locale = Locale.getDefault()

  /**
   * Attach the current [context].
   * Should be called in activity onCreate().
   */
  fun attachBaseContext(base: Context) {
    this.context = base
   // currentLocale = getCurrentLocale()
  }

  fun get() = context

 // fun getLocale() = currentLocale

  /**
   * The method returns the correct current locale.
   * The correct locale is the locale from the list of locales on the device
   * that supports by this application. If the correct language is not found,
   * we always choose the default language - English (USA).
   */
  private fun getCurrentLocale(): Locale {
    var correctLocale: Locale? = null
    val locales = ConfigurationCompat.getLocales(context.resources.configuration)
    for (i in 0 until locales.size()) {
      // If the new locale is added this part should be increased.
      if (locales[i].language == "en") {
        correctLocale = locales[i]
      }
    }
    return correctLocale ?: Locale.US
  }
}