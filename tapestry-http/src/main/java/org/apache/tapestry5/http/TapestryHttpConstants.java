// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.apache.tapestry5.http;

/**
 * Class defining constants for Tapestry HTTP.
 */
final public class TapestryHttpConstants {

    /**
     * Request attribute key; if non-null, then automatic GZIP compression of response stream is
     * suppressed. This is
     * useful when the code opening the response stream wants to explicitly control whether GZIP
     * compression occurs or
     * not.
     *
     * @since 5.1.0.0
     */
    public static final String SUPPRESS_COMPRESSION = "tapestry.supress-compression";
    
    /**
     * The version number of the core Tapestry framework, or UNKNOWN if the version number is not available (which
     * should only occur when developing Tapestry).
     */
    public static final String TAPESTRY_VERSION = "tapestry.version";

}
