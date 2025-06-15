package com.example.dawaidata.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.dawaidata.R

class HomeFragment : Fragment() {

    private lateinit var videoView: VideoView
    private val handler = Handler(Looper.getMainLooper())
    private val videoTimeoutMillis = 30_000L // 30 seconds timeout
    private var timeoutRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        videoView = view.findViewById(R.id.videoView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playLocalVideo()
    }

    private fun playLocalVideo() {
        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.pandavideo}")
        videoView.setVideoURI(videoUri)

        // Timeout in case video never starts
        timeoutRunnable = Runnable {
            if (!videoView.isPlaying) {
                Toast.makeText(requireContext(), "Video failed to start (timeout)", Toast.LENGTH_SHORT).show()
                videoView.stopPlayback()
            }
        }
        handler.postDelayed(timeoutRunnable!!, videoTimeoutMillis)

        videoView.setOnPreparedListener {
            handler.removeCallbacks(timeoutRunnable!!)
            videoView.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        videoView.stopPlayback()
    }
}
