package com.cybershark.mediahub.ui.movies.views.watching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.mediahub.R
import com.cybershark.mediahub.ui.movies.adapters.MoviesWatchingAdapter
import com.cybershark.mediahub.ui.movies.viewmodel.MoviesViewModel
import kotlinx.android.synthetic.main.fragment_movies_watching.*


class MoviesWatchingFragment : Fragment() {

    private val moviesViewModel by lazy { ViewModelProvider(this).get(MoviesViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies_watching, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMoviesWatching.layoutManager = LinearLayoutManager(context)

        val adapter = MoviesWatchingAdapter()
        rvMoviesWatching.adapter = adapter

        moviesViewModel.dummyMoviesData.observe(viewLifecycleOwner, Observer {
            adapter.setItemsList(it)
        })
    }
}
