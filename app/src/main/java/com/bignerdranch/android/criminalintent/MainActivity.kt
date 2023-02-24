package com.bignerdranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


/** Okay, so my goal for this commented code is to  maybe piece together what parts of the code do.
 * I have a relatively loose grasp on how Fragments work. */

// start here
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // setting the default view pane

        // set the fragment container inside of activity_main
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) { // if there is no fcurrent fragment
            val fragment = CrimeFragment() // create a new CrimeFragment using the constructor
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment) // add the fragement to the fragment container
                .commit() // commit the "transaction" in .beginTransaction
        }
    }
}