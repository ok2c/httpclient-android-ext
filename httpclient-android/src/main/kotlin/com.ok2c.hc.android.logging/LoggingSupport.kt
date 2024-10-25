package com.ok2c.hc.android.logging

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.helpers.MarkerIgnoringBase
import org.slf4j.helpers.MessageFormatter

class AndroidLoggerFactory : ILoggerFactory {

    private val loggerMap: ConcurrentMap<String, Logger> = ConcurrentHashMap()

    private fun loggerNameToTag(loggerName: String): String {
        val length = loggerName.length
        if (length <= 23) {
            return loggerName
        }
        val lastPeriod = loggerName.lastIndexOf(".")
        return if (length - (lastPeriod + 1) <= 23)
            loggerName.substring(lastPeriod + 1)
        else
            loggerName.substring(loggerName.length - 23)
    }

    override fun getLogger(name: String): Logger {
        val tag: String = when {
            name == "org.apache.hc.client5.http.wire" -> "HttpClientWire"
            name == "org.apache.hc.client5.http.headers" -> "HttpClientHeader"
            name.startsWith("org.apache.hc.") -> "HttpClient"
            else -> loggerNameToTag(name)
        }
        var logger = loggerMap[tag]
        if (logger == null) {
            val newLogger: Logger = AndroidLogger(tag)
            logger = loggerMap.putIfAbsent(name, newLogger)
            if (logger == null) {
                logger = newLogger
            }
        }
        return logger
    }

    companion object {
        val INSTANCE = AndroidLoggerFactory()
    }

}

internal class AndroidLogger(name: String) : MarkerIgnoringBase() {

    init {
        this.name = name
    }

    fun isLoggable(priority: Int): Boolean {
        return Log.isLoggable(name, priority)
    }

    private fun log(priority: Int, message: String, throwable: Throwable?) {
        Log.println(
            priority,
            name,
            if (throwable != null) "$message\n${Log.getStackTraceString(throwable)}" else message
        )
    }

    fun logFormatted(priority: Int, format: String, vararg argArray: Any?) {
        if (isLoggable(priority)) {
            val ft = MessageFormatter.arrayFormat(format, argArray)
            log(priority, ft.message, ft.throwable)
        }
    }

    fun logMessage(priority: Int, message: String, throwable: Throwable?) {
        if (isLoggable(priority)) {
            log(priority, message, throwable)
        }
    }

    override fun isTraceEnabled(): Boolean {
        return isLoggable(Log.VERBOSE)
    }

    override fun trace(msg: String) {
        logMessage(Log.VERBOSE, msg, null)
    }

    override fun trace(format: String, arg: Any) {
        logFormatted(Log.VERBOSE, format, arg)
    }

    override fun trace(format: String, arg1: Any, arg2: Any?) {
        logFormatted(Log.VERBOSE, format, arg1, arg2)
    }

    override fun trace(format: String, vararg arguments: Any) {
        logFormatted(Log.VERBOSE, format, *arguments)
    }

    override fun trace(msg: String, t: Throwable) {
        logMessage(Log.VERBOSE, msg, t)
    }

    override fun isDebugEnabled(): Boolean {
        return isLoggable(Log.DEBUG)
    }

    override fun debug(msg: String) {
        logMessage(Log.DEBUG, msg, null)
    }

    override fun debug(format: String, arg: Any) {
        logFormatted(Log.DEBUG, format, arg)
    }

    override fun debug(format: String, arg1: Any, arg2: Any?) {
        logFormatted(Log.DEBUG, format, arg1, arg2)
    }

    override fun debug(format: String, vararg arguments: Any) {
        logFormatted(Log.DEBUG, format, *arguments)
    }

    override fun debug(msg: String, t: Throwable) {
        logMessage(Log.DEBUG, msg, t)
    }

    override fun isInfoEnabled(): Boolean {
        return isLoggable(Log.INFO)
    }

    override fun info(msg: String) {
        logMessage(Log.INFO, msg, null)
    }

    override fun info(format: String, arg: Any) {
        logFormatted(Log.INFO, format, arg)
    }

    override fun info(format: String, arg1: Any, arg2: Any?) {
        logFormatted(Log.INFO, format, arg1, arg2)
    }

    override fun info(format: String, vararg arguments: Any) {
        logFormatted(Log.INFO, format, *arguments)
    }

    override fun info(msg: String, t: Throwable) {
        logMessage(Log.INFO, msg, t)
    }

    override fun isWarnEnabled(): Boolean {
        return isLoggable(Log.WARN)
    }

    override fun warn(msg: String) {
        logMessage(Log.WARN, msg, null)
    }

    override fun warn(format: String, arg: Any) {
        logFormatted(Log.WARN, format, arg)
    }

    override fun warn(format: String, arg1: Any, arg2: Any?) {
        logFormatted(Log.WARN, format, arg1, arg2)
    }

    override fun warn(format: String, vararg arguments: Any) {
        logFormatted(Log.WARN, format, *arguments)
    }

    override fun warn(msg: String, t: Throwable) {
        logMessage(Log.WARN, msg, t)
    }

    override fun isErrorEnabled(): Boolean {
        return isLoggable(Log.ERROR)
    }

    override fun error(msg: String) {
        logMessage(Log.ERROR, msg, null)
    }

    override fun error(format: String, arg: Any) {
        logFormatted(Log.ERROR, format, arg)
    }

    override fun error(format: String, arg1: Any, arg2: Any?) {
        logFormatted(Log.ERROR, format, arg1, arg2)
    }

    override fun error(format: String, vararg arguments: Any) {
        logFormatted(Log.ERROR, format, *arguments)
    }

    override fun error(msg: String, t: Throwable) {
        logMessage(Log.ERROR, msg, t)
    }

}