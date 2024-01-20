/*
 *  Copyright 2024 Cosgy Dev (info@cosgy.dev).
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  
 *   Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dev.cosgy.jmusicbot.util

/**
 * @author Kosugi_kun
 */
class Cache {
    @JvmField
    var title: String? = null
    var author: String? = null
    @JvmField
    var length: String? = null
    var identifier: String? = null
    var isStream: Boolean? = null
    @JvmField
    var url: String? = null
    @JvmField
    var userId: String? = null

    constructor(
        title: String?,
        author: String?,
        length: Long,
        identifier: String?,
        isStream: Boolean,
        uri: String?,
        userId: Long
    ) {
        this.title = title
        this.author = author
        this.length = length.toString()
        this.identifier = identifier
        this.isStream = isStream
        this.url = uri
        this.userId = userId.toString()
    }

    constructor()
}
