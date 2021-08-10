/*
 * Copyright 2017 - 2019 saftsau
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
import com.github.saftsau.xrel4j.release.p2p.P2pGroup;
import com.github.saftsau.xrel4j.release.p2p.P2pRelease;
import com.github.saftsau.xrel4j.release.scene.Release;
import com.github.saftsau.xrel4j.release.scene.ReleaseAddProof;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.util.*;

/**
 * Java implementation of the xREL API v2. Method and parameter names are based on the xREL API with
 * "_" replaced by camel case. Order of methods and parameters are based on the xREL API
 * documentation. It is STRONGLY encouraged to read the xREL API documentation!
 *
 * @see <a href="https://www.xrel.to/wiki/1681/API.html">API Overview</a>
 */
public class Xrel {
    
    private static final String MESSAGE_ARCHIVE_MISSING = "archive missing";
    private static final String MESSAGE_FILTER_MISSING = "filter missing";
    private static final String MESSAGE_TOKEN_MISSING = "token missing";
    private static final String MESSAGE_EXT_INFO_MISSING = "extInfo missing";
    private static final String MESSAGE_CATEGORY_MISSING = "category missing";
    private static final String MESSAGE_EXT_INFO_TYPE_MISSING = "extInfoType missing";
    private static final String MESSAGE_RELEASE_LIST_MISSING = "releaseList missing";
    private static final String MESSAGE_IMAGE_MISSING = "image missing";
    private static final String MESSAGE_P2P_CATEGORY_MISSING = "p2pCategory missing";
    private static final String MESSAGE_P2P_GROUP_MISSING = "p2pGroup missing";
    private static final String MESSAGE_ID_DIR_MISSING = "idDir missing";
    private static final String MESSAGE_DIR_MISSING = "dir missing";
    private static final String MESSAGE_ID_MISSING = "id missing";
    private static final String MESSAGE_RELEASE_MISSING = "release missing";
    private static final String MESSAGE_P2P_RELEASE_MISSING = "p2pRelease missing";
    private static final String MESSAGE_COUNTRY_MISSING = "country missing";
    private static final String MESSAGE_Q_MISSING = "q missing";
    private static final String MESSAGE_TYPE_MISSING = "type missing";
    private static final String MESSAGE_FAVORITE_MISSING = "favorite missing";
    private static final String MESSAGE_TEXT_MISSING = "text missing";
    private static final String MESSAGE_GRANT_TYPE_MISSING = "grantType missing";
    private static final String MESSAGE_CODE_MISSING = "code missing";
    
    private static final String EXCEPTION_MESSAGE_EITHER_SCENE_OR_P2P_MUST_BE_SET_TO_TRUE = "either scene or p2p must be set to true";
    private static final String EXCEPTION_MESSAGE_LIMIT_MUST_BE_1_OR_GREATER = "limit must be 1 or greater";
    private static final String EXCEPTION_MESSAGE_RATINGS_MUST_BE_BETWEEN_1_AND_10 = "ratings must be between 1 and 10";
    
    private static final String RELEASE_TYPE_SCENE = "release";
    private static final String RELEASE_TYPE_P2P = "p2p_rls";
    
    private static final int PAGINATION_PER_PAGE_MIN = 5;
    private static final int PAGINATION_PER_PAGE_MAX = 100;
    private static final String RESPONSE_TYPE = "code";
    
    private final RestClient restClient;
    private final Optional<String> clientId;
    private Optional<String> clientSecret;
    private Optional<String> redirectUri;
    private Optional<String> state;
    private Optional<String[]> scope;
    
    /**
     * Constructs a new xREL object without any oAuth information.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel() {
        this(RestClient.getInstance());
    }
    
    /**
     * Constructs a new xREL object without any oAuth information.
     *
     * @param restClient The RestClient
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(RestClient restClient) {
        this.restClient = restClient;
        this.clientId = Optional.empty();
    }
    
    /**
     * Constructs a new xREL object with oAuth information and no scopes.
     *
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(String clientId, String clientSecret) {
        this(RestClient.getInstance(), clientId, clientSecret);
    }
    
    /**
     * Constructs a new xREL object with oAuth information and no scopes.
     *
     * @param restClient The RestClient
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(RestClient restClient, String clientId, String clientSecret) {
        this.restClient = restClient;
        this.clientId = Optional.of(clientId);
        this.clientSecret = Optional.of(clientSecret);
        this.redirectUri = Optional.empty();
        this.state = Optional.empty();
        this.scope = Optional.empty();
    }
    
    /**
     * Constructs a new xREL object with oAuth information and no scopes.
     *
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     * @param redirectUri Optional URI to redirect to after the authentication. Please read the Guide
     * for more details.
     * @param state Optionally any string. You may set this value to any value, and it will be
     * returned after the authentication. It might also be useful to prevent CSRF attacks.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(String clientId, String clientSecret, Optional<String> redirectUri, Optional<String> state) {
        this(RestClient.getInstance(), clientId, clientSecret, redirectUri, state);
    }
    
    /**
     * Constructs a new xREL object with oAuth information and no scopes.
     *
     * @param restClient The RestClient
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     * @param redirectUri Optional URI to redirect to after the authentication. Please read the Guide
     * for more details.
     * @param state Optionally any string. You may set this value to any value, and it will be
     * returned after the authentication. It might also be useful to prevent CSRF attacks.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(RestClient restClient, String clientId, String clientSecret, Optional<String> redirectUri, Optional<String> state) {
        this.restClient = restClient;
        this.clientId = Optional.of(clientId);
        this.clientSecret = Optional.of(clientSecret);
        this.redirectUri = redirectUri;
        this.state = state;
        this.scope = Optional.empty();
    }
    
    /**
     * Constructs a new xREL object. If you have oAuth access but no additional scopes you should use
     * {@link #Xrel(String, String, Optional, Optional)}.
     *
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     * @param scope Needed to access protected methods. If you do have scope access: you MUST supply
     * these while processing the Tokens, even if you only plan to use them at a later stage.
     * Rule of thumb: if you have these, always add them here.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(String clientId, String clientSecret, String[] scope) {
        this(RestClient.getInstance(), clientId, clientSecret, scope);
    }
    
    /**
     * Constructs a new xREL object. If you have oAuth access but no additional scopes you should use
     * {@link #Xrel(RestClient, String, String, Optional, Optional)}.
     *
     * @param restClient The RestClient
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     * @param scope Needed to access protected methods. If you do have scope access: you MUST supply
     * these while processing the Tokens, even if you only plan to use them at a later stage.
     * Rule of thumb: if you have these, always add them here.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(RestClient restClient, String clientId, String clientSecret, String[] scope) {
        this.restClient = restClient;
        this.clientId = Optional.of(clientId);
        this.clientSecret = Optional.of(clientSecret);
        this.redirectUri = Optional.empty();
        this.state = Optional.empty();
        this.scope = Optional.of(scope);
    }
    
    /**
     * Constructs a new xREL object. If you have oAuth access but no additional scopes you should use
     * {@link #Xrel(String, String, Optional, Optional)}.
     *
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     * @param redirectUri Optional URI to redirect to after the authentication. Please read the Guide
     * for more details.
     * @param state Optionally any string. You may set this value to any value, and it will be
     * returned after the authentication. It might also be useful to prevent CSRF attacks.
     * @param scope Needed to access protected methods. If you do have scope access: you MUST supply
     * these while processing the Tokens, even if you only plan to use them at a later stage.
     * Rule of thumb: if you have these, always add them here.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(String clientId, String clientSecret, Optional<String> redirectUri, Optional<String> state, String[] scope) {
        this(RestClient.getInstance(), clientId, clientSecret, redirectUri, state, scope);
    }
    
    /**
     * Constructs a new xREL object. If you have oAuth access but no additional scopes you should use
     * {@link #Xrel(RestClient, String, String, Optional, Optional)}.
     *
     * @param restClient The RestClient
     * @param clientId Your consumer key.
     * @param clientSecret Your consumer secret.
     * @param redirectUri Optional URI to redirect to after the authentication. Please read the Guide
     * for more details.
     * @param state Optionally any string. You may set this value to any value, and it will be
     * returned after the authentication. It might also be useful to prevent CSRF attacks.
     * @param scope Needed to access protected methods. If you do have scope access: you MUST supply
     * these while processing the Tokens, even if you only plan to use them at a later stage.
     * Rule of thumb: if you have these, always add them here.
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Xrel(RestClient restClient, String clientId, String clientSecret, Optional<String> redirectUri, Optional<String> state, String[] scope) {
        this.restClient = restClient;
        this.clientId = Optional.of(clientId);
        this.clientSecret = Optional.of(clientSecret);
        this.redirectUri = redirectUri;
        this.state = state;
        this.scope = Optional.of(scope);
    }
    
    /**
     * Gets the minimum amount of entries in pagination.
     *
     * @return The minimum pagination amount
     */
    private int getPaginationPerPageMin() {
        return PAGINATION_PER_PAGE_MIN;
    }
    
