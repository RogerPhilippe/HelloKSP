package br.com.phs.helloksp

import android.os.Bundle

interface Event {

    fun getHashMapOfParamsForCustomAnalytics(): HashMap<*, *>?
    fun getBundleOfParamsForFirebase(): Bundle

}