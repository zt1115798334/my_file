package com.example.myFile.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang
 * date: 2019/7/11 14:18
 * description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RangeSettings {
    private long start;
    private long end;
    private long contentLength;
    private long totalLength;
    private boolean range;

    public RangeSettings(long totalLength) {
        this.totalLength = totalLength;
    }

    public RangeSettings(long start, long end, long contentLength, long totalLength) {
        this.start = start;
        this.end = end;
        this.contentLength = contentLength;
        this.totalLength = totalLength;
        this.range = true;
    }

}
