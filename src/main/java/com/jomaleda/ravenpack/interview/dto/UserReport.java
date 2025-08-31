package com.jomaleda.ravenpack.interview.dto;

import com.opencsv.bean.CsvBindByName;

public record UserReport(
   @CsvBindByName(column = "user_id")
   String userId,
   @CsvBindByName(column = "total_messages")
   int totalMessages,
   @CsvBindByName(column = "avg_score")
   float avgScore
) {}
