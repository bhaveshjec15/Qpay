package com.qpay.android.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

fun <T1, T2, R> zip(
  src1: LiveData<T1>,
  src2: LiveData<T2>,
  zipper: (T1, T2) -> R
): LiveData<R> {

  return MediatorLiveData<R>().apply {

    var lastSrc1: T1? = null
    var lastSrc2: T2? = null

    fun updateValueIfNeeded() {
      if (lastSrc1 != null && lastSrc2 != null) {
        value = zipper(lastSrc1!!, lastSrc2!!)
      }
    }

    addSource(src1) {
      lastSrc1 = it
      updateValueIfNeeded()
    }

    addSource(src2) {
      lastSrc2 = it
      updateValueIfNeeded()
    }
  }
}

fun <T1, T2, T3, R> zip(
  src1: LiveData<T1>,
  src2: LiveData<T2>,
  src3: LiveData<T3>,
  zipper: (T1, T2, T3) -> R
): LiveData<R> {

  return MediatorLiveData<R>().apply {
    var lastSrc1: T1? = null
    var lastSrc2: T2? = null
    var lastSrc3: T3? = null

    fun updateValueIfNeeded() {
      if (lastSrc1 != null && lastSrc2 != null && lastSrc3 != null) {
        postValue(zipper(lastSrc1!!, lastSrc2!!, lastSrc3!!))
      }
    }

    addSource(src1) {
      lastSrc1 = it
      updateValueIfNeeded()
    }

    addSource(src2) {
      lastSrc2 = it
      updateValueIfNeeded()
    }

    addSource(src3) {
      lastSrc3 = it
      updateValueIfNeeded()
    }
  }
}

fun <T1, T2, T3, T4, R> zip(
  src1: LiveData<T1>,
  src2: LiveData<T2>,
  src3: LiveData<T3>,
  src4: LiveData<T4>,
  zipper: (T1, T2, T3, T4) -> R
): LiveData<R> {

  return MediatorLiveData<R>().apply {
    var lastSrc1: T1? = null
    var lastSrc2: T2? = null
    var lastSrc3: T3? = null
    var lastSrc4: T4? = null

    fun updateValueIfNeeded() {
      if (lastSrc1 != null && lastSrc2 != null && lastSrc3 != null && lastSrc4 != null) {
        postValue(zipper(lastSrc1!!, lastSrc2!!, lastSrc3!!, lastSrc4!!))
      }
    }

    addSource(src1) {
      lastSrc1 = it
      updateValueIfNeeded()
    }

    addSource(src2) {
      lastSrc2 = it
      updateValueIfNeeded()
    }

    addSource(src3) {
      lastSrc3 = it
      updateValueIfNeeded()
    }

    addSource(src4) {
      lastSrc4 = it
      updateValueIfNeeded()
    }
  }
}

fun <T1, T2, T3, T4, T5, T6, R> zip(
  src1: LiveData<T1>,
  src2: LiveData<T2>,
  src3: LiveData<T3>,
  src4: LiveData<T4>,
  src5: LiveData<T5>,
  src6: LiveData<T6>,
  zipper: (T1, T2, T3, T4, T5, T6) -> R
): LiveData<R> {

  return MediatorLiveData<R>().apply {
    var lastSrc1: T1? = null
    var lastSrc2: T2? = null
    var lastSrc3: T3? = null
    var lastSrc4: T4? = null
    var lastSrc5: T5? = null
    var lastSrc6: T6? = null

    fun updateValueIfNeeded() {
      if (lastSrc1 != null && lastSrc2 != null && lastSrc3 != null && lastSrc4 != null && lastSrc5 != null && lastSrc6 != null) {
        postValue(zipper(lastSrc1!!, lastSrc2!!, lastSrc3!!, lastSrc4!!, lastSrc5!!, lastSrc6!!))
      }
    }

    addSource(src1) {
      lastSrc1 = it
      updateValueIfNeeded()
    }

    addSource(src2) {
      lastSrc2 = it
      updateValueIfNeeded()
    }

    addSource(src3) {
      lastSrc3 = it
      updateValueIfNeeded()
    }

    addSource(src4) {
      lastSrc4 = it
      updateValueIfNeeded()
    }
    addSource(src5) {
      lastSrc5 = it
      updateValueIfNeeded()
    }
    addSource(src6) {
      lastSrc6 = it
      updateValueIfNeeded()
    }
  }
}

fun <T> MutableLiveData<T>.forceRefresh() {
  this.value = this.value
}