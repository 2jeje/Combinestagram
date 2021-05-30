package com.jeje.combinestagram.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeje.combinestagram.model.Photo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class SharedViewModel : ViewModel() {

    private val subscriptions = CompositeDisposable()
    private val imageSubject: BehaviorSubject<MutableList<Photo>> = BehaviorSubject.createDefault(mutableListOf())
    private val selectedPhotos = MutableLiveData<List<Photo>>()


    init {
        imageSubject.subscribe { photos -> selectedPhotos.value = photos }.addTo(subscriptions)
    }

    fun getSelectedPhotos(): LiveData<List<Photo>> = selectedPhotos

    fun clearPhotos() {
        imageSubject.value?.clear()
        imageSubject.onNext(imageSubject.value!!)
    }

    fun subscribeSelectedPhotos(selectedPhotos: Observable<Photo>) {
        selectedPhotos
            .doOnComplete { Log.v("SharedViewModel", "Completed selecting phtos") }
            .subscribe { photo ->
                imageSubject.value?.add(photo)
                imageSubject.onNext(imageSubject.value!!)
            }
            .addTo(subscriptions)

    }

    override fun onCleared() {
        subscriptions.dispose()
        super.onCleared()
    }

    fun saveBitmapFromImageView(imageView: ImageView, context: Context) {
        val tmpImg = "${System.currentTimeMillis()}.png"

        val os: OutputStream?

        val collagesDirectory = File(context.getExternalFilesDir(null), "collages")
        if (!collagesDirectory.exists()) {
            collagesDirectory.mkdirs()
        }

        val file = File(collagesDirectory, tmpImg)

        try {
            os = FileOutputStream(file)
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        } catch(e: IOException) {
            Log.e("MainActivity", "Problem saving collage", e)
        }
    }
}