package com.example.emrental.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAQH4SpVk:APA91bGT3yvx1Hd4R5Vbf0hHdTM-6jBGFvoHS9l1Insf2AJ8crKEoCS2Clyzg0ZBRZRZKwpoyYIXGKMrytmB5Ap1CFiYzdpOEAl3hRe50JjSv0PiNfh_14YIfrlG3_pb8j3AN4H2ggFw" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

