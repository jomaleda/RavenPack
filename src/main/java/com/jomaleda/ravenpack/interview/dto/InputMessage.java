package com.jomaleda.ravenpack.interview.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class InputMessage {
   @CsvBindByName(column = "user_id", required = true)
   private String userId;
   
   @CsvBindByName(column = "message", required = true)
   private String message;
}
