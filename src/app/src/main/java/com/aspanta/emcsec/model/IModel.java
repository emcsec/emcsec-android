package com.aspanta.emcsec.model;


import retrofit2.Response;
import rx.Observable;

public interface IModel {

    Observable<Response<Object>> sendGetBitcoinCourseRequest(String currency);

    Observable<Response<Object>> sendGetEmercoinCourseRequest(String currency);
}
