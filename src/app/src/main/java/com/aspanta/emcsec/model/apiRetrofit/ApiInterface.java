package com.aspanta.emcsec.model.apiRetrofit;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

import static com.aspanta.emcsec.tools.Config.BTN_COURSE_PATH;
import static com.aspanta.emcsec.tools.Config.EMC_COURSE_PATH;


public interface ApiInterface {

    @GET(BTN_COURSE_PATH)
    Observable<Response<Object>> sendGetBitcoinCourseRequest(@Query("convert") String currency);

    @GET(EMC_COURSE_PATH)
    Observable<Response<Object>> sendGetEmercoinCourseRequest(@Query("convert") String currency);
}
