package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment

class CrimeFragment : Fragment() {

    // Wiring up the interactive elements in the layout file fragment_crime.xml
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    /* Okay, so, we reload the saved instance state as usual and then
    we also start a new crime object.  This is here since each time we open this fragment in this
    case, we're going to be entering in a new crime
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    /* onCreateView()
    * So here we are defining the parameters of the Fragment.  In this program the fragment
    * is inflated to take up the whole screen. */
    override fun onCreateView(
        // define the data types in onCreateView
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Inflate the fragment within the container using the layout
        file fragment_crime.xml. */
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        // Initialize the lateinit variables above
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence,
                                           start: Int,
                                           before: Int,
                                           count: Int
            ) {
                // this space intentionally left blank
            }

            override fun onTextChanged(sequence: CharSequence,
                                       start: Int,
                                       before: Int,
                                       count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }
}