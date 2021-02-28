package com.example.a3rdhand.FCM;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TokenAPIClient {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAALNRxdp4:APA91bFuADHP9HsCUHabY3T4Bi5T9j3k3AgpVE-MEFg1lpdQTJJiKeU_55zRa-H2TqN6roythzKstAyIJ65bdrhM7H-jNqHINx7IJvccF5dH3pXunrQasU_pVreMIKC8JtKRyeUQqEV9"
        }
    )

    @POST("fcm/send")
    Call<TokenResponse> sendNotification(@Body NotificationSender body);
}
