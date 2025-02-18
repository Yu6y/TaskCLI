package com.company.service;


import com.company.exception.TaskErrorException;
import com.company.model.Task;
import com.company.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private FileService fileService;

    public TaskService(){
        try {
            this.fileService = new FileService();
        }catch(TaskErrorException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void addTask(String task){
        Task newTask = new Task();
        newTask.setDescription(task);

        try {
            newTask.setId(fileService.getAvailableId());
            String response;
            response = fileService.addTask(newTask);
            System.out.println(response);
        }catch(TaskErrorException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void listTasks(){
        List<Task> list;

        list = fileService.getTasksList();
        list.sort(new TaskComparator());
        Collections.reverse(list);

        System.out.println("List of all TODO's:");
        System.out.println("-------------------------");
        if(list.isEmpty())
            System.out.println("List is empty. Add task.");
        for(Task task: list){
            System.out.println(task.toString());
        }
    }

    public void updateTask(int id, String description){
        try {
            Task task;

            task = fileService.getTask(id);
            task.setDescription(description);
            task.setUpdatedAt(LocalDateTime.now());

            System.out.println(fileService.updateTask(task));
        }catch(TaskErrorException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void deleteTask(int id){
        try{
            System.out.println(fileService.deleteTask(id));
        }catch(TaskErrorException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void markInProgress(int id){
        try{
            Task task;

            task = fileService.getTask(id);
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setUpdatedAt(LocalDateTime.now());

            System.out.println(fileService.updateTask(task));
        }catch(TaskErrorException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void markDone(int id){
        try{
            Task task;

            task = fileService.getTask(id);
            task.setStatus(TaskStatus.DONE);
            task.setUpdatedAt(LocalDateTime.now());

            System.out.println(fileService.updateTask(task));
        }catch(TaskErrorException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void listTasks(String filter){
        List<Task> list;

        list = fileService.getTasksList().stream().filter(t -> t.getStatus().getValue().equals(filter)).collect(Collectors.toList());
        list.sort(new TaskComparator());
        Collections.reverse(list);

        System.out.println("List of " + filter +" TODO's:");
        System.out.println("-------------------------");
        if(list.isEmpty())
            System.out.println("List is empty. Add task.");
        for(Task task: list){
            System.out.println(task.toString());
        }
    }
}
