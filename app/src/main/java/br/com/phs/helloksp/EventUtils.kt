package br.com.phs.helloksp

import android.util.Log

object EventUtils {

    fun postEvent(event: Event) {
        sendFirebaseEvent(event)
        sendCustomAnalyticEvent(event)
    }

    private fun sendFirebaseEvent(event: Event) {
        Log.i("Firebase_Event_fire", event.getBundleOfParamsForFirebase().toString())
    }

    private fun sendCustomAnalyticEvent(event: Event) {
        Log.i("C_Analytics_Event_fire", event.getHashMapOfParamsForCustomAnalytics().toString())
    }

}