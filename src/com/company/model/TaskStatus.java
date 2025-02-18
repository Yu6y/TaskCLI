package com.company.model;

public enum TaskStatus {
    TODO ("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private final String out;

    TaskStatus(String status) {
        this.out = status;
    }

    public String getValue() {
        return out;
    }

    public static TaskStatus getStatus(String in){
        for(TaskStatus status: TaskStatus.values()) {
            if(status.getValue().equals(in))
                return status;
        }
        throw new IllegalArgumentException("Unknown status.");
    }
}
