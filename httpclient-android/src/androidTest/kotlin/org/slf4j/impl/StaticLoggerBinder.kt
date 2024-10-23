/*
 * Copyright 2022, OK2 Consulting Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slf4j.impl

import com.ok2c.hc.android.logging.AndroidLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

class StaticLoggerBinder: LoggerFactoryBinder {

    companion object {
        private val INSTANCE = StaticLoggerBinder()

        @JvmStatic
        fun getSingleton(): StaticLoggerBinder? {
            return INSTANCE
        }
    }

    override fun getLoggerFactory(): ILoggerFactory? {
        return AndroidLoggerFactory.INSTANCE
    }

    override fun getLoggerFactoryClassStr(): String? {
        return AndroidLoggerFactory::class.java.name
    }

}