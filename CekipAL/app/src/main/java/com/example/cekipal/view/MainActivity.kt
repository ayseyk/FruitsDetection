package com.example.cekipal.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.cekipal.bLL.MyService
import com.example.cekipal.R
import com.example.cekipal.databinding.ActivityMainBinding
import java.io.File

lateinit var bitmap : Bitmap
lateinit var mediaSoundPlayer : MediaPlayer
var clickSoundExist : Boolean? = null
var musicExist : Boolean? = null
lateinit var serviceIntent : Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var currentPath: String? = null
    private lateinit var photoUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
        initializeVisibility()
        initializeEvents()
    }

    private fun initializeViews(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detectBtn.visibility = View.GONE
        binding.photo.visibility = View.GONE

        val fruitArray = resources.getStringArray(R.array.fruits_info)
        val randomIndex = (fruitArray.indices).shuffled().random()
        binding.tvFruitInfo.text = fruitArray[randomIndex]

        clickSoundExist = readSettings("sound") == true
        musicExist = readSettings("music") == true
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun initializeEvents(){
        serviceIntent = Intent(this, MyService ::class.java)
        mediaSoundPlayer = MediaPlayer.create(this,R.raw.sound)

        if(musicExist!!) startService(serviceIntent) else stopService(serviceIntent)

        binding.cameraBtn.setOnClickListener {
            if(clickSoundExist!!){
                mediaSoundPlayer.start()
            }

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                cameraFun()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),2)
            }
        }

        binding.galleryBtn.setOnClickListener {
            if(clickSoundExist!!){
                mediaSoundPlayer.start()
            }

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED)
            {
                galleryFun()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }
        }

        binding.detectBtn.setOnClickListener {
            if(clickSoundExist!!){
                mediaSoundPlayer.start()
            }

            val intent = Intent(this, DetectionScreen::class.java)
            detectScreenRL.launch(intent)
        }

        binding.settingsImageButton.setOnClickListener{
            if(clickSoundExist!!){
                mediaSoundPlayer.start()
            }

            val popup = LayoutInflater.from(this).inflate(R.layout.settings_popup, null)
            val popWarning = AlertDialog.Builder(this)
            popWarning.setView(popup)

            val swMusic = popup.findViewById<Switch>(R.id.swMusic)
            val swSound = popup.findViewById<Switch>(R.id.swSound)

            swMusic.isChecked = readSettings("music") == true
            swSound.isChecked = readSettings("sound") == true

            popWarning.show()

            swMusic.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    musicExist = true
                    writeSettings("music",musicExist!!)
                    startService(serviceIntent)
                }
                else{
                    musicExist = false
                    writeSettings("music",musicExist!!)
                    stopService(serviceIntent)
                }
            }
            swSound.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    clickSoundExist = true
                    writeSettings("sound",clickSoundExist!!)
                }
                else{
                    clickSoundExist = false
                    writeSettings("sound",clickSoundExist!!)
                }
            }
        }
    }

    private fun writeSettings(key: String, value: Boolean ){
        val pref = getSharedPreferences(packageName, MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(key,value)
        editor.commit()
    }
    private fun readSettings(key: String):Boolean{
        val pref = getSharedPreferences(packageName, MODE_PRIVATE)
        return pref.getBoolean(key,true)
    }


    private var detectScreenRL = registerForActivityResult(ActivityResultContracts
    .StartActivityForResult()){
        if(it.resultCode == RESULT_OK || it.resultCode == RESULT_CANCELED){ //close
            initializeVisibility()
        }
    }

    private fun initializeVisibilityWithPhoto(){
        binding.photo.visibility = View.VISIBLE
        binding.detectBtn.visibility = View.VISIBLE

        binding.photoAnim.visibility = View.GONE
        binding.tvFruitInfo.visibility = View.GONE
        binding.camGalLinearLayout.visibility = View.GONE
        binding.settingsImageButton.visibility = View.GONE
    }

    private fun initializeVisibility(){
        binding.photoAnim.visibility = View.VISIBLE
        binding.camGalLinearLayout.visibility = View.VISIBLE
        binding.tvFruitInfo.visibility = View.VISIBLE

        binding.photo.visibility = View.GONE
        binding.detectBtn.visibility = View.GONE
        binding.settingsImageButton.visibility = View.VISIBLE

    }

    override fun onBackPressed() {
        if(binding.photoAnim.visibility == View.VISIBLE){
            super.onBackPressed()
            stopService(serviceIntent)
        }
        initializeVisibility()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        stopService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        if(musicExist!!) startService(serviceIntent) else stopService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (gr in grantResults)
        {
            if (gr != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, getString(R.string.toast_permission_message), Toast.LENGTH_LONG).show()
                return
            }
        }
        if(requestCode == 1) galleryFun() else cameraFun()
    }

    private fun cameraFun()
    {
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            createImageFile()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent,0)
        }
    }

    private fun galleryFun()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,2)
    }

    private fun createImageFile()
    {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("photo", ".jpg" ,storageDir)
        currentPath = imageFile.absolutePath
        photoUri = FileProvider.getUriForFile(this, packageName, imageFile)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == 0)
        {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = 1
            bmOptions.inPurgeable = true
            bitmap = BitmapFactory.decodeFile(currentPath, bmOptions)
            bitmap = rotateBitmap()!!
            initializeVisibilityWithPhoto()
            binding.photo.setImageBitmap(bitmap)
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == 2)
        {
            photoUri = data?.data!!
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,photoUri)
            initializeVisibilityWithPhoto()
            binding.photo.setImageBitmap(bitmap)
        }
    }

    private fun rotateBitmap() : Bitmap?{
        val orientation: Int = ExifInterface(currentPath!!).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
        return rotatedBitmap
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

}

