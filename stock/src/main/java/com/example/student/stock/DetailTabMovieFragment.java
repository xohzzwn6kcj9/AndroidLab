package com.example.student.stock;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class DetailTabMovieFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
            return null;
        }
		LinearLayout linear=(LinearLayout)inflater.inflate(R.layout.detail_videoview, container, false);
		VideoView videoView = (VideoView) linear.findViewById(R.id.video);

		MediaController mc = new MediaController(getActivity());
		videoView.setMediaController(mc);
		videoView.setVideoURI(Uri.parse("http://70.12.116.130:8080/Gee.mp4"));
		videoView.requestFocus();
		videoView.start();
		return linear;
	}
}