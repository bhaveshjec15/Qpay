package com.qpay.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.view.View

class QPayApplication : Application() {

  val MyPREFERENCES = "MyPrefs"
//  @Inject
//  lateinit var networkFlipperPlugin: NetworkFlipperPlugin


  /*@Inject
  lateinit var notificationWillShowInForegroundHandler: NotificationWillShowInForegroundHandler
*/
 /* @ExperimentalCoroutinesApi*/
  override fun onCreate() {
    super.onCreate()
    QPayApplication.appContext = applicationContext
    sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
   /* Instabug.Builder(this, "44a61b51f02880311f90537c3c08e4d3")
      .build()*/
  }

  companion object {

    lateinit  var appContext: Context
    var sharedpreferences: SharedPreferences? = null
  }


}

/*fun setLocale(context: Context): Context {
  val sharedPreffs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
  val stringStore = SharedPrefsStringStore(sharedPreffs)
  val locRepo = LanguageRepository(stringStore)
  var newContext = context

  var savedLocaleCode = locRepo.getLocalCode()?.toLowerCase(Locale.ENGLISH)
  Log.e("mcheck", "setLocale saved code $savedLocaleCode")
  // obtain correct locale, en - defualt
  if (savedLocaleCode == null) {
    var localeCode = if (VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      context.resources.configuration.locales.get(0).language
    } else {
      context.resources.configuration.locale.language

    }
    if (localeCode == "en" || localeCode == "id" || localeCode == "in") {
      savedLocaleCode = localeCode
    } else {
      savedLocaleCode = "en"
    }
    locRepo.saveLocalCode(savedLocaleCode)

  }
  val locName = if (savedLocaleCode == "en") {
    context.getString(R.string.english)
  } else {
    context.getString(R.string.bahasa)
  }
  Log.e("mcheck","locName $locName")
  locRepo.saveLocaleName(locName)
  Log.e("mcheck", "setLocale derived code $savedLocaleCode")
  val locale = Locale(savedLocaleCode)
  Locale.setDefault(locale)

  val res = context.resources
  val config = Configuration(res.configuration)
  if (VERSION.SDK_INT >= 17) {
    config.setLocale(locale)
    newContext = context.createConfigurationContext(config)
  } else {
    config.setLocale(locale)
    res.updateConfiguration(config, res.displayMetrics)
  }
  return newContext
}*/

fun getActivity(view: View): Activity? {
  var context: Context? = view.context
  while (context is ContextWrapper) {
    if (context is Activity) {
      return context
    }
    context = context.baseContext
  }
  return null
}