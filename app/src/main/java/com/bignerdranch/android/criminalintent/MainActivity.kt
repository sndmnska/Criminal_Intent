package com.bignerdranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*


/** Okay, so my goal for this commented code is to  maybe piece together what parts of the code do.
 * I have a relatively loose grasp on how Fragments work. */

// start here

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // setting the default view pane

        // set the fragment container inside of activity_main
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) { // if there is no current fragment
            val fragment =
                CrimeListFragment.newInstance() // create a new CrimeFragment using the companion object
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.fragment_container,
                    fragment
                ) // add the fragment to the fragment container
                .commit() // commit the "transaction" in .beginTransaction
        }
    }

    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}