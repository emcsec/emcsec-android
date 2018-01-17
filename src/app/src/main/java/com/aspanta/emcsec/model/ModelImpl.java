package com.aspanta.emcsec.model;

import com.aspanta.emcsec.model.apiRetrofit.ApiModule;

import retrofit2.Response;
import rx.Observable;

import static com.aspanta.emcsec.tools.Config.BASE_URL_COIN_MARKET;


public class ModelImpl implements IModel {

    public final String TAG = getClass().getName();

    @Override
    public Observable<Response<Object>> sendGetBitcoinCourseRequest(String currency) {
        return ApiModule.getApiInterface(BASE_URL_COIN_MARKET).sendGetBitcoinCourseRequest(currency);
    }

    @Override
    public Observable<Response<Object>> sendGetEmercoinCourseRequest(String currency) {
        return ApiModule.getApiInterface(BASE_URL_COIN_MARKET).sendGetEmercoinCourseRequest(currency);
    }
}
