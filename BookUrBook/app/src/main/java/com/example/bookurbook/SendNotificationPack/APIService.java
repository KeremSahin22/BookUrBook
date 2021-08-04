package com.example.bookurbook.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAArUzwtUw:APA91bEd5ifHEwjBTqb_uS75QXNm0-WgjYIfvm3X22BHSKedOkNE31bSvdelAJ9pRMDiS4A7zFLq7GQceYcFvJnFz6oNafuqmKZQWI2Z7iNL9zrWzY3VLkc47QNU-NOJiLetIPjkrPAp"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

