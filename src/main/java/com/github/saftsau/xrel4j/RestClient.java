/*
 * Copyright 2019 saftsau
 *
 * This file is part of xREL4J.
 *
 * xREL4J is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * xREL4J is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with xREL4J. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.github.saftsau.xrel4j;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

public class RestClient {
    
    private static final class InstanceHolder {
        static final RestClient INSTANCE = create();
    }
    
    public static final String BASE_XREL_URL = "https://api.xrel.to/v2/";
    
    private final OkHttpClient httpClient;
    private final ResponseInterceptor responseInterceptor;
    private final Retrofit retrofit;
    private final XrelService xrelService;
    
    public static RestClient getInstance() {
        return InstanceHolder.INSTANCE;
    }
    
    public static OkHttpClient.Builder createHttpClientBuilder() {
        return new OkHttpClient.Builder().addInterceptor(new ResponseInterceptor());
    }
    
    public static RestClient create() {
        return create(null);
    }
    
    public static RestClient create(Consumer<OkHttpClient.Builder> httpClientBuilderConsumer) {
        final OkHttpClient.Builder httpClientBuilder = createHttpClientBuilder();
        final ResponseInterceptor responseInterceptor = new ResponseInterceptor();
        httpClientBuilder.addInterceptor(responseInterceptor);
        if (httpClientBuilderConsumer != null) {
            httpClientBuilderConsumer.accept(httpClientBuilder);
        }
        return new RestClient(httpClientBuilder.build(), responseInterceptor);
    }
    
    private RestClient(OkHttpClient httpClient, ResponseInterceptor responseInterceptor) {
        this(httpClient, responseInterceptor, new Retrofit.Builder().baseUrl(BASE_XREL_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build());
    }
    
    private RestClient(OkHttpClient httpClient, ResponseInterceptor responseInterceptor, Retrofit retrofit) {
        this(httpClient, responseInterceptor, retrofit, retrofit.create(XrelService.class));
    }
    
    private RestClient(OkHttpClient httpClient, ResponseInterceptor responseInterceptor, Retrofit retrofit, XrelService xrelService) {
        this.httpClient = httpClient;
        this.responseInterceptor = responseInterceptor;
        this.retrofit = retrofit;
        this.xrelService = xrelService;
    }
    
    public String getOAuth2Auth(String responseType, String clientId, Optional<String> redirectUri, Optional<String> state, Optional<String[]> scope) {
        String url;
        url = BASE_XREL_URL + "oauth2/auth?response_type=" + URLEncoder.encode(responseType, StandardCharsets.UTF_8) + "&client_id=" + clientId;
        if (redirectUri.isPresent()) {
            url = url + "&redirect_uri=" + URLEncoder.encode(redirectUri.get(), StandardCharsets.UTF_8);
        }
        if (state.isPresent()) {
            url = url + "&state=" + URLEncoder.encode(state.get(), StandardCharsets.UTF_8);
        }
        if (scope.isPresent() && scope.get().length > 0) {
            url = url + "&scope=" + URLEncoder.encode(String.join(" ", scope.get()), StandardCharsets.UTF_8);
        }
        return url;
    }
    
    public OkHttpClient getHttpClient() {
        return httpClient;
    }
    
    public ResponseInterceptor getResponseInterceptor() {
        return responseInterceptor;
    }
    
    public Retrofit getRetrofit() {
        return retrofit;
    }
    
    public XrelService getXrelService() {
        return xrelService;
    }
    
}
