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

import com.github.saftsau.xrel4j.comment.Comment;
import com.github.saftsau.xrel4j.extinfo.ExtInfo;
import com.github.saftsau.xrel4j.extinfo.ExtInfoMedia;
import com.github.saftsau.xrel4j.extinfo.ExtInfoSearchResult;
import com.github.saftsau.xrel4j.favorite.Favorite;
import com.github.saftsau.xrel4j.favorite.FavoriteAddDelEntry;
import com.github.saftsau.xrel4j.favorite.FavoriteMarkRead;
import com.github.saftsau.xrel4j.release.ReleaseSearchResult;
import com.github.saftsau.xrel4j.release.p2p.P2pCategory;
import com.github.saftsau.xrel4j.release.p2p.P2pRelease;
import com.github.saftsau.xrel4j.release.scene.Release;
import com.github.saftsau.xrel4j.release.scene.ReleaseAddProof;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Set;

/**
 * Retrofit2 interface used to define all xREL v2 API endpoints and their parameters.
 */
public interface XrelService {
    
    @GET("release/info" + Xrel.FORMAT)
    Call<Release> releaseInfo(@Query(value = "id") String id, @Query(value = "dirname") String dirname);
    
    @GET("release/latest" + Xrel.FORMAT)
    Call<PaginationList<Release>> releaseLatest(@Header(Xrel.AUTHORIZATION_HEADER) String bearerToken, @Query(value = "per_page") int perPage, @Query(value = "page") int page, @Query(value = "archive") String archive, @Query(value = "filter") String filter);
    
    @GET("release/categories" + Xrel.FORMAT)
    Call<Set<ReleaseCategory>> releaseCategories();
    
    @GET("release/browse_category" + Xrel.FORMAT)
    Call<PaginationList<Release>> releaseBrowseCategory(@Query(value = "category_name") String categoryName, @Query(value = "ext_info_type") String extInfoType, @Query(value = "per_page") int perPage, @Query(value = "page") int page);
    
    @GET("release/ext_info" + Xrel.FORMAT)
    Call<PaginationList<Release>> releaseExtInfo(@Query(value = "id") String id, @Query(value = "per_page") int perPage, @Query(value = "page") int page);
    
    @GET("release/filters" + Xrel.FORMAT)
    Call<Set<Filter>> releaseFilters();
    
    @FormUrlEncoded
    @POST("release/addproof" + Xrel.FORMAT)
    Call<ReleaseAddProof> releaseAddproof(@Header("Authorization") String authorization, @Field("id") Set<String> id, @Field("image") String image);
    
    @GET("p2p/releases" + Xrel.FORMAT)
    Call<PaginationList<P2pRelease>> p2pReleases(@Query(value = "per_page") int perPage, @Query(value = "page") int page, @Query(value = "category_id") String categoryId, @Query(value = "group_id") String groupId, @Query(value = "ext_info_id") String extInfoId);
    
    @GET("p2p/categories" + Xrel.FORMAT)
    Call<Set<P2pCategory>> p2pCategories();
    
    @GET("p2p/rls_info" + Xrel.FORMAT)
    Call<P2pRelease> p2pRlsInfo(@Query(value = "id") String id, @Query(value = "dirname") String dirname);
    
    @GET("nfo/release" + Xrel.FORMAT)
    Call<ResponseBody> nfoRelease(@Header("Authorization") String authorization, @Query(value = "id") String id);
    
    @GET("nfo/p2p_rls" + Xrel.FORMAT)
    Call<ResponseBody> nfoP2pRelease(@Header("Authorization") String authorization, @Query(value = "id") String id);
    
    @GET("calendar/upcoming" + Xrel.FORMAT)
    Call<List<ExtInfo>> calendarUpcoming(@Query(value = "country") String country);
    
    @GET("ext_info/info" + Xrel.FORMAT)
    Call<ExtInfo> extInfoInfo(@Header("Authorization") String authorization, @Query(value = "id") String id);
    
    @GET("ext_info/media" + Xrel.FORMAT)
    Call<List<ExtInfoMedia>> extInfoMedia(@Query(value = "id") String id);
    
    @FormUrlEncoded
    @POST("ext_info/rate" + Xrel.FORMAT)
    Call<ExtInfo> extInfoRate(@Header("Authorization") String authorization, @Field("id") String id, @Field("rating") int rating);
    
    @GET("search/releases" + Xrel.FORMAT)
    Call<ReleaseSearchResult> searchReleases(@Query(value = "q") String q, @Query(value = "scene") boolean scene, @Query(value = "p2p") boolean p2p, @Query(value = "limit") Integer limit);
    
    @GET("search/ext_info" + Xrel.FORMAT)
    Call<ExtInfoSearchResult> searchExtInfo(@Query(value = "q") String q, @Query(value = "type") String type, @Query(value = "limit") Integer limit);
    
    @GET("favs/lists" + Xrel.FORMAT)
    Call<List<Favorite>> favsLists(@Header("Authorization") String authorization);
    
    @GET("favs/list_entries" + Xrel.FORMAT)
    Call<List<ExtInfo>> favsListEntries(@Header("Authorization") String authorization, @Query(value = "id") long id, @Query(value = "get_releases") boolean getReleases);
    
    @FormUrlEncoded
    @POST("favs/list_addentry" + Xrel.FORMAT)
    Call<FavoriteAddDelEntry> favsListAddEntry(@Header("Authorization") String authorization, @Field("id") long id, @Field("ext_info_id") String extInfoId);
    
    @FormUrlEncoded
    @POST("favs/list_delentry" + Xrel.FORMAT)
    Call<FavoriteAddDelEntry> favsListDelEntry(@Header("Authorization") String authorization, @Field("id") long id, @Field("ext_info_id") String extInfoId);
    
    @FormUrlEncoded
    @POST("favs/list_markread" + Xrel.FORMAT)
    Call<FavoriteMarkRead> favsListMarkread(@Header("Authorization") String authorization, @Field("id") long id, @Field("release_id") String releaseId, @Field("type") String type);
    
    @GET("comments/get" + Xrel.FORMAT)
    Call<PaginationList<Comment>> commentsGet(@Query(value = "id") String id, @Query(value = "type") String type, @Query(value = "per_page") int perPage, @Query(value = "page") int page);
    
    @FormUrlEncoded
    @POST("comments/add" + Xrel.FORMAT)
    Call<Comment> commentsAdd(@Header("Authorization") String authorization, @Field(value = "id") String id, @Field(value = "type") String type, @Field(value = "text") String text, @Field(value = "video_rating") Integer videoRating, @Field(value = "audio_rating") Integer audioRating);
    
    @POST("user/info" + Xrel.FORMAT)
    Call<User> userInfo(@Header("Authorization") String authorization);
    
    @FormUrlEncoded
    @POST("oauth2/token" + Xrel.FORMAT)
    Call<Token> oauth2Token(@Field(value = "grant_type") String grantType, @Field(value = "client_id") String clientId, @Field(value = "client_secret") String clientSecret, @Field(value = "code") String code, @Field(value = "redirect_uri") String redirectUri, @Field(value = "refresh_token") String refreshToken, @Field(value = "scope") String scope);
    
}
