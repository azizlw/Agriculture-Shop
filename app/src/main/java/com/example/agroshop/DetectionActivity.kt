package com.example.agroshop

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import java.io.IOException

class DetectionActivity : AppCompatActivity() {
    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap

    private val mCameraRequestCode = 0
    private val mGalleryRequestCode = 2

    private val mInputSize = 224
    private  var result1: String? = null;
    private val mModelPath = "plant_disease_model.tflite"
    private val mLabelPath = "plant_labels.txt"
    private val mSamplePath = "soybean.JPG"
    private lateinit var mPhotoImageView: ImageView
    private lateinit var mResultTextView: TextView
    private lateinit var mCameraButton: Button
    private lateinit var mGalleryButton: Button
    private lateinit var mDetectButton: Button
    private lateinit var back: Button
    private lateinit var recommed: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar!!.hide()
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_detection)
        mClassifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)
        mPhotoImageView = findViewById(R.id.mPhotoImageView)
        mCameraButton = findViewById(R.id.mCameraButton)
        mDetectButton = findViewById(R.id.mDetectButton)
        mGalleryButton = findViewById(R.id.mGalleryButton)
        mResultTextView = findViewById(R.id.mResultTextView);
        back = findViewById(R.id.back)
        recommed = findViewById(R.id.recommed);
        resources.assets.open(mSamplePath).use {
            mBitmap = BitmapFactory.decodeStream(it)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mInputSize, mInputSize, true)
            mPhotoImageView.setImageBitmap(mBitmap)
        }
        mCameraButton.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(callCameraIntent, mCameraRequestCode)
        }
       back.setOnClickListener {
           intent = Intent(this, HomeActivity::class.java)
           startActivity(intent);
        }
        recommed.setOnClickListener {
            intent = Intent(this,categoryproduct::class.java)
           // println(result1)
            if(result1 != null && !result1!!.trim().isEmpty()){
                val toast = Toast.makeText(applicationContext, result1, Toast.LENGTH_LONG)
                toast.show()
                println(result1)
                val s= "healthy"
                if(result1!!.contains(s,ignoreCase = true)){
                    val toast = Toast.makeText(applicationContext, "Your plant is healthy, No need to any product", Toast.LENGTH_LONG)
                    toast.show()
                }else {
                    intent.putExtra("disease", result1);
                    startActivity(intent);
                }
            }else{
                val toast = Toast.makeText(applicationContext, "Your Plant is Healthy,No need to recommend Product", Toast.LENGTH_LONG)
                toast.show()
            }
        }


        mGalleryButton.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_PICK)
            callGalleryIntent.type = "image/*"
            startActivityForResult(callGalleryIntent, mGalleryRequestCode)
        }
        mDetectButton.setOnClickListener {
            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
            result1 = (results?.title).toString()
            mResultTextView.text= results?.title+"\n Confidence:"+results?.confidence

        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == mCameraRequestCode){
            //Considérons le cas de la caméra annulée
            if(resultCode == Activity.RESULT_OK && data != null) {
                mBitmap = data.extras!!.get("data") as Bitmap
                mBitmap = scaleImage(mBitmap)
                val toast = Toast.makeText(this, ("Image crop to: w= ${mBitmap.width} h= ${mBitmap.height}"), Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 20)
                toast.show()
                mPhotoImageView.setImageBitmap(mBitmap)
                mResultTextView.text= "Your photo image set now."
            } else {
                Toast.makeText(this, "Camera cancel..", Toast.LENGTH_LONG).show()
            }
        } else if(requestCode == mGalleryRequestCode) {
            if (data != null) {
                val uri = data.data

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                println("Success!!!")
                mBitmap = scaleImage(mBitmap)
                mPhotoImageView.setImageBitmap(mBitmap)

            }
        } else {
            Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_LONG).show()

        }
    }


    fun scaleImage(bitmap: Bitmap?): Bitmap {
        val orignalWidth = bitmap!!.width
        val originalHeight = bitmap.height
        val scaleWidth = mInputSize.toFloat() / orignalWidth
        val scaleHeight = mInputSize.toFloat() / originalHeight
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, orignalWidth, originalHeight, matrix, true)
    }
 fun OnBackPressedCallback(getActivity: HomeActivity){
     println("done")
 }
}