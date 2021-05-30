package com.jeje.combinestagram.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jeje.combinestagram.PhotoStore
import com.jeje.combinestagram.R
import com.jeje.combinestagram.model.Photo
import com.jeje.combinestagram.viewmodel.SharedViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.layout_photo_bottom_sheet.*


class PhotosBottomDialogFragment : BottomSheetDialogFragment(), PhotosAdapter.PhotoListener {

    private lateinit var viewModel: SharedViewModel

    private val selectedPhotosSubject = PublishSubject.create<Photo>()

    val selectedPhotos: Observable<Photo>
        get() = selectedPhotosSubject.hide()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_photo_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val ctx = activity
        ctx?.let {
            viewModel = ViewModelProviders.of(ctx).get(SharedViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosRecyclerView.layoutManager = GridLayoutManager(context, 3)
        photosRecyclerView.adapter = PhotosAdapter(PhotoStore.photos, this)
    }

    override fun photoClicked(photo: Photo) {
        selectedPhotosSubject.onNext(photo)
    }

    override fun onDestroyView() {
        selectedPhotosSubject.onComplete()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): PhotosBottomDialogFragment {
            return PhotosBottomDialogFragment()
        }
    }
}