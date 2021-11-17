// Copyright 2021 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.tapestry5.StreamResponse;

/**
 * An event handler method may return an instance of this class to send an specific HTTP status
 * code to the client. It also supports providing a string to be used as the response body
 * and extra HTTP headers to be set.
 * 
 * For returning binary content and/or adding a response header more than once and/or
 * adding a response header without overwriting existing ones, implement a {@link StreamResponse} 
 * instead.
 *
 * @since 5.8.0
 */
public final class HttpStatus
{
    private static final String CONTENT_LOCATION_HTTP_HEADER = "Content-Location";

    private final int statusCode;

    String message;
    
    String contentType;
    
    Map<String, String> extraHttpHeaders;
    
    /**
     * Creates an object with a given status code and no message.
     */
    public HttpStatus(int statusCode)
    {
        this(statusCode, null, null);
    }
    
    /**
     * Creates an object with a given status code, message and <code>text/plain</code> MIME content type.
     */
    public HttpStatus(int statusCode, String message)
    {
        this(statusCode, message, "text/plain");
    }

    /**
     * Creates an object with a given status code, message and MIME content type.
     */
    public HttpStatus(int statusCode, String message, String contentType)
    {
        this.statusCode = statusCode;
        this.message = message;
        this.contentType = contentType;
    }

    /**
     * Returns the status code.
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Returns the message.
     */
    public String getMessage()
    {
        return message;
    }
    
    /**
     * Returns the extra HTTP headers.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getExtraHttpHeaders() {
        return extraHttpHeaders != null ? extraHttpHeaders : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns the MIME content type of the message.
     */
    public String getContentType() 
    {
        return contentType;
    }
    
    /**
     * Sets the <code>Content-Location</code> HTTP header.
     */
    public HttpStatus withContentLocation(String location)
    {
        return withHttpHeader(CONTENT_LOCATION_HTTP_HEADER, location);
    }
    
    /**
     * Sets an HTTP header. If an existing value for this header already exists,
     * it gets overwritten. If you need to set multiple headers or add them without
     * overwriting existing ones, you need to implement {@link StreamResponse} instead.
     */
    public HttpStatus withHttpHeader(String name, String value)
    {
        Objects.requireNonNull(name, "Parameter name cannot be null");
        Objects.requireNonNull(value, "Parameter value cannot be null");
        if (extraHttpHeaders == null)
        {
            extraHttpHeaders = new HashMap<>(3);
        }
        extraHttpHeaders.put(name, value);
        return this;
    }

}
