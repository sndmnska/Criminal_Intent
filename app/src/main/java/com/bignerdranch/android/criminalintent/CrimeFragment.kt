package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bignerdranch.android.criminalintent.R.*
import java.io.File
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    // Wiring up the interactive elements in the layout file fragment_crime.xml
    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    /* Okay, so, we reload the saved instance state as usual and then
    we also start a new crime object.  This is here since each time we open this fragment in this
    case, we're going to be entering in a new crime */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        Log.d(TAG, "args bundle crime Id: $crimeId")
        // Load crime from database
        crimeDetailViewModel.loadCrime(crimeId)
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
        val view = inflater.inflate(layout.fragment_crime, container, false)

        // Initialize the lateinit variables above
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView


        return view
    }

    // Observing changes to the fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    photoUri = FileProvider.getUriForFile(requireActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    photoFile)
                    updateUI()
                }
            })
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                // this space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence,
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

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT, getString(string.crime_report_subject)
                )
            }.also { intent ->
                val chooserIntent =
                    Intent.createChooser(intent, getString(string.send_report))
                startActivity(intent)
            }
        }

        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }

            // Dummy code to check that the filter is working
//            pickContactIntent.addCategory(Intent.CATEGORY_HOME)


            // guards against no contacts app when an Intent can't follow through.
            /*Seems to have issues with finding the contacts app which does exist.
            * Possible */
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            // the below block of code is not working.  Is it because resolveActivity is depreciated?
            /*if (resolvedActivity == null) {
                    isEnabled = false
                }*/
        } // something isn't working quite right here I think...
        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY)
            // the below block of code is not working.  Is it because resolveActivity is depreciated?
/*            if (resolvedActivity == null) {
                isEnabled = true
            }*/

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

                startActivityForResult(captureImage, REQUEST_PHOTO)
            }

        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,
        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() // skips the checkbox animation when updating the UI.
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
        updatePhotoView()
    }


    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {

            /*The below code pulls out the contact name for use in our program. p.305*/
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri =  data.data!!   // Data is not null from null check above.
                // specify which fields you want your query to return values for
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                // Perform your query - the contractUri is like a "where" clause here
                val cursor = requireActivity().contentResolver

                    .query(contactUri, queryFields, null, null, null)
                cursor?.use {
                    // verify cursor contains at least one result
                    if (it.count == 0) { return }
                    // pull out the first column of the first row of data
                    // that's your suspect's name
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text= suspect
                }
            }

            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView()
            }

        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(string.crime_report_solved)
        } else {
            getString(string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(string.crime_report_no_suspect)
        } else {
            getString(string.crime_report_suspect, crime.suspect)
        }
        // template string
        return getString(string.crime_report, crime.title, dateString, solvedString, suspect)
    }
    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }
}
