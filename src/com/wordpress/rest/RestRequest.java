package com.wordpress.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.Map;
import java.util.HashMap;

import java.io.UnsupportedEncodingException;

public class RestRequest extends Request<JSONObject> {

    public static final String USER_AGENT_HEADER="User-Agent";
    public static final String REST_AUTHORIZATION_HEADER="Authorization";
    public static final String REST_AUTHORIZATION_FORMAT="Bearer %s";

    public interface Listener extends Response.Listener<JSONObject> {}
    public interface ErrorListener extends Response.ErrorListener {}
    
    private final Listener mListener;
    private final Map<String,String> mParams;
    private final Map<String,String> mHeaders = new HashMap<String,String>(2);
    public RestRequest(int method, String url, Map<String, String> params, Listener listener,
                       ErrorListener errorListener){
        super(method, url, errorListener);
        mParams = params;
        mListener = listener;
    }
    
    public void setAccessToken(String token){
        mHeaders.put(REST_AUTHORIZATION_HEADER, String.format(REST_AUTHORIZATION_FORMAT, token));
    }

    public void setUserAgent(String userAgent){
        mHeaders.put(USER_AGENT_HEADER, userAgent);
    }
    
    @Override
    public Map<String, String> getHeaders(){
        return mHeaders;
    }

    @Override
    protected void deliverResponse(JSONObject response){
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected Map<String, String> getParams(){
        return mParams;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

}