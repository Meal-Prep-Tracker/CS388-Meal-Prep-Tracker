package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


private const val TAG = "AddMealFragment"
private const val CAMERA_RESULT_CODE = 123

@SuppressLint("SimpleDateFormat")
val dateFormat = SimpleDateFormat("MM/dd/yyyy")

class AddMealFragment(val listener: SetOnAddMealListener) : Fragment() {
    private lateinit var mealNameEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var addMealBtn: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: StorageReference
    private lateinit var imageButton: ImageButton
    private lateinit var auth: FirebaseAuth
    private var imageTaken = false

    private lateinit var mealDate: TextView
    interface SetOnAddMealListener {
        fun onAddMealClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth.currentUser ?: run {
            val intent = Intent(activity, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, "AddMealFragment")
            startActivity(intent)
            activity?.finish()
        }
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_meal, container, false)
        return view
    }

    private fun addNewMeal(key: String, meal: Meal) {
        // Add to the meals collection and route to EditIngredient
        val mealCollection = database.getReference(MEALS_COLLECTION)
        mealCollection.child(key).setValue(meal)
            .addOnSuccessListener {
//                Log.i("firebase", "Got value ${it.value}")
                val intent = Intent(activity, EditIngredientActivity::class.java)
                intent.putExtra(MEAL_EXTRA, meal)
                startActivity(intent)
                // replace AddMeal with MealList on stack so user can be routed back to it once hitting done
                listener.onAddMealClick()
            }
            .addOnFailureListener {
                Log.e("firebase", "Error getting last inserted Meal", it)
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // [START initialize_database_ref]
        database = Firebase.database
        // Call new methods within onViewCreated
        mealNameEditText = view.findViewById(R.id.mealName)
        servingsEditText = view.findViewById(R.id.mealServings)
        addMealBtn = view.findViewById(R.id.addMealBtn)
        mealDate = view.findViewById(R.id.mealDate)
        imageButton = view.findViewById(R.id.food_image)
//        imageButton.background = null
        // Set event handlers
        addMealBtn.setOnClickListener {
            val key = database.getReference(MEALS_COLLECTION).push().key
            // error log
            if (key == null) {
                Log.w(TAG, "Couldn't get push key for meals")
            }
            else {
                // Make a new meal
                val meal = Meal(
                    user_id = auth.currentUser!!.uid,
                    id = key,
                    name = mealNameEditText.text.toString(),
                    servings = servingsEditText.text.toString().toInt(),
                    date = dateFormat.parse(mealDate.text.toString())?.time
                )

                // if the image is taken, save it to Firebase storage
                if(imageTaken) {
                    val bitmapToSave: Bitmap = getImageBitmap()
                    storage = com.google.firebase.ktx.Firebase.storage.reference
                    val objLocation = "${IMAGES_COLLECTION}/${UUID.randomUUID()}.jpeg"
                    val storedImage = storage.child(objLocation)
                    val bytes = ByteArrayOutputStream()
                    bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
                    val path = MediaStore.Images.Media.insertImage(
                        requireContext().contentResolver,
                        bitmapToSave,
                        objLocation,
                        ""
                    )
                    // set the image_id of the meal on success
                    storedImage.putFile(Uri.parse(path))
                        .addOnSuccessListener { task ->
                            task.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                                val downloadURL = downloadUri.toString()
                                Log.v(
                                    TAG,
                                    "Successfully saved image to Firebase Storage: ${downloadURL}"
                                )
                                meal.image_id = objLocation
                                // Add new meal
                                addNewMeal(key, meal)
                                // Get the last inserted Meal
                                /*database.child(MEALS_COLLECTION).child(key).get()
                                    .addOnSuccessListener {
                                        Log.i("firebase", "Got value ${it.value}")
                                        val meal: Meal? = it.getValue(Meal::class.java)

                                        // Route to EditIngredientActivity
                                        val intent = Intent(activity, EditIngredientActivity::class.java)
                                        intent.putExtra(MEAL_EXTRA, meal)
                                        startActivity(intent)
                                        // Put MealListFragment on stack so user can be routed back to it onc hitting done
                                        listener.onAddMealClick()


                                    }
                                    .addOnFailureListener {
                                        Log.e("firebase", "Error getting last inserted Meal", it)
                                    }*/
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to save image to Firebase Storage: ${e}")
                            // Add new meal
                            addNewMeal(key, meal)
                            // Get the last inserted Meal
                            /*database.child(MEALS_COLLECTION).child(key).get()
                                .addOnSuccessListener {
                                    Log.i("firebase", "Got value ${it.value}")
                                    val meal: Meal? = it.getValue(Meal::class.java)

                                    // Route to EditIngredientActivity
                                    val intent = Intent(activity, EditIngredientActivity::class.java)
                                    intent.putExtra(MEAL_EXTRA, meal)
                                    startActivity(intent)
                                    // Put MealListFragment on stack so user can be routed back to it onc hitting done
                                    listener.onAddMealClick()


                                }
                                .addOnFailureListener {
                                    Log.e("firebase", "Error getting last inserted Meal", it)
                                }

                             */
                        }

                }
                else {
                    // Add new meal
                    addNewMeal(key, meal)
                    // Get the last inserted Meal
                    /* database.child(MEALS_COLLECTION).child(key).get()
                        .addOnSuccessListener {
                            Log.i("firebase", "Got value ${it.value}")
                            val meal: Meal? = it.getValue(Meal::class.java)

                            // Route to EditIngredientActivity
                            val intent = Intent(activity, EditIngredientActivity::class.java)
                            intent.putExtra(MEAL_EXTRA, meal)
                            startActivity(intent)
                            // Put MealListFragment on stack so user can be routed back to it onc hitting done
                            listener.onAddMealClick()
                        }
                        .addOnFailureListener {
                            Log.e("firebase", "Error getting last inserted Meal", it)
                        }
                     */
                }

            }
        }
        imageButton.setOnClickListener {
            val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camIntent, CAMERA_RESULT_CODE)
        }
        view.findViewById<Button>(R.id.pickDate).setOnClickListener {
            val cc = object : DatePickerFragment.OnDateSelectListener {
                @SuppressLint("SetTextI18n")
                override fun onDateSelect(c: Calendar) {
                    mealDate.text = "${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.DAY_OF_MONTH)}/${
                        c.get(Calendar.YEAR)
                    }"
                }
            }
            val newFragment = DatePickerFragment(cc)
            newFragment.show(parentFragmentManager, "datePicker")
        }

        // Set the data to current date
        // Get the current date
        val currentDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())

        // Set the current date to the TextView
        mealDate.text = currentDate
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_RESULT_CODE) {
            // A food image was taken so we can assume a bitmap for it exists
            val takenPic: Bitmap? = data!!.extras!!["data"] as Bitmap?
            Glide
                .with(this)
                .asBitmap()
                .load(takenPic)
                .into(imageButton)
            // Image is taken
            imageTaken = true
        }
    }

    private fun getImageBitmap(): Bitmap {
        return imageButton.drawable.toBitmap()
    }
}