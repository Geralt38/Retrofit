package course.labs.retrofitlab.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import course.labs.retrofitlab.R;
import course.labs.retrofitlab.adapter.MoviesAdapter;
import course.labs.retrofitlab.model.Detailed.MovieDetailed;
import course.labs.retrofitlab.model.Movie;
import course.labs.retrofitlab.model.MoviesResponse;
import course.labs.retrofitlab.rest.ApiClient;
import course.labs.retrofitlab.rest.ServiceGenerator;
import course.labs.retrofitlab.rest.TMDBInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static course.labs.retrofitlab.rest.ApiClient.BASE_URL;

public class DetailsActivity extends AppCompatActivity{

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private final static String API_KEY = "7e8f60e325cd06e164799af1e317d7a7";

    private TextView titleView;
    private TextView originalTitleView;
    private TextView subtitleView;
    private TextView taglineView;
    private TextView ratingView;
    private TextView productionCompsView;
    private TextView genresView;
    private TextView descriptionView;
    private ImageView posterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        String id = getIntent().getExtras().getString("id");

        titleView = (TextView) findViewById(R.id.title);
        originalTitleView = (TextView) findViewById(R.id.original_title);
        subtitleView = (TextView) findViewById(R.id.subtitle);
        taglineView = (TextView) findViewById(R.id.tagline);
        ratingView = (TextView) findViewById(R.id.rating);
        genresView = (TextView) findViewById(R.id.genres);
        productionCompsView = (TextView) findViewById(R.id.prod_comps);
        descriptionView = (TextView) findViewById(R.id.description);
        posterView = (ImageView) findViewById(R.id.poster);

        loadDetails(id);
    }

    private void loadDetails(String id) {

        TMDBInterface apiService = ServiceGenerator.createService(TMDBInterface.class, BASE_URL);
        ApiClient.getClient().create(TMDBInterface.class);


        Call<MovieDetailed> call = apiService.getMovieDetails(Integer.parseInt(id),API_KEY);

        call.enqueue(new Callback<MovieDetailed>() {
            @Override
            public void onResponse(Call<MovieDetailed> call, Response<MovieDetailed> response) {
                //код ответа сервера (200 - ОК), в данном случае далее не используется
                int statusCode = response.code();
                //получаем список фильмов, произведя парсинг JSON ответа с помощью библиотеки Retrofit
                MovieDetailed movie = response.body();

                setupViews(movie);
            }

            @Override
            public void onFailure(Call<MovieDetailed> call, Throwable t) {

                Log.e(TAG, t.toString());

            }
        });
    }

    private void setupViews(MovieDetailed movie) {
        titleView.setText(movie.getTitle());
        originalTitleView.setText(movie.getOriginalTitle());
        subtitleView.setText(movie.getReleaseDate());
        taglineView.setText(movie.getTagline());
        ratingView.setText(movie.getVoteAverage().toString());
        genresView.setText(movie.getGenresString());
        productionCompsView.setText(movie.getCompaniesString());
        descriptionView.setText(movie.getOverview());

        String IMAGE_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
        String moviePoster = movie.getPosterPath();
        String posterURL = IMAGE_URL_PREFIX+moviePoster;
        new DownloadImageTask(posterView).execute(posterURL);
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

