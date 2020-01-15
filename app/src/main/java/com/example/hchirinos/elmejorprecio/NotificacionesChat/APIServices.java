package com.example.hchirinos.elmejorprecio.NotificacionesChat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {
    @Headers(
            {
                    "Content-Type:aplication/json",
                    "Authorization:key=AAAAv9vEEls:APA91bHAlV866u-gC7RsudHR-h_cTUDkENQPB_YOASWv6hmz1xPAcNb_ZlEGCytdK0hdIUtgeUzxini40ZpDMX1DAC-x4RW5SFJyzXVYiR-RE71PGOd8B-Hb4lZS2p2iQCzBPyuKKipk"
            }
    )

    @POST ("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
