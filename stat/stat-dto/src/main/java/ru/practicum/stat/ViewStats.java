package ru.practicum.stat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ViewStats {
    private  String app;

    private String uri;

    private Long hits;
}