    /**
     * Gets the maximum amount of entries in pagination.
     *
     * @return The maximum pagination amount
     */
    private int getPaginationPerPageMax() {
        return PAGINATION_PER_PAGE_MAX;
    }
    
    /**
     * Gets the response type used for OAuth.
     *
     * @return The response type
     */
    private String getResponseType() {
        return RESPONSE_TYPE;
    }
    
    /**
     * Gets the set consumer key.
     *
     * @return The consumer key
     */
    public Optional<String> getClientId() {
        return clientId;
    }
    
    /**
     * Gets the set consumer secret.
     *
     * @return The consumer secret
     */
    public Optional<String> getClientSecret() {
        return clientSecret;
    }
    
    /**
     * Gets the set redirect URI.
     *
     * @return The redirect URI
     */
    public Optional<String> getRedirectUri() {
        return redirectUri;
    }
    
    /**
     * Gets the set state used to prevent CSRF attacks.
     *
     * @return The state
     */
    public Optional<String> getState() {
        return state;
    }
    
    /**
     * Gets the set scope.
     *
     * @return The scope
     */
    public Optional<String[]> getScope() {
        return scope;
    }
    
    /**
     * Normalizes the given perPage and page input. perPage is a value between
     * {@link #PAGINATION_PER_PAGE_MIN} and {@link #PAGINATION_PER_PAGE_MAX} and page is a value greater
     * zero.
     *
     * @param perPage The perPage value to normalize
     * @param page The page value to normalize
     *
     * @return An array with perPage on [0] and page on [1]
     */
    private int[] normalizePageValues(int perPage, int page) {
        if (perPage < getPaginationPerPageMin()) {
            perPage = getPaginationPerPageMin();
        } else if (perPage > getPaginationPerPageMax()) {
            perPage = getPaginationPerPageMax();
        }
        if (page < 1) {
            page = 1;
        }
        
        int[] result = new int[2];
        result[0] = perPage;
        result[1] = page;
        return result;
    }
    
    /**
     * Gets the maximum number of requests that the consumer is permitted to make per hour as returned
     * by the last request. -1 if not yet set.
     *
     * @return The X-RateLimit-Limit
     *
     * @see <a href="https://www.xrel.to/wiki/2727/api-rate-limiting.html">API: Rate Limiting</a>
     */
    public int getXRateLimitLimit() {
        return restClient.getResponseInterceptor().getXRateLimitLimit();
    }
    
    /**
     * Gets the number of requests remaining in the current rate limit window as returned by the last
     * request. -1 if not yet set.
     *
     * @return The X-RateLimit-Remaining
     *
     * @see <a href="https://www.xrel.to/wiki/2727/api-rate-limiting.html">API: Rate Limiting</a>
     */
    public int getXRateLimitRemaining() {
        return restClient.getResponseInterceptor().getXRateLimitRemaining();
    }
    
    /**
     * Gets the time at which the current rate limit window resets in UTC epoch seconds as returned by
     * the last request. -1 if not yet set.
     *
     * @return The X-RateLimit-Reset
     *
     * @see <a href="https://www.xrel.to/wiki/2727/api-rate-limiting.html">API: Rate Limiting</a>
     */
    public int getXRateLimitReset() {
        return restClient.getResponseInterceptor().getXRateLimitReset();
    }
    
