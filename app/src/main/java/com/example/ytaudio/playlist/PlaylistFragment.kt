package com.example.ytaudio.playlist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.databinding.PlaylistFragmentBinding
import com.example.ytaudio.fragment.ActionModeFragment
import com.example.ytaudio.main.MainActivityViewModel
import javax.inject.Inject


class PlaylistFragment : ActionModeFragment() {

    @Inject
    lateinit var playlistViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    val playlistViewModel: PlaylistViewModel by viewModels {
        playlistViewModelFactory
    }

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        mainActivityViewModelFactory
    }

    private lateinit var binding: PlaylistFragmentBinding

    private val playlistAdapter = PlaylistAdapter(this) {
        mainActivityViewModel.audioItemClicked(it.id)
        findNavController().navigate(PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlaylistFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application

        playlistViewModel.networkFailure.observe(viewLifecycleOwner, Observer {
            binding.networkFailure.visibility = if (it) View.VISIBLE else View.GONE
        })

        binding.viewModel = this.playlistViewModel

        binding.apply {

            playlistView.adapter = playlistAdapter
            playlistView.layoutManager =
                LinearLayoutManager(application, RecyclerView.VERTICAL, false)
            playlistView.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )

            lifecycleOwner = this@PlaylistFragment
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_playlist_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                if (!this.playlistViewModel.playlistItems.value.isNullOrEmpty()) {
                    playlistAdapter.startActionMode()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}