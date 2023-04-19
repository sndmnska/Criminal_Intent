package com.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.criminalintent.database.CrimeRepository
import java.io.File
import java.util.UUID


// Creating a Transformation Statement
/*A transformation function takes two inputs: a LiveData object used as a trigger and
a mapping function that must return a LiveData object, AKA the Transformation Result*/
class CrimeDetailViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeID: UUID) {
        crimeIdLiveData.value = crimeID
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    // expose File information to CrimeFragment with the below
    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }
}