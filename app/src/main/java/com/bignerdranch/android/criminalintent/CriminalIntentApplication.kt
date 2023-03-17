package com.bignerdranch.android.criminalintent

import android.app.Application
import com.bignerdranch.android.criminalintent.database.CrimeRepository

// This allows you to access lifecycle information about the application itself.

class CriminalIntentApplication: Application() {

    override fun onCreate() { // Application.onCreate()  [...not the Activity.onCreate() one]
        /* .onCreate() is called when the application is first loaded into memory.
        This makes it a good time to do any kind of one-time initialization operations.
         */
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}