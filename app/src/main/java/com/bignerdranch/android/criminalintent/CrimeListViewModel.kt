package com.bignerdranch.android.criminalintent

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.criminalintent.database.CrimeRepository

class CrimeListViewModel: ViewModel() {
    /* Initially written to populate the ViewModel for testing while working with RecyclerView
 * */
//    val crimes = mutableListOf<Crime>()
//    init { // populate the list with dummy data
//        for (i in 0 until 100) {
//            val crime = Crime()
//            crime.title = "Crime #$i"
//            crime.isSolved= i % 2 == 0
//            crimes += crime
//        }
//    }

    // Calls crimeRepository to get the crimes from the already existant database.
    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()

}