package inorg.gsdl.plndpt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String Base_URL = "http://gsdl.org.in/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(Base_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

        }
        return retrofit;
    }

}
