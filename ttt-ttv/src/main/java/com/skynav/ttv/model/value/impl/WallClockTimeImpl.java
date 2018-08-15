/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY SKYNAV, INC. AND ITS CONTRIBUTORS “AS IS” AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SKYNAV, INC. OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.skynav.ttv.model.value.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.skynav.ttv.model.value.WallClockTime;
import com.skynav.ttv.model.value.TimeBase;
import com.skynav.ttv.model.value.TimeParameters;

public class WallClockTimeImpl implements WallClockTime {
    public static final WallClockTime ZERO = new WallClockTimeImpl(0, 0, 0, 0, 0, 0);
    private int years;
    private int months;
    private int days;
    private int hours;
    private int minutes;
    private double seconds;
    BigDecimal jd;
    public WallClockTimeImpl(int years, int months, int days, int hours, int minutes, double seconds) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }
    public WallClockTimeImpl(String years, String months, String days, String hours, String minutes, String seconds) {
        try {
            int yy;
            if (years != null)
                yy = Integer.parseInt(years);
            else
                yy = 0;
            int mo;
            if (months != null)
                mo = Integer.parseInt(months);
            else
                mo = 0;
            int dd;
            if (days != null)
                dd = Integer.parseInt(days);
            else
                dd = 0;
            int hh;
            if (hours != null)
                hh = Integer.parseInt(hours);
            else
                hh = 0;
            int mm;
            if (minutes != null)
                mm = Integer.parseInt(minutes);
            else
                mm = 0;
            double ss;
            if (seconds != null)
                ss = Double.parseDouble(seconds);
            else
                ss = 0;
            this.years = yy;
            this.months = mo;
            this.days = dd;
            this.hours = hh;
            this.minutes = mm;
            this.seconds = ss;
        } catch (NumberFormatException e) {
        }
    }
    public Type getType() {
        return Type.WallClock;
    }
    public double getTime(TimeParameters parameters) {
        assert parameters != null;
        double t = 0;
        t += (double) getHours() * 3600;
        t += (double) getMinutes() * 60;
        t += (double) getSeconds() *  1;
        return t;
    }
    public int getYears() {
        return years;
    }
    public int getMonths() {
        return months;
    }
    public int getDays() {
        return days;
    }
    public int getHours() {
        return hours;
    }
    public int getMinutes() {
        return minutes;
    }
    public double getSeconds() {
        return seconds;
    }
    public BigDecimal getJulianDate() {
        if (jd == null)
            jd = computeJulianDate();
        return jd;
    }
    private BigDecimal computeJulianDate() {
        BigInteger jd = computeJulianDayNumber();
        double H = (double) getHours();
        double M = (double) getHours();
        double S = getSeconds();
        double J = (H - 12) / 24 + (M / 1440) + (S / 86400);
        return new BigDecimal(jd).add(new BigDecimal(J));
    }
    private BigInteger computeJulianDayNumber() {
        long Y = getYears();
        long M = getMonths();
        long D = getDays();
        long J = (1461 * (Y + 4800 + (M - 14)/12))/4 +(367 * (M - 2 - 12 * ((M - 14)/12)))/12 - (3 * ((Y + 4900 + (M - 14)/12)/100))/4 + D - 32075;
        return new BigInteger(Long.toString(J));
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(Integer.toString(getYears()));
        sb.append(',');
        sb.append(Integer.toString(getMonths()));
        sb.append(',');
        sb.append(Integer.toString(getDays()));
        sb.append(',');
        sb.append(Integer.toString(getHours()));
        sb.append(',');
        sb.append(Integer.toString(getMinutes()));
        sb.append(',');
        sb.append(Double.toString(getSeconds()));
        sb.append(']');
        return sb.toString();
    }
    /**
     * Obtain current wall clock time in UTC time zone
     * @return current wall clock time in UTC time zone
     */
    public static WallClockTime utc() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        int years = now.getYear();
        int months = now.getMonthValue();
        int days = now.getDayOfMonth();
        int hours = now.getHour();
        int minutes = now.getMinute();
        double seconds = (double) now.getSecond() + ((double) now.getNano()) / 1e9;
        return new WallClockTimeImpl(years, months, days, hours, minutes, seconds);
    }
    /**
     * Obtain offset from default time zone to UTC time zone in seconds (may be negative).
     * @return seconds offset from default time zone to UTC time zone
     */
    public static int utcOffset() {
        return ZonedDateTime.now().getOffset().getTotalSeconds();
    }        
}