    /**
     * Checks if a specific scope is not given at creation time.
     *
     * @param scope The scope to check for
     *
     * @return {@code false} if found, {@code true} otherwise
     */
    private boolean denyScope(String scope) {
        final Optional<String[]> optional = getScope();
        if (optional.isEmpty()) {
            throw new XrelException("No Scope provided");
        }
        for (String string : optional.get()) {
            if (Objects.equals(string, scope)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns information about a single release, specified by the complete dirname or an API release
     * id.
     *
     * @param idDir Dirname or API id of the release
     * @param useId If {@code true} uses the idDir as an API ID for the request, if {@code false} uses
     * the idDir as a dirname for the request
     *
     * @return The corresponding {@link Release}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/1680/api-release-info.html">API: release/info method</a>
     */
    private Release getReleaseInfo(String idDir, boolean useId) {
        Objects.requireNonNull(idDir, MESSAGE_ID_DIR_MISSING);
        final XrelService xrelService = restClient.getXrelService();
        final Call<Release> call;
        if (useId) {
            call = xrelService.releaseInfo(idDir, null);
        } else {
            call = xrelService.releaseInfo(null, idDir);
        }
        final Response<Release> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns information about a single release, specified by the complete dirname.
     *
     * @param dir Dirname of the release
     *
     * @return The corresponding {@link Release}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/1680/api-release-info.html">API: release/info method</a>
     */
    public Release getReleaseInfoDir(String dir) {
        Objects.requireNonNull(dir, MESSAGE_DIR_MISSING);
        
        return getReleaseInfo(dir, false);
    }
    
    /**
     * Returns information about a single release, specified by the API release id.
     *
     * @param id API id of the release
     *
     * @return The corresponding {@link Release}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/1680/api-release-info.html">API: release/info method</a>
     */
    public Release getReleaseInfoId(String id) {
        Objects.requireNonNull(id, MESSAGE_ID_MISSING);
        
        return getReleaseInfo(id, true);
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work. You can
     * either provide a {@link Filter}, a {@code Token} or neither, but not both.
     *
     * @param archive YYYY-MM for archive.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param filter Filter (from {@link #getReleaseCategories()}).
     * @param token Uses the overview filter of the respective user
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatestPrivate(String archive, int perPage, int page, Filter filter, Token token) {
        int[] normalizedPageValues = normalizePageValues(perPage, page);
        
        String filterParam = null;
        if (filter != null && token == null) {
            filterParam = String.valueOf(filter.id());
        }
        if (filter == null && token != null) {
            filterParam = "overview";
        }
        String bearerToken = null;
        if (token != null) {
            bearerToken = token.createBearerHeader();
        }
        Call<PaginationList<Release>> call = restClient.getXrelService()
                .releaseLatest(bearerToken, normalizedPageValues[0], normalizedPageValues[1], archive, filterParam);
        
        final Response<PaginationList<Release>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatest(int perPage, int page) {
        return getReleaseLatestPrivate(null, perPage, page, null, null);
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work.
     *
     * @param archive YYYY-MM for archive.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatest(String archive, int perPage, int page) {
        Objects.requireNonNull(archive, MESSAGE_ARCHIVE_MISSING);
        
        return getReleaseLatestPrivate(archive, perPage, page, null, null);
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param filter Filter (from {@link #getReleaseCategories()}).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatest(int perPage, int page, Filter filter) {
        Objects.requireNonNull(filter, MESSAGE_FILTER_MISSING);
        
        return getReleaseLatestPrivate(null, perPage, page, filter, null);
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work.
     *
     * @param archive YYYY-MM for archive.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param filter Filter (from {@link #getReleaseCategories()}).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatest(String archive, int perPage, int page, Filter filter) {
        Objects.requireNonNull(archive, MESSAGE_ARCHIVE_MISSING);
        Objects.requireNonNull(filter, MESSAGE_FILTER_MISSING);
        
        return getReleaseLatestPrivate(archive, perPage, page, filter, null);
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param token Uses the overview filter of the respective user
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatest(int perPage, int page, Token token) {
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return getReleaseLatestPrivate(null, perPage, page, null, token);
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work.
     *
     * @param archive YYYY-MM for archive.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param token Uses the overview filter of the respective user
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatest(String archive, int perPage, int page, Token token) {
        Objects.requireNonNull(archive, MESSAGE_ARCHIVE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return getReleaseLatestPrivate(archive, perPage, page, null, token);
    }
    
    /**
     * Returns the latest releases. Also allows to browse the archive by month. Please note that the
     * latest release list with no archive defined does NOT return a total number of pages. Around
     * 1000 releases are available for browsing in that mode. per_page continues to work. You can
     * either provide a {@link Filter}, a {@code Token} or neither, but not both.
     *
     * @param archive YYYY-MM for archive.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param filter Filter (from {@link #getReleaseCategories()}).
     * @param token Uses the overview filter of the respective user
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/2994/api-release-latest.html">API: release/latest
     * method</a>
     */
    public PaginationList<Release> getReleaseLatest(String archive, int perPage, int page, Filter filter, Token token) {
        Objects.requireNonNull(archive, MESSAGE_ARCHIVE_MISSING);
        Objects.requireNonNull(filter, MESSAGE_FILTER_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return getReleaseLatestPrivate(archive, perPage, page, filter, token);
    }
    
    /**
     * Returns a list of available release categories. You can use the category name in
     * {@link #getReleaseBrowseCategory(ReleaseCategory, String, int, int)}. You should avoid calling
     * this method repeatedly and cache its result for at least 24 hours (where possible).
     *
     * @return The set of {@link ReleaseCategory}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/6318/api-release-categories.html">API:
     * release/categories method</a>
     */
    public Set<ReleaseCategory> getReleaseCategories() {
        Call<Set<ReleaseCategory>> call = restClient.getXrelService().releaseCategories();
        final Response<Set<ReleaseCategory>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        final Set<ReleaseCategory> categorySet = response.body();
        if (categorySet == null) {
            throw new XrelException("No ReleaseCategory found");
        }
        // We put all categories we found into a map, so we can calculate the parent
        // categories
        final Map<String, ReleaseCategory> hashMap = new HashMap<>();
        for (ReleaseCategory releaseCategory : categorySet) {
            hashMap.put(releaseCategory.getName(), releaseCategory);
        }
        for (ReleaseCategory releaseCategory : categorySet) {
            releaseCategory.setParentCat(hashMap.get(releaseCategory.getParentCatName()));
        }
        return categorySet;
    }
    
    /**
     * Returns scene releases from the given category.
     *
     * @param category Category from {@link #getReleaseCategories()}
     * @param extInfoType Use one of: {@code movie}|{@code tv}|{@code game}|{@code console}|{@code
     * software}|{@code xxx} or {@code null}
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3751/api-release-browse-category.html">API:
     * release/browse_category method</a>
     */
    private PaginationList<Release> getReleaseBrowseCategoryPrivate(ReleaseCategory category, String extInfoType, int perPage, int page) {
        int[] normalizedPageValues = normalizePageValues(perPage, page);
        Call<PaginationList<Release>> call = restClient.getXrelService()
                .releaseBrowseCategory(category.getName(), extInfoType, normalizedPageValues[0], normalizedPageValues[1]);
        final Response<PaginationList<Release>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns scene releases from the given category.
     *
     * @param category Category from {@link #getReleaseCategories()}
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3751/api-release-browse-category.html">API:
     * release/browse_category method</a>
     */
    public PaginationList<Release> getReleaseBrowseCategory(ReleaseCategory category, int perPage, int page) {
        return getReleaseBrowseCategoryPrivate(category, null, perPage, page);
    }
    
    /**
     * Returns scene releases from the given category.
     *
     * @param category Category from {@link #getReleaseCategories()}
     * @param extInfoType Use one of: {@code movie}|{@code tv}|{@code game}|{@code console}|{@code
     * software}|{@code xxx}
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3751/api-release-browse-category.html">API:
     * release/browse_category method</a>
     */
    public PaginationList<Release> getReleaseBrowseCategory(ReleaseCategory category, String extInfoType, int perPage, int page) {
        Objects.requireNonNull(category, MESSAGE_CATEGORY_MISSING);
        Objects.requireNonNull(extInfoType, MESSAGE_EXT_INFO_TYPE_MISSING);
        
        return getReleaseBrowseCategoryPrivate(category, extInfoType, perPage, page);
    }
    
    /**
     * Returns all releases associated with a given Ext Info.
     *
     * @param extInfo Ext info.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The list of {@link Release} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     */
    public PaginationList<Release> getReleaseExtInfo(ExtInfo extInfo, int perPage, int page) {
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        final int[] normalizedPageValues = normalizePageValues(perPage, page);
        Call<PaginationList<Release>> call = restClient.getXrelService()
                .releaseExtInfo(extInfo.getId(), normalizedPageValues[0], normalizedPageValues[1]);
        final Response<PaginationList<Release>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns a set of public, predefined release filters. You can use the filter ID in
     * {@code #getReleaseLatest(Optional, int, int, Optional, Optional)}. You should avoid calling
     * this method repeatedly and cache its result for at least 24 hours (where possible).
     *
     * @return The set of {@link Filter}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/2996/api-release-filters.html">API: release/filters
     * method</a>
     */
    public Set<Filter> getReleaseFilters() {
        Call<Set<Filter>> call = restClient.getXrelService().releaseFilters();
        final Response<Set<Filter>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Adds a proof picture to a given API release id. This method requires the addproof scope. Please
     * read the rules before posting proofs.
     *
     * @param releaseList The list of releases this proof should be added to.
     * @param image Base64 encoded image
     * @param token The {@link Token} with all needed info
     *
     * @return The {@link ReleaseAddProof} containing the URL of the proof and the {@link List} of
     * {@link Release}. Careful: every {@link Release} only contains an ID.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6444/api-release-addproof.html">API: release/addproof
     * method</a>
     */
    public ReleaseAddProof postReleaseAddProof(List<Release> releaseList, String image, Token token) {
        Objects.requireNonNull(releaseList, MESSAGE_RELEASE_LIST_MISSING);
        Objects.requireNonNull(image, MESSAGE_IMAGE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        if (getScope().isEmpty() || denyScope("addproof")) {
            throw new XrelException("addproof scope not provided");
        }
        final Set<String> ids = new HashSet<>();
        for (Release release : releaseList) {
            ids.add(release.getId());
        }
        final Call<ReleaseAddProof> call = restClient.getXrelService().releaseAddproof(token.createBearerHeader(), ids, image);
        final Response<ReleaseAddProof> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param p2pCategory Optional P2P category ID from {@link #getP2pCategories()}
     * @param p2pGroup Optional P2P release group
     * @param extInfo Optional ExtInfo
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    private PaginationList<P2pRelease> getP2pReleasesPrivate(int perPage, int page, P2pCategory p2pCategory, P2pGroup p2pGroup, ExtInfo extInfo) {
        int[] normalizedPageValues = normalizePageValues(perPage, page);
        
        String categoryId = null;
        if (p2pCategory != null) {
            categoryId = p2pCategory.getId();
        }
        String groupId = null;
        if (p2pGroup != null) {
            groupId = p2pGroup.id();
        }
        String extInfoId = null;
        if (extInfo != null) {
            extInfoId = extInfo.getId();
        }
        Call<PaginationList<P2pRelease>> call = restClient.getXrelService()
                .p2pReleases(normalizedPageValues[0], normalizedPageValues[1], categoryId, groupId, extInfoId);
        final Response<PaginationList<P2pRelease>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    public PaginationList<P2pRelease> getP2pReleases(int perPage, int page) {
        return getP2pReleasesPrivate(perPage, page, null, null, null);
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param p2pCategory P2P category ID from {@link #getP2pCategories()}
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    public PaginationList<P2pRelease> getP2pReleases(int perPage, int page, P2pCategory p2pCategory) {
        Objects.requireNonNull(p2pCategory, MESSAGE_P2P_CATEGORY_MISSING);
        
        return getP2pReleasesPrivate(perPage, page, p2pCategory, null, null);
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param p2pCategory P2P category ID from {@link #getP2pCategories()}
     * @param p2pGroup P2P release group
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    public PaginationList<P2pRelease> getP2pReleases(int perPage, int page, P2pCategory p2pCategory, P2pGroup p2pGroup) {
        Objects.requireNonNull(p2pCategory, MESSAGE_P2P_CATEGORY_MISSING);
        Objects.requireNonNull(p2pGroup, MESSAGE_P2P_GROUP_MISSING);
        
        return getP2pReleasesPrivate(perPage, page, p2pCategory, p2pGroup, null);
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param p2pGroup P2P release group
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    public PaginationList<P2pRelease> getP2pReleases(int perPage, int page, P2pGroup p2pGroup) {
        Objects.requireNonNull(p2pGroup, MESSAGE_P2P_GROUP_MISSING);
        
        return getP2pReleasesPrivate(perPage, page, null, p2pGroup, null);
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param p2pGroup P2P release group
     * @param extInfo ExtInfo
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    public PaginationList<P2pRelease> getP2pReleases(int perPage, int page, P2pGroup p2pGroup, ExtInfo extInfo) {
        Objects.requireNonNull(p2pGroup, MESSAGE_P2P_GROUP_MISSING);
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        
        return getP2pReleasesPrivate(perPage, page, null, p2pGroup, extInfo);
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param extInfo ExtInfo
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    public PaginationList<P2pRelease> getP2pReleases(int perPage, int page, ExtInfo extInfo) {
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        
        return getP2pReleasesPrivate(perPage, page, null, null, extInfo);
    }
    
    /**
     * Browse P2P/non-scene releases. Please note that the latest release list does NOT return a total
     * number of pages. Around 1000 releases are available for browsing in that mode. per_page
     * continues to work.
     *
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     * @param p2pCategory P2P category ID from {@link #getP2pCategories()}
     * @param p2pGroup P2P release group
     * @param extInfo ExtInfo
     *
     * @return The list of {@link P2pRelease} matching the criteria
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3699/api-p2p-releases.html">API: p2p/releases</a>
     */
    public PaginationList<P2pRelease> getP2pReleases(int perPage, int page, P2pCategory p2pCategory, P2pGroup p2pGroup, ExtInfo extInfo) {
        Objects.requireNonNull(p2pCategory, MESSAGE_P2P_CATEGORY_MISSING);
        Objects.requireNonNull(p2pGroup, MESSAGE_P2P_GROUP_MISSING);
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        
        return getP2pReleasesPrivate(perPage, page, p2pCategory, p2pGroup, extInfo);
    }
    
    /**
     * Returns a set of available P2P release categories and their IDs. You can use the category in
     * {@link #getP2pReleases(int, int, P2pCategory, P2pGroup, ExtInfo)}.
     *
     * @return The list of {@link P2pCategory}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3698/api-p2p-categories.html">API: p2p/categories</a>
     */
    public Set<P2pCategory> getP2pCategories() {
        Call<Set<P2pCategory>> call = restClient.getXrelService().p2pCategories();
        final Response<Set<P2pCategory>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns information about a single P2P/non-scene release, specified by the complete dirname or
     * an API P2P release id.
     *
     * @param idDir Dirname or API id of the release
     * @param useId If {@code true} uses the idDir as an API ID for the request, if {@code false} uses
     * the idDir as a dirname for the request
     *
     * @return The {@link P2pRelease}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3697/api-p2p-rls-info.html">API: p2p/rls_info</a>
     */
    private P2pRelease getP2pRlsInfo(String idDir, boolean useId) {
        Objects.requireNonNull(idDir, MESSAGE_ID_DIR_MISSING);
        final Call<P2pRelease> call;
        if (useId) {
            call = restClient.getXrelService().p2pRlsInfo(idDir, null);
        } else {
            call = restClient.getXrelService().p2pRlsInfo(null, idDir);
        }
        final Response<P2pRelease> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns information about a single P2P/non-scene release, specified by the complete dirname.
     *
     * @param dir The dirname of the P2P release
     *
     * @return The {@link P2pRelease}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3697/api-p2p-rls-info.html">API: p2p/rls_info</a>
     */
    public P2pRelease getP2pRlsInfoDir(String dir) {
        Objects.requireNonNull(dir, MESSAGE_DIR_MISSING);
        
        return getP2pRlsInfo(dir, false);
    }
    
    /**
     * Returns information about a single P2P/non-scene release, specified by the API P2P release id.
     *
     * @param id The API P2P release id
     *
     * @return The {@link P2pRelease}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/3697/api-p2p-rls-info.html">API: p2p/rls_info</a>
     */
    public P2pRelease getP2pRlsInfoId(String id) {
        Objects.requireNonNull(id, MESSAGE_ID_MISSING);
        
        return getP2pRlsInfo(id, true);
    }
    
    /**
     * Returns an image of a NFO file for a given API release.
     *
     * @param release The {@link Release} you want the NFO of
     * @param token The {@link Token} with all needed info
     *
     * @return The NFO as byte[]
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6438/api-nfo-release.html">API: nfo/release method</a>
     */
    public byte[] getNfoRelease(Release release, Token token) {
        Objects.requireNonNull(release, MESSAGE_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        if (getScope().isEmpty() || denyScope("viewnfo")) {
            throw new XrelException("viewnfo scope not provided");
        }
        final Call<ResponseBody> call = restClient.getXrelService().nfoRelease(token.createBearerHeader(), release.getId());
        final byte[] nfo;
        try {
            final Response<ResponseBody> response = call.execute();
            nfo = response.body().bytes();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return nfo;
    }
    
    /**
     * Returns an image of a NFO file for a given API P2P release.
     *
     * @param p2pRelease The {@link P2pRelease} you want the NFO of
     * @param token The {@link Token} with all needed info
     *
     * @return The NFO as byte[]
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6437/api-nfo-p2p-rls.html">API: nfo/p2p_rls method</a>
     */
    public byte[] getNfoP2pRls(P2pRelease p2pRelease, Token token) {
        Objects.requireNonNull(p2pRelease, MESSAGE_P2P_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        if (getScope().isEmpty() || denyScope("viewnfo")) {
            throw new XrelException("viewnfo scope not provided");
        }
        final Call<ResponseBody> call = restClient.getXrelService().nfoP2pRelease(token.createBearerHeader(), p2pRelease.getId());
        try {
            return Objects.requireNonNull(call.execute().body()).bytes();
        } catch (Exception e) {
            throw new XrelException(e);
        }
    }
    
    /**
     * Returns a list upcoming movies and their releases.
     *
     * @param country {@code de} for upcoming movies in germany, {@code us} for upcoming movies in the
     * US/international.
     *
     * @return The list of {@link ExtInfo}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/1827/api-calendar-upcoming.html">API: calendar/upcoming
     * method</a>
     */
    public List<ExtInfo> getCalendarUpcoming(String country) {
        Objects.requireNonNull(country, MESSAGE_COUNTRY_MISSING);
        if (!country.equals("de") && !country.equals("us")) {
            throw new XrelException("country must be either de or us");
        }
        final Call<List<ExtInfo>> call = restClient.getXrelService().calendarUpcoming(country);
        final Response<List<ExtInfo>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns information about an Ext Info.
     *
     * @param extInfo The {@link ExtInfo} you want more info about.
     * @param token Your optional {@link Token}. If supplied you will also get {@code own_rating} from
     * this {@link ExtInfo}.
     *
     * @return The new {@link ExtInfo}.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/2725/api-ext-info-info.html">API: ext_info/info
     * method</a>
     */
    private ExtInfo getExtInfoInfoPrivate(ExtInfo extInfo, Token token) {
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        String authorization = null;
        if (token != null) {
            authorization = token.createBearerHeader();
        }
        final Call<ExtInfo> call = restClient.getXrelService().extInfoInfo(authorization, extInfo.getId());
        final Response<ExtInfo> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns information about an Ext Info.
     *
     * @param extInfo The {@link ExtInfo} you want more info about.
     *
     * @return The new {@link ExtInfo}.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/2725/api-ext-info-info.html">API: ext_info/info
     * method</a>
     */
    public ExtInfo getExtInfoInfo(ExtInfo extInfo) {
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        
        return getExtInfoInfoPrivate(extInfo, null);
    }
    
    /**
     * Returns information about an Ext Info.
     *
     * @param extInfo The {@link ExtInfo} you want more info about.
     * @param token Your {@link Token}. If supplied you will also get {@code own_rating} from this
     * {@link ExtInfo}.
     *
     * @return The new {@link ExtInfo}.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/2725/api-ext-info-info.html">API: ext_info/info
     * method</a>
     */
    public ExtInfo getExtInfoInfo(ExtInfo extInfo, Token token) {
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return getExtInfoInfoPrivate(extInfo, token);
    }
    
    /**
     * Returns media associated with an Ext Info.
     *
     * @param extInfo The ExtInfo which media you want to retrieve. The found ExtInfoMedia will be
     * added to the given ExtInfo.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6314/api-ext-info-media.html">API: ext_info/media
     * method</a>
     */
    public void getExtInfoMedia(ExtInfo extInfo) {
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        final Call<List<ExtInfoMedia>> call = restClient.getXrelService().extInfoMedia(extInfo.getId());
        final Response<List<ExtInfoMedia>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        final List<ExtInfoMedia> extInfoMediaList = response.body();
        if (extInfoMediaList == null) {
            throw new XrelException("No ExtInfoMedia found");
        }
        if (extInfo.getExtInfoMedia() != null) {
            extInfo.getExtInfoMedia().clear();
            extInfo.getExtInfoMedia().addAll(extInfoMediaList);
        } else {
            extInfo.setExtInfoMedia(extInfoMediaList);
        }
    }
    
    /**
     * Rate an Ext Info. Also updates the given {@link ExtInfo} object.
     *
     * @param extInfo The {@link ExtInfo} to rate.
     * @param rating Rating between 1 (bad) to 10 (good). You may only vote once, and may not change
     * your vote. Check the ownRating property from the response to get the rating as displayed
     * on the website.
     * @param token The {@link Token} with all needed info.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6315/api-ext-info-rate.html">API: ext_info/rate
     * method</a>
     */
    public void postExtInfoRate(ExtInfo extInfo, int rating, Token token) {
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        if (rating < 1 || rating > 10) {
            throw new XrelException("rating must be in the range of 1 - 10");
        }
        final Call<ExtInfo> call = restClient.getXrelService().extInfoRate(token.createBearerHeader(), extInfo.getId(), rating);
        final Response<ExtInfo> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        final ExtInfo extInfoRated = response.body();
        if (extInfoRated == null) {
            throw new XrelException("No ExtInfo rated found");
        }
        extInfo.setOwnRating(extInfoRated.getOwnRating());
    }
    
    /**
     * Searches for Scene and P2P releases. For all calls to search methods, additional rate limiting
     * applies. Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     * @param scene If {@code true}, Scene releases will be included in the search results.
     * @param p2p If {@code true}, P2P releases will be included in the search results.
     * @param limit Number of returned search results. {@code -1} to disable.
     *
     * @return A pair object containing the lists of {@link Release} and {@link P2pRelease}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6320/api-search-releases.html">API: search/releases
     * method</a>
     */
    private ReleaseSearchResult getSearchReleasesPrivate(String q, boolean scene, boolean p2p, Integer limit) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        if ((!p2p) && (!scene)) {
            throw new XrelException(EXCEPTION_MESSAGE_EITHER_SCENE_OR_P2P_MUST_BE_SET_TO_TRUE);
        }
        if (limit != null && limit < 1) {
            throw new XrelException("limit must be either null or greater than 1");
        }
        final Call<ReleaseSearchResult> call = restClient.getXrelService().searchReleases(q, scene, p2p, limit);
        final Response<ReleaseSearchResult> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Searches for Scene and P2P releases. For all calls to search methods, additional rate limiting
     * applies. Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     * @param scene If {@code true}, Scene releases will be included in the search results.
     * @param p2p If {@code true}, P2P releases will be included in the search results.
     * @param limit Number of returned search results.
     *
     * @return A pair object containing the lists of {@link Release} and {@link P2pRelease}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6320/api-search-releases.html">API: search/releases
     * method</a>
     */
    public ReleaseSearchResult getSearchReleases(String q, boolean scene, boolean p2p, int limit) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        
        if ((!p2p) && (!scene)) {
            throw new XrelException(EXCEPTION_MESSAGE_EITHER_SCENE_OR_P2P_MUST_BE_SET_TO_TRUE);
        }
        
        if (limit < 1) {
            throw new XrelException(EXCEPTION_MESSAGE_LIMIT_MUST_BE_1_OR_GREATER);
        }
        
        return getSearchReleasesPrivate(q, scene, p2p, limit);
    }
    
    /**
     * Searches for Scene and P2P releases. For all calls to search methods, additional rate limiting
     * applies. Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     * @param scene If {@code true}, Scene releases will be included in the search results.
     * @param p2p If {@code true}, P2P releases will be included in the search results.
     *
     * @return A pair object containing the lists of {@link Release} and {@link P2pRelease}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6320/api-search-releases.html">API: search/releases
     * method</a>
     */
    public ReleaseSearchResult getSearchReleases(String q, boolean scene, boolean p2p) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        
        if ((!p2p) && (!scene)) {
            throw new XrelException(EXCEPTION_MESSAGE_EITHER_SCENE_OR_P2P_MUST_BE_SET_TO_TRUE);
        }
        
        return getSearchReleasesPrivate(q, scene, p2p, null);
    }
    
    /**
     * Searches for Ext Infos. For all calls to search methods, additional rate limiting applies.
     * Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     * @param type One of: {@code movie}|{@code tv}|{@code game}|{@code console}|{@code
     * software}|{@code xxx} - or {@code null} to browse releases of all types
     * @param limit Number of returned search results. {@code -1} to disable.
     *
     * @return List of {@link ExtInfo}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6319/api-search-ext-info.html">API: search/ext_info
     * method</a>
     */
    private ExtInfoSearchResult getSearchExtInfoPrivate(String q, String type, Integer limit) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        if (limit != null && limit < 1) {
            throw new XrelException("limit must be either -1 or greater than 1");
        }
        final Call<ExtInfoSearchResult> call = restClient.getXrelService().searchExtInfo(q, type, limit);
        final Response<ExtInfoSearchResult> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Searches for Ext Infos. For all calls to search methods, additional rate limiting applies.
     * Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     *
     * @return List of {@link ExtInfo}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6319/api-search-ext-info.html">API: search/ext_info
     * method</a>
     */
    public ExtInfoSearchResult getSearchExtInfo(String q) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        
        return getSearchExtInfoPrivate(q, null, null);
    }
    
    /**
     * Searches for Ext Infos. For all calls to search methods, additional rate limiting applies.
     * Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     * @param limit Number of returned search results.
     *
     * @return List of {@link ExtInfo}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6319/api-search-ext-info.html">API: search/ext_info
     * method</a>
     */
    public ExtInfoSearchResult getSearchExtInfo(String q, int limit) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        
        if (limit < 1) {
            throw new XrelException(EXCEPTION_MESSAGE_LIMIT_MUST_BE_1_OR_GREATER);
        }
        
        return getSearchExtInfoPrivate(q, null, limit);
    }
    
    /**
     * Searches for Ext Infos. For all calls to search methods, additional rate limiting applies.
     * Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     * @param type One of: {@code movie}|{@code tv}|{@code game}|{@code console}|{@code
     * software}|{@code xxx}
     *
     * @return List of {@link ExtInfo}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6319/api-search-ext-info.html">API: search/ext_info
     * method</a>
     */
    public ExtInfoSearchResult getSearchExtInfo(String q, String type) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        Objects.requireNonNull(type, MESSAGE_TYPE_MISSING);
        
        return getSearchExtInfoPrivate(q, type, null);
    }
    
    /**
     * Searches for Ext Infos. For all calls to search methods, additional rate limiting applies.
     * Currently this limit is set at 2 calls per 10 seconds. Please keep track of that limit
     * yourself.
     *
     * @param q Search keyword.
     * @param type One of: {@code movie}|{@code tv}|{@code game}|{@code console}|{@code
     * software}|{@code xxx}
     * @param limit Number of returned search results.
     *
     * @return List of {@link ExtInfo}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6319/api-search-ext-info.html">API: search/ext_info
     * method</a>
     */
    public ExtInfoSearchResult getSearchExtInfo(String q, String type, int limit) {
        Objects.requireNonNull(q, MESSAGE_Q_MISSING);
        Objects.requireNonNull(type, MESSAGE_TYPE_MISSING);
        
        if (limit < 1) {
            throw new XrelException(EXCEPTION_MESSAGE_LIMIT_MUST_BE_1_OR_GREATER);
        }
        
        return getSearchExtInfoPrivate(q, type, limit);
    }
    
    /**
     * Returns a list of all the current user's favorite lists.
     *
     * @param token The {@link Token} with all needed info.
     *
     * @return A list of all {@link Favorite}.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/1754/api-favs-lists.html">API: favs/lists method</a>
     */
    public List<Favorite> getFavsLists(Token token) {
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        final Call<List<Favorite>> call = restClient.getXrelService().favsLists(token.createBearerHeader());
        final Response<List<Favorite>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Retrieves entries of a favorite list and adds the retrieved entries to the {@link Favorite}
     * object you provided.
     *
     * @param favorite The favorite list, as obtained through {@link #getFavsLists(Token)}.
     * @param getReleases If {@code true}, a list of unread(!) releases will be returned with each
     * ext_info entry.
     * @param token The {@link Token} with all needed info.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/1823/api-favs-list-entries.html">API: favs/list_entries
     * method</a>
     */
    public void getFavsListEntries(Favorite favorite, boolean getReleases, Token token) {
        Objects.requireNonNull(favorite, MESSAGE_FAVORITE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        final Call<List<ExtInfo>> call = restClient.getXrelService().favsListEntries(token.createBearerHeader(), favorite.getId(), getReleases);
        final Response<List<ExtInfo>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        final List<ExtInfo> extInfoList = response.body();
        if (extInfoList == null) {
            throw new XrelException("No ExtInfo found");
        }
        if (favorite.getEntries() != null) {
            favorite.getEntries().clear();
            favorite.getEntries().addAll(extInfoList);
        } else {
            favorite.setEntries(extInfoList);
        }
    }
    
    /**
     * Add or removes an Ext Info to / from a favorite list.
     *
     * @param favorite The favorite list, as obtained through {@link #getFavsLists(Token)}.
     * @param extInfo The {@link ExtInfo} to add or remove.
     * @param token The {@link Token} with all needed info.
     * @param delete {@code true} if {@link ExtInfo} should be removed or {@code false} if
     * {@link ExtInfo} should be added.
     *
     * @return The new {@link Favorite}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6316/api-favs-list-addentry.html">API:
     * favs/list_addentry method</a><br>
     * <a href= "https://www.xrel.to/wiki/6317/api-favs-list-delentry.html">API:
     * favs/list_delentry method</a>
     */
    private FavoriteAddDelEntry postFavsListAddDelEntry(Favorite favorite, ExtInfo extInfo, Token token, boolean delete) {
        Objects.requireNonNull(favorite, MESSAGE_FAVORITE_MISSING);
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        final Call<FavoriteAddDelEntry> call;
        if (delete) {
            call = restClient.getXrelService().favsListDelEntry(token.createBearerHeader(), favorite.getId(), extInfo.getId());
        } else {
            call = restClient.getXrelService().favsListAddEntry(token.createBearerHeader(), favorite.getId(), extInfo.getId());
        }
        final Response<FavoriteAddDelEntry> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Add an Ext Info to a favorite list.
     *
     * @param favorite The favorite list, as obtained through {@link #getFavsLists(Token)}.
     * @param extInfo The {@link ExtInfo} to add.
     * @param token The {@link Token} with all needed info.
     *
     * @return The new {@link Favorite}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6316/api-favs-list-addentry.html">API:
     * favs/list_addentry method</a>
     */
    public FavoriteAddDelEntry postFavsListAddEntry(Favorite favorite, ExtInfo extInfo, Token token) {
        Objects.requireNonNull(favorite, MESSAGE_FAVORITE_MISSING);
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return postFavsListAddDelEntry(favorite, extInfo, token, false);
    }
    
    /**
     * Removes an Ext Info from a favorite list.
     *
     * @param favorite The favorite list, as obtained through {@link #getFavsLists(Token)}.
     * @param extInfo The {@link ExtInfo} to remove.
     * @param token The {@link Token} with all needed info.
     *
     * @return The new {@link Favorite}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6317/api-favs-list-delentry.html">API:
     * favs/list_delentry method</a>
     */
    public FavoriteAddDelEntry postFavsListDelEntry(Favorite favorite, ExtInfo extInfo, Token token) {
        Objects.requireNonNull(favorite, MESSAGE_FAVORITE_MISSING);
        Objects.requireNonNull(extInfo, MESSAGE_EXT_INFO_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return postFavsListAddDelEntry(favorite, extInfo, token, true);
    }
    
    /**
     * Mark a {@link Release} or {@link P2pRelease} on a favorite list as read.
     *
     * @param favorite The favorite list, as obtained through {@link #getFavsLists(Token)}.
     * @param release The {@link Release} to be marked as read or {@code null}.
     * @param p2pRelease The {@link P2pRelease} to be marked as read or {@code null}.
     * @param token The {@link Token} with all needed info.
     *
     * @return The new {@link FavoriteMarkRead}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6344/api-favs-list-markread.html">API:
     * favs/list_markread method</a>
     */
    private FavoriteMarkRead postFavsListMarkRead(Favorite favorite, Release release, P2pRelease p2pRelease, Token token) {
        Objects.requireNonNull(favorite, MESSAGE_FAVORITE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        String releaseId;
        String type;
        if (release != null) {
            releaseId = release.getId();
            type = RELEASE_TYPE_SCENE;
        } else {
            releaseId = p2pRelease.getId();
            type = RELEASE_TYPE_P2P;
        }
        final Call<FavoriteMarkRead> call = restClient.getXrelService()
                .favsListMarkread(token.createBearerHeader(), favorite.getId(), releaseId, type);
        final Response<FavoriteMarkRead> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Mark a {@link Release} on a favorite list as read.
     *
     * @param favorite The favorite list, as obtained through {@link #getFavsLists(Token)}.
     * @param release The {@link Release} to be marked as read.
     * @param token The {@link Token} with all needed info.
     *
     * @return The new {@link FavoriteMarkRead}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6344/api-favs-list-markread.html">API:
     * favs/list_markread method</a>
     */
    public FavoriteMarkRead postFavsListMarkRead(Favorite favorite, Release release, Token token) {
        Objects.requireNonNull(favorite, MESSAGE_FAVORITE_MISSING);
        Objects.requireNonNull(release, MESSAGE_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return postFavsListMarkRead(favorite, release, null, token);
    }
    
    /**
     * Mark a {@link P2pRelease} on a favorite list as read.
     *
     * @param favorite The favorite list, as obtained through {@link #getFavsLists(Token)}.
     * @param p2pRelease The {@link P2pRelease} to be marked as read.
     * @param token The {@link Token} with all needed info.
     *
     * @return The new {@link FavoriteMarkRead}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6344/api-favs-list-markread.html">API:
     * favs/list_markread method</a>
     */
    public FavoriteMarkRead postFavsListMarkRead(Favorite favorite, P2pRelease p2pRelease, Token token) {
        Objects.requireNonNull(favorite, MESSAGE_FAVORITE_MISSING);
        Objects.requireNonNull(p2pRelease, MESSAGE_P2P_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return postFavsListMarkRead(favorite, null, p2pRelease, token);
    }
    
    /**
     * Returns comments for a given {@link Release} or {@link P2pRelease}.
     *
     * @param release The corresponding {@link Release} or {@code null}.
     * @param p2pRelease The corresponding {@link P2pRelease} or {@code null}.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The {@link PaginationList} containing the {@link Comment}.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6313/api-comments-get.html">API: comments/get
     * method</a>
     */
    private PaginationList<Comment> getCommentsGet(Release release, P2pRelease p2pRelease, int perPage, int page) {
        int[] normalizedPageValues = normalizePageValues(perPage, page);
        String id;
        String type;
        if (release != null) {
            id = release.getId();
            type = RELEASE_TYPE_SCENE;
        } else {
            id = p2pRelease.getId();
            type = RELEASE_TYPE_P2P;
        }
        final Call<PaginationList<Comment>> call = restClient.getXrelService()
                .commentsGet(id, type, normalizedPageValues[0], normalizedPageValues[1]);
        final Response<PaginationList<Comment>> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Returns comments for a given {@link Release}.
     *
     * @param release The corresponding {@link Release}.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The {@link PaginationList} containing the {@link Comment}.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6313/api-comments-get.html">API: comments/get
     * method</a>
     */
    public PaginationList<Comment> getCommentsGet(Release release, int perPage, int page) {
        Objects.requireNonNull(release, MESSAGE_RELEASE_MISSING);
        
        return getCommentsGet(release, null, perPage, page);
    }
    
    /**
     * Returns comments for a given {@link P2pRelease}.
     *
     * @param p2pRelease The corresponding {@link P2pRelease}.
     * @param perPage Number of releases per page. Min. 5, max. 100.
     * @param page Page number (1 to N).
     *
     * @return The {@link PaginationList} containing the {@link Comment}.
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6313/api-comments-get.html">API: comments/get
     * method</a>
     */
    public PaginationList<Comment> getCommentsGet(P2pRelease p2pRelease, int perPage, int page) {
        Objects.requireNonNull(p2pRelease, MESSAGE_P2P_RELEASE_MISSING);
        
        return getCommentsGet(null, p2pRelease, perPage, page);
    }
    
    /**
     * Add a comment to a given {@link Release} or {@link P2pRelease}. The text may contain BBCode.
     * Supplying either a text, a rating (both audio and video) or both is mandatory.
     *
     * @param release The {@link Release} to add a comment to or {@code null}.
     * @param p2pRelease The {@link P2pRelease} to add a comment to or {@code null}.
     * @param text The comment. You may use BBCode to format the text. Can be {@code null} if both
     * videoRating and audioRating are set.
     * @param videoRating Video rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website. Use {@code null} to disable.
     * @param audioRating Audio rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website. Use {@code null} to disable.
     * @param token The {@link Token} with all needed info.
     *
     * @return The added {@link Comment}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6312/api-comments-add.html">API: comments/add
     * method</a>
     */
    private Comment postCommentsAdd(Release release, P2pRelease p2pRelease, String text, Integer videoRating, Integer audioRating, Token token) {
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        String id;
        String type;
        if (release != null) {
            id = release.getId();
            type = RELEASE_TYPE_SCENE;
        } else {
            id = p2pRelease.getId();
            type = RELEASE_TYPE_P2P;
        }
        final Call<Comment> call = restClient.getXrelService().commentsAdd(token.createBearerHeader(), id, type, text, videoRating, audioRating);
        final Response<Comment> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * Add a comment to a given {@link Release}. The text may contain BBCode. Supplying either a text,
     * a rating (both audio and video) or both is mandatory.
     *
     * @param release The {@link Release} to add a comment to.
     * @param videoRating Video rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param audioRating Audio rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param token The {@link Token} with all needed info.
     *
     * @return The added {@link Comment}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6312/api-comments-add.html">API: comments/add
     * method</a>
     */
    public Comment postCommentsAdd(Release release, int videoRating, int audioRating, Token token) {
        Objects.requireNonNull(release, MESSAGE_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        if (videoRating < 1 || audioRating < 1 || videoRating > 10 || audioRating > 10) {
            throw new XrelException(EXCEPTION_MESSAGE_RATINGS_MUST_BE_BETWEEN_1_AND_10);
        }
        
        return postCommentsAdd(release, null, null, videoRating, audioRating, token);
    }
    
    /**
     * Add a comment to a given {@link Release}. The text may contain BBCode. Supplying either a text,
     * a rating (both audio and video) or both is mandatory.
     *
     * @param release The {@link Release} to add a comment to.
     * @param text The comment. You may use BBCode to format the text. Can be {@code null} if both
     * videoRating and audioRating are set.
     * @param token The {@link Token} with all needed info.
     *
     * @return The added {@link Comment}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6312/api-comments-add.html">API: comments/add
     * method</a>
     */
    public Comment postCommentsAdd(Release release, String text, Token token) {
        Objects.requireNonNull(release, MESSAGE_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        Objects.requireNonNull(text, MESSAGE_TEXT_MISSING);
        
        return postCommentsAdd(release, null, text, null, null, token);
    }
    
    /**
     * Add a comment to a given {@link Release}. The text may contain BBCode. Supplying either a text,
     * a rating (both audio and video) or both is mandatory.
     *
     * @param release The {@link Release} to add a comment to.
     * @param text The comment. You may use BBCode to format the text. Can be {@code null} if both
     * videoRating and audioRating are set.
     * @param videoRating Video rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param audioRating Audio rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param token The {@link Token} with all needed info.
     *
     * @return The added {@link Comment}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6312/api-comments-add.html">API: comments/add
     * method</a>
     */
    public Comment postCommentsAdd(Release release, String text, int videoRating, int audioRating, Token token) {
        Objects.requireNonNull(release, MESSAGE_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        Objects.requireNonNull(text, MESSAGE_TEXT_MISSING);
        
        if (videoRating < 1 || audioRating < 1 || videoRating > 10 || audioRating > 10) {
            throw new XrelException(EXCEPTION_MESSAGE_RATINGS_MUST_BE_BETWEEN_1_AND_10);
        }
        
        return postCommentsAdd(release, null, text, videoRating, audioRating, token);
    }
    
    /**
     * Add a comment to a given {@link P2pRelease}. The text may contain BBCode.
     *
     * @param p2pRelease The {@link P2pRelease} to add a comment to.
     * @param videoRating Video rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param audioRating Audio rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param token The {@link Token} with all needed info
     *
     * @return The added {@link Comment}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6312/api-comments-add.html">API: comments/add
     * method</a>
     */
    public Comment postCommentsAdd(P2pRelease p2pRelease, int videoRating, int audioRating, Token token) {
        Objects.requireNonNull(p2pRelease, MESSAGE_P2P_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        if (videoRating < 1 || audioRating < 1 || videoRating > 10 || audioRating > 10) {
            throw new XrelException(EXCEPTION_MESSAGE_RATINGS_MUST_BE_BETWEEN_1_AND_10);
        }
        
        return postCommentsAdd(null, p2pRelease, null, videoRating, audioRating, token);
    }
    
    /**
     * Add a comment to a given {@link P2pRelease}. The text may contain BBCode.
     *
     * @param p2pRelease The {@link P2pRelease} to add a comment to.
     * @param text The comment. You may use BBCode to format the text. Can be {@code null} if both
     * videoRating and audioRating are set.
     * @param token The {@link Token} with all needed info
     *
     * @return The added {@link Comment}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6312/api-comments-add.html">API: comments/add
     * method</a>
     */
    public Comment postCommentsAdd(P2pRelease p2pRelease, String text, Token token) {
        Objects.requireNonNull(p2pRelease, MESSAGE_P2P_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        Objects.requireNonNull(text, MESSAGE_TEXT_MISSING);
        
        return postCommentsAdd(null, p2pRelease, text, null, null, token);
    }
    
    /**
     * Add a comment to a given {@link P2pRelease}. The text may contain BBCode.
     *
     * @param p2pRelease The {@link P2pRelease} to add a comment to.
     * @param text The comment. You may use BBCode to format the text. Can be {@code null} if both
     * videoRating and audioRating are set.
     * @param videoRating Video rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param audioRating Audio rating between 1 (bad) to 10 (good). You must always rate both or
     * none. You may only vote once, and may not change your vote. Check the vote property from
     * the response to get the rating as displayed on the website.
     * @param token The {@link Token} with all needed info
     *
     * @return The added {@link Comment}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6312/api-comments-add.html">API: comments/add
     * method</a>
     */
    public Comment postCommentsAdd(P2pRelease p2pRelease, String text, int videoRating, int audioRating, Token token) {
        Objects.requireNonNull(p2pRelease, MESSAGE_P2P_RELEASE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        Objects.requireNonNull(text, MESSAGE_TEXT_MISSING);
        
        if (videoRating < 1 || audioRating < 1 || videoRating > 10 || audioRating > 10) {
            throw new XrelException(EXCEPTION_MESSAGE_RATINGS_MUST_BE_BETWEEN_1_AND_10);
        }
        
        return postCommentsAdd(null, p2pRelease, text, videoRating, audioRating, token);
    }
    
    /**
     * Returns information about the active user.
     *
     * @param token The token used for authentication
     *
     * @return The {@link User}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href= "https://www.xrel.to/wiki/6441/api-user-info.html">API: user/info method</a>
     */
    public User getUserInfo(Token token) {
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        final Call<User> call = restClient.getXrelService().userInfo(token.createBearerHeader());
        final Response<User> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        return response.body();
    }
    
    /**
     * This is the endpoint for the first step of the OAuth 2.0 authentication flow. This basically
     * returns the URL you have to point the user to with all the information you provided while
     * constructing the {@link Xrel} object.
     *
     * @return The url you point the user to
     *
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public String getOauth2Auth() {
        if (getClientId().isEmpty()) {
            throw new XrelException("No Client ID provided");
        }
        return restClient.getOAuth2Auth(getResponseType(), getClientId().get(), getRedirectUri(), getState(), getScope());
    }
    
    /**
     * This is the endpoint for the last step of the OAuth 2.0 authentication flow.
     *
     * @param grantType {@code authorization_code} for User authentication, {@code client_credentials}
     * for Application authentication, {@code refresh_token} for refreshing an access token
     * @param code When performing the {@code authorization_code} grant, you must specify the code
     * provided from {@link #getOauth2Auth()}
     * @param token The {@link Token} with all needed info if performing {@code refresh_token} or
     * {@code null} otherwise
     *
     * @return The new {@link Token}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    private Token postOauth2TokenPrivate(String grantType, String code, Token token) {
        Objects.requireNonNull(grantType, MESSAGE_GRANT_TYPE_MISSING);
        final boolean grantsRefreshToken = Objects.equals(grantType, "refresh_token");
        if (!Objects.equals(grantType, "authorization_code") && !Objects.equals(grantType, "client_credentials") && !grantsRefreshToken) {
            throw new XrelException("Invalid grant_type");
        }
        String unsetParameters = "Needed parameters not set:";
        boolean error = false;
        if (getClientId().isEmpty()) {
            error = true;
            unsetParameters += " client_id";
        }
        if (getClientSecret().isEmpty()) {
            error = true;
            unsetParameters += " client_secret";
        }
        if (Objects.equals(grantType, "authorization_code") && code.isEmpty()) {
            error = true;
            unsetParameters += " code";
        }
        if (grantsRefreshToken && (token == null || token.getRefreshToken().isEmpty())) {
            error = true;
            unsetParameters += " refresh_token";
        }
        if (error) {
            throw new XrelException(unsetParameters);
        }
        String refreshToken = null;
        String redirectUri = null;
        String scope = null;
        if (grantsRefreshToken) {
            refreshToken = token.getRefreshToken();
        }
        if (!grantsRefreshToken && getRedirectUri().isPresent()) {
            redirectUri = getRedirectUri().get();
        }
        if (getScope().isPresent() && getScope().get().length > 0) {
            scope = String.join(" ", getScope().get());
        }
        final Call<Token> call = restClient.getXrelService()
                .oauth2Token(grantType, getClientId().get(), getClientSecret().get(), code, redirectUri, refreshToken, scope);
        final Response<Token> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new XrelException(e);
        }
        response.body(); //TODO Is this correct?
        return response.body();
    }
    
    /**
     * This is the endpoint for the last step of the OAuth 2.0 authentication flow.
     *
     * @param grantType {@code authorization_code} for User authentication, {@code client_credentials}
     * for Application authentication, {@code refresh_token} for refreshing an access token
     * @param token The {@link Token} with all needed info if performing {@code refresh_token}
     *
     * @return The new {@link Token}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Token postOauth2Token(String grantType, Token token) {
        Objects.requireNonNull(grantType, MESSAGE_GRANT_TYPE_MISSING);
        Objects.requireNonNull(token, MESSAGE_TOKEN_MISSING);
        
        return postOauth2TokenPrivate(grantType, null, token);
    }
    
    /**
     * This is the endpoint for the last step of the OAuth 2.0 authentication flow.
     *
     * @param grantType {@code authorization_code} for User authentication, {@code client_credentials}
     * for Application authentication, {@code refresh_token} for refreshing an access token
     * @param code When performing the {@code authorization_code} grant, you must specify the code
     * provided from {@link #getOauth2Auth()}
     *
     * @return The new {@link Token}
     *
     * @throws XrelException If there is an error returned by the xREL API
     * @see <a href="https://www.xrel.to/wiki/6436/api-oauth2.html">API: OAuth 2.0</a>
     */
    public Token postOauth2Token(String grantType, String code) {
        Objects.requireNonNull(grantType, MESSAGE_GRANT_TYPE_MISSING);
        Objects.requireNonNull(code, MESSAGE_CODE_MISSING);
        
        return postOauth2TokenPrivate(grantType, code, null);
    }
    
}
