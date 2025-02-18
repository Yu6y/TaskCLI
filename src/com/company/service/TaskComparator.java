package com.company.service;

import com.company.model.Task;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task a, Task b){
        return a.getUpdatedAt().compareTo(b.getUpdatedAt());
    }
}
