/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mfilatov.prayingtimes.telegrambot.model.PrayingTimes;

@FeignClient(value = "timeskeeperClient", url = "http://localhost:8080/")
public interface TimeskeeperClient {
  @GetMapping(path = "getTimesByCoordinates")
  PrayingTimes getTimesByCoordinates(
      @RequestParam(value = "latitude") Double latitude,
      @RequestParam(value = "longitude") Double longitude,
      @RequestParam(value = "method") Integer method);
}
