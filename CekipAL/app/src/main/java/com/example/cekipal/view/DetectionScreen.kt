package com.example.cekipal.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.cekipal.bLL.Detection_Async
import com.example.cekipal.databinding.ActivityDetectionScreenBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.WanderingCubes

class DetectionScreen : AppCompatActivity() {
    private lateinit var binding : ActivityDetectionScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
        initializeEvents()
    }

    private fun initializeViews(){
        binding = ActivityDetectionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.takenPhoto.setImageBitmap(bitmap)
        binding.tvPredictResult.visibility = View.GONE
        binding.tvResultTitle.visibility = View.GONE

        val progressBar = binding.progressBar as ProgressBar
        val doubleBounce: Sprite = WanderingCubes()
        progressBar.indeterminateDrawable = doubleBounce

        Detection_Async(this, this::resultFunc).execute()
        if(musicExist!!) startService(serviceIntent) else stopService(serviceIntent)

    }

    private fun initializeEvents(){
        binding.closeBtn.setOnClickListener {
            if(clickSoundExist!!){
                mediaSoundPlayer.start()
            }
            setResult(RESULT_OK,intent)
            finish()
        }
    }

    private fun resultFunc (predict : String?)
    {
        if(predict != null){
            binding.progressBar.visibility = View.GONE
            binding.tvResultTitle.visibility = View.VISIBLE
            binding.tvPredictResult.visibility = View.VISIBLE
            binding.tvPredictResult.text = predict
        }
    }
}