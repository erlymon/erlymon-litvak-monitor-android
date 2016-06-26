/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of TraccarLitvakM (fork Erlymon Monitor).
 *
 * TraccarLitvakM (fork Erlymon Monitor) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraccarLitvakM (fork Erlymon Monitor) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraccarLitvakM (fork Erlymon Monitor).  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.litvak.monitor

import android.support.multidex.MultiDexApplication
import android.text.format.DateFormat
import android.util.Log
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.android.LogcatAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.chibatching.kotpref.Kotpref
import com.facebook.stetho.Stetho
import org.erlymon.litvak.core.model.api.ApiModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/17/16.
 */
class MainApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        initLoggerSystem()
        Stetho.initializeWithDefaults(this);
        Kotpref.init(baseContext)
        ApiModule.getInstance().init(applicationContext, MainPref.dns, MainPref.sslOrTls)
    }

    private fun initLoggerSystem() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        lc.reset()

        // setup FileAppender
        val encoder1 = PatternLayoutEncoder()
        encoder1.context = lc
        encoder1.pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        encoder1.start()

        val fileAppender = FileAppender<ILoggingEvent>()
        fileAppender.context = lc
        try {
            val date = Date()
            val file = File(getExternalFilesDir(null), DateFormat.format("yyyy-MM-dd", date.time).toString() + ".log")
            fileAppender.file = file.path
        } catch (e: Exception) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.e("ExternalStorage", "Error writing ", e)

        }

        fileAppender.encoder = encoder1
        fileAppender.start()

        // setup LogcatAppender
        val encoder2 = PatternLayoutEncoder()
        encoder2.context = lc
        encoder2.pattern = "[%thread] %msg%n"
        encoder2.start()

        val logcatAppender = LogcatAppender()
        logcatAppender.context = lc
        logcatAppender.encoder = encoder2
        logcatAppender.start()

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        root.level = Level.toLevel("ALL")
        root.addAppender(fileAppender)
        root.addAppender(logcatAppender)
    }
}
