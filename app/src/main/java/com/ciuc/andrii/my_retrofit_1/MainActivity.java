package com.ciuc.andrii.my_retrofit_1;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ciuc.andrii.my_retrofit_1.adapters.RandomUserAdapter;
import com.ciuc.andrii.my_retrofit_1.pojo.MyRandomUser;
import com.ciuc.andrii.my_retrofit_1.interfaces.RandomUsersApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class MainActivity extends AppCompatActivity {

    private String BASE_URL = "https://randomuser.me/";
    private double USERSNUMBER = 20;

    private RecyclerView recyclerView;
    private Switch aSwitch ;
    private TextView textView;
    private ProgressDialog pd ;

    private RandomUserAdapter mAdapter;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private OkHttpClient okHttpClient;
    private Gson gson;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        aSwitch = findViewById(R.id.my_switch);
        textView = findViewById(R.id.text_state);

        mAdapter = new RandomUserAdapter();

        gson = new GsonBuilder().create();

        httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(httpLoggingInterceptor)
                .build();


        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Завантаження");


        callWithRetrofit();


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textView.setText("Java RX");
                    callWithRX();
                }else {
                    textView.setText("Retrofit");
                    callWithRetrofit();
                }

            }
        });



        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Toast.makeText(MainActivity.this, mAdapter.resultList.get(position).usersInformation(), Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void callWithRetrofit(){
        pd.show();

        Call<MyRandomUser> randomUsersCall = getRandomUsersApi(getRetrofit(BASE_URL, okHttpClient, gson)).getRandomUsers(USERSNUMBER);

        randomUsersCall.enqueue(new Callback<MyRandomUser>() {
            @Override
            public void onResponse(Call<MyRandomUser> call, @NonNull Response<MyRandomUser> response) {
                if(response.isSuccessful()) {
                    mAdapter.setItems(response.body().getResults());
                    recyclerView.setAdapter(mAdapter);

                    pd.cancel();
                }
            }

            @Override
            public void onFailure(Call<MyRandomUser> call, Throwable t) {
                callWithRetrofit();
            }
        });
    }
    

    private void callWithRX(){
        pd.show();

        Observable<MyRandomUser> randomUsersCall = getRandomUsersApi(getRetrofit(BASE_URL, okHttpClient, gson)).getRandomUsersRX(USERSNUMBER);

        randomUsersCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MyRandomUser>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(MyRandomUser myRandomUser) {

                        mAdapter.setItems(myRandomUser.getResults());
                        recyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callWithRX();
                    }

                    @Override
                    public void onComplete() {
                        pd.cancel();
                    }
                }) ;
    }


    private Retrofit getRetrofit(String baseUrl, OkHttpClient client, Gson mGson){
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .client(client)
                .build();
    }

    private RandomUsersApi getRandomUsersApi(Retrofit retrofit){
        return retrofit.create(RandomUsersApi.class);
    }


}
