package com.mngl.utils;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {

    private int greencolor, redcolor;
    private HashSet<CalendarDay> dates;
    private HashSet<CalendarDay> indates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.greencolor = color;
        this.dates = new HashSet<>(dates);
    }

    public EventDecorator(int green, int red,ArrayList<CalendarDay> dates , ArrayList<CalendarDay> indates) {
        greencolor = green;
        this.dates = new HashSet<>(dates);
        redcolor = red;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new DotSpan(5, greencolor));
    }
}
