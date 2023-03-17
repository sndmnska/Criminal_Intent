package com.bignerdranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/* This is for the Crime object, of what a new Crime would be defined as per object */

@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var title:String = "",
                 var date: Date = Date(),
                 var isSolved: Boolean = false)
