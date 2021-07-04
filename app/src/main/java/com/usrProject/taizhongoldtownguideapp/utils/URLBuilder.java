package com.usrProject.taizhongoldtownguideapp.utils;

import android.content.Context;

import com.usrProject.taizhongoldtownguideapp.R;

import org.apache.commons.lang3.StringUtils;

public class URLBuilder {
    private static final String AUTHORIZATION = "Authorization";
    private static final String ELEMENT_NAME = "elementName";
    private static final String PARAMETER_NAME = "parameterName";
    private static final String LOCATION_NAME = "locationName";

    public String getOpenDataUrl(Context context, String Authorization, String elementName, String parameterName, String locationName){
        String base = context.getString(R.string.opendata_base_url);
        boolean hasParameter = false;
        if(StringUtils.isNotBlank(Authorization)){
            base = getPathParameterPrefix(base, hasParameter,AUTHORIZATION, Authorization);
            hasParameter = true;
        }
        if(StringUtils.isNotBlank(elementName)){
            base = getPathParameterPrefix(base, hasParameter, ELEMENT_NAME, elementName);
            hasParameter = true;
        }
        if(StringUtils.isNotBlank(parameterName)){
            base = getPathParameterPrefix(base, hasParameter, PARAMETER_NAME, parameterName);
            hasParameter = true;
        }
        if(StringUtils.isNotBlank(locationName)){
            base = getPathParameterPrefix(base, hasParameter, LOCATION_NAME, locationName);
            hasParameter = true;
        }

        return base;
    }

    private String getPathParameterPrefix(String base, boolean hasParameter,String field, String value){
        if(hasParameter){
            base += "&" + field + "=" + value;
            return base;
        }
        base += "?" + field + "=" + value;
        return base;
    }
}
