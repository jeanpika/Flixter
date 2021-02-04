package com.example.flixter.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.DetailActivity;
import com.example.flixter.MoviePlay;
import com.example.flixter.R;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // usually involves inflating alayout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // involves populating the data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder" + position);
        //get the movie at the passed in position
        Movie movie = movies.get(position);
        // Bind the movie data into the viewholder
        holder.bind(movie);
    }

    // return the total count of item in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        Movie movie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(Movie movie) {
            this.movie = movie;
            getVideoId(this.movie);
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageUrl;

            //if phone is in landscape mode
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //then imageUrl = back drop image
                imageUrl = movie.getBackdropPath();
            } else {
                //else imageUrl = poster image
                imageUrl = movie.getPosterPath();
            }
            Glide.with(context).load(imageUrl).into(ivPoster);

            //Register the Clicklistener on the whole thing
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                //Navigate to a different activity on tap
                public void onClick(View v) {
                    Intent i;
                    if (movie.getRating() > 5.0) {
                        i = new Intent(context, MoviePlay.class);
                        i.putExtra("videoId", movie.getVideoId());
                    } else {
                        i = new Intent(context, DetailActivity.class);
                        i.putExtra("movie", Parcels.wrap(movie));
                    }
                    context.startActivity(i);
                }
            });
        }

        public void getVideoId(final Movie movie){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Headers headers, JSON json) {
                    try {
                        JSONArray results = json.jsonObject.getJSONArray("results");
                        if(results.length() == 0){
                            return;
                        }
                        String youtubeKey = results.getJSONObject(0).getString("key");
                        Log.d("DetailActivity", youtubeKey);
                        movie.setVideoId(youtubeKey);

                    } catch (JSONException e) {
                        Log.e("DetailActivity", "Failed to parse JSON", e);
                    }
                }

                @Override
                public void onFailure(int i, Headers headers, String s, Throwable throwable) {

                }
            });

        }
    }
}
