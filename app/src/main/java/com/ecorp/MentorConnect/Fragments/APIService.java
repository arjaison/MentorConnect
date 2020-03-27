package com.ecorp.MentorConnect.Fragments;

import com.ecorp.MentorConnect.Notifications.MyResponse;
import com.ecorp.MentorConnect.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAABM3AP3Q:APA91bG5W5qSKSYJyK7bAn7RhzTk5rGPVwwn_nHo8mG49A9QQapXlQvwP5M7k0LORf18gSoBTBx8AcSmzRnhHmkkKgg6OONlcv4-CrE4QY7oWMt4bamizJep7KTlgA4dGIHgCc0f0a_Z"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
