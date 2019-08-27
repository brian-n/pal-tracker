package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private long idCounter = 0;
    HashMap<Long, TimeEntry> map;

    public InMemoryTimeEntryRepository() {
        map = new HashMap<Long, TimeEntry>();
    }

    public TimeEntry create(TimeEntry timeEntry) {
        TimeEntry mapTimeEntry = new TimeEntry(++idCounter, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        map.put(idCounter, mapTimeEntry);
        return mapTimeEntry;
    }

    public TimeEntry find(long id) {
        return map.get(id);
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry mapTimeEntry = find(id);

        if (mapTimeEntry == null) {
            return null;
        }

        mapTimeEntry.setProjectId(timeEntry.getProjectId());
        mapTimeEntry.setUserId(timeEntry.getUserId());
        mapTimeEntry.setDate(timeEntry.getDate());
        mapTimeEntry.setHours(timeEntry.getHours());
        return mapTimeEntry;
    }

    public void delete(long id) {
        map.remove(id);
    }

    public List<TimeEntry> list() {
        return map.values().stream().collect(Collectors.toList());
    }
}
