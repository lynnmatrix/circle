package com.jadenine.circle.model.state;

import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.db.CircleDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import rx.android.internal.Preconditions;

/**
 * Created by linym on 7/15/15.
 */
@Table(databaseName = CircleDatabase.NAME, allFields = true)
public class TimelineRangeCursor extends BaseModel{
    @PrimaryKey(autoincrement = true)
    @Column(length = -1) //issue https://github.com/Raizlabs/DBFlow/pull/305/commits
    long id;

    @NotNull
    String timeline;

    @NotNull
    Long top;

    @NotNull
    Long bottom;

    boolean hasMore = true;

    TimelineRangeCursor(){}
    public TimelineRangeCursor(String timeline) {
        this.timeline = timeline;
    }

    public TimelineRangeCursor(String timeline, List<? extends Identifiable<Long>> list) {
        this.timeline = timeline;
        if (list.size() > 0) {
            setTop(list.get(0).getId());
            setBottom(list.get(list.size() - 1).getId());
        }
    }

    public Long getTop() {
        return top;
    }

    public Long getBottom() {
        return bottom;
    }

    public boolean getHasMore(){
        return hasMore;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTop(Long top) {
        Preconditions.checkArgument(null == top || null == bottom || top <= bottom,
                "invalid top:" +top + ", current bottom is" + bottom);
        this.top = top;
        if (null == bottom) {
            bottom = top;
        }
    }

    public void setBottom(Long bottom) {
        Preconditions.checkArgument(null == top || null == bottom || top <= bottom,
                "invalid bottom:" + bottom + ", current top is " + top);
        this.bottom = bottom;
        if(null == top) {
            top = bottom;
        }
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public void contact(TimelineRangeCursor cursor) {
        setBottom(cursor.getBottom());
        setHasMore(cursor.hasMore);
    }
}
