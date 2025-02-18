/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot.model;

public record PrayingTimes(
    String date,
    String timezone,
    String method,
    String imsak,
    String fajr,
    String sunrise,
    String dhuhr,
    String asr,
    String sunset,
    String maghrib,
    String isha,
    String midnight) {}
