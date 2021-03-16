package inorg.gsdl.plndpt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient2 {

    public static final String Base_URL2 = "http://map.gsdl.org.in:8080/planningdpt/";
    // public static final String Base_URL2 ="http://10.0.2.2:8080/";

    private static Retrofit retrofit2 = null;

    public static Retrofit getClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();

        if (retrofit2 == null) {
            retrofit2 = new Retrofit.Builder().baseUrl(Base_URL2)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

        }
        return retrofit2;
    }

}
