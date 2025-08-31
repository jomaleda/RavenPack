package com.jomaleda.ravenpack.interview.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReport {
   @CsvBindByName(column = "user_id")
   private String userId;
   @CsvBindByName(column = "total_messages")
   private int totalMessages;
   @CsvBindByName(column = "avg_score")
   private float avgScore;
}
