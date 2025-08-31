package com.jomaleda.ravenpack.interview.dto;

import com.opencsv.bean.CsvBindByName;

public record InputMessage(
   @CsvBindByName(column = "user_id", required = true)
   String userId,
   @CsvBindByName(column = "message", required = true)
   String message
) {}
