package com.anushka.viewmodeldemo1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private var count = MutableLiveData<Int>()
    val currentCount: LiveData<Int>
    get() = count

    init {
        count.value = 0
    }

    fun updateCount(){
        count.value?.let {
            count.value = it + 1
        }
    }
}