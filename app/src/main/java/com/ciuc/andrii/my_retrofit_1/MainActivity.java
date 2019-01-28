package com.ciuc.andrii.my_retrofit_1;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ciuc.andrii.my_retrofit_1.adapters.RandomUserAdapter;
import com.ciuc.andrii.my_retrofit_1.pojo.MyRandomUser;
import com.ciuc.andrii.my_retrofit_1.interfaces.RandomUsersApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    RandomUserAdapter mAdapter = new RandomUserAdapter();

    Retrofit retrofit;

    Gson gson = new GsonBuilder().create();


    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    OkHttpClient okHttpClient;

    ProgressDialog pd ;

    double USERSNUMBER = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(httpLoggingInterceptor)
                .build();


        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Завантаження");


        createRetrofit();
       /* Observable<MyRandomUser> randomUsersCall = randomUsersApi.getRandomUsers("10.0");


        randomUsersCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MyRandomUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("test2","subscribe");
                    }

                    @Override
                    public void onNext(MyRandomUser myRandomUser) {
                        Log.i("test2",myRandomUser.getResults().toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test2","error" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i("test2","complete");
                    }
                }) ;*/




        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Toast.makeText(MainActivity.this, mAdapter.resultList.get(position).usersInformation(), Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void createRetrofit(){
        pd.show();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();



        RandomUsersApi randomUsersApi = retrofit.create(RandomUsersApi.class);


        Call<MyRandomUser> randomUsersCall = randomUsersApi.getRandomUsers(USERSNUMBER);

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
                /*Toast.makeText(MainActivity.this,"Помилка з'єднання" + t.getMessage(),Toast.LENGTH_LONG).show();*/

                createRetrofit();
            }
        });
    }

}
