package com.jeje.combinestagram.view

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jeje.combinestagram.R
import com.jeje.combinestagram.model.Photo
import com.jeje.combinestagram.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = getString(R.string.collage)

        viewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        addButton.setOnClickListener {
            actionAdd()
        }

        clearButton.setOnClickListener {
            actionClear()
        }

        saveButton.setOnClickListener {
            actionSave()
        }

        viewModel.getSelectedPhotos().observe(this, Observer { photos ->
            photos?.let { photos ->
                if (photos.isNotEmpty()) {
                    val bitmaps = photos.map { BitmapFactory.decodeResource(resources, it.drawable) }
                    val newBitmap = combineImages(bitmaps)
                    collageImage.setImageDrawable(BitmapDrawable(resources, newBitmap))
                } else {
                    collageImage.setImageResource(android.R.color.transparent)
                }
            }
            updateUi(photos)
        })
    }

    private fun updateUi(photos: List<Photo>) {
        saveButton.isEnabled = photos.isNotEmpty() && (photos.size % 2 == 0)
        clearButton.isEnabled = photos.isNotEmpty()
        addButton.isEnabled = photos.size < 6
        title = if (photos.isNotEmpty()) resources.getQuantityString(R.plurals.photos_format, photos.size, photos.size) else getString(R.string.collage)
    }

    private fun actionAdd() {
        val addPhotosBottomDialogFragment = PhotosBottomDialogFragment.newInstance()
        addPhotosBottomDialogFragment.show(supportFragmentManager, "PhotosBottomDialogFragment")
        viewModel.subscribeSelectedPhotos(addPhotosBottomDialogFragment.selectedPhotos)
    }

    private fun actionClear() {
        viewModel.clearPhotos()
    }

    private fun actionSave() {
        println("actionSave")
    }
}