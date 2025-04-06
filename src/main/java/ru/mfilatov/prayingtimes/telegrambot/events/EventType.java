/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot.events;

public enum EventType {
  NEW_USER,
  CHANGE_LOCATION,
  TIMES_REQUEST,
  HELP,
  START,
  ERROR,
  UNKNOWN_COMMAND
}
