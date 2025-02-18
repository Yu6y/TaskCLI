package com.company.controller;

import com.company.service.TaskService;

import java.util.Arrays;

public class CLIHandler {
    private TaskService taskService;

    public CLIHandler(){
        this.taskService = new TaskService();
    }

    public void handleCommandLine(String[] line){
        String command = line[0];
        String[] params = Arrays.copyOfRange(line, 1, line.length);
        switch(command){
            case "add":
                if(params.length != 1)
                    printError();
                else
                    if(!checkParams(params[0]))
                        taskService.addTask(params[0]);
                    else
                        printParamsError();
                break;
            case "list":
                if(params.length > 1)
                    printError();
                else if(params.length == 1)
                    if(params[0].equals("done") || params[0].equals("todo") || params[0].equals("in-progress"))
                        taskService.listTasks(params[0]);
                    else
                        printParamsError();
                else
                    taskService.listTasks();
                break;
            case "update":
                if(params.length != 2)
                    printError();
                else
                    if(!checkParams(params[1]))
                        taskService.updateTask(getId(params[0]), params[1]);
                    else
                        printParamsError();
                break;
            case "delete":
                if(params.length != 1)
                    printError();
                else
                    taskService.deleteTask(getId(params[0]));
                break;
            case "mark-in-progress":
                if(params.length != 1)
                    printError();
                else
                    taskService.markInProgress(getId(params[0]));
                break;
            case "mark-done":
                if(params.length != 1)
                    printError();
                else
                    taskService.markDone(getId(params[0]));
                break;
            case "help":
                printHelper();
                break;
            default:
                printError();
        }
    }

    public void printHelper(){
        System.out.println("Application to store tasks\n\n" +
                "TaskCli command [params]\n\n" +
                "add \"name of task\" - to add new task\n" +
                "update id \"new name\" - to updated task with given ID\n" +
                "delete id - to delete task with given ID\n" +
                "list - to list all tasks\n" +
                "list done - to list done tasks\n" +
                "list todo - to list todo tasks\n" +
                "list in-progress - to list in-progress tasks.\n");
    }

    private void printError(){
        System.out.println("Command not found. Use 'help' parameter to list available params.");
    }

    private void printParamsError(){
        System.out.println("Invalid arguments.");
    }

    private boolean checkParams(String param){
        return param.trim().isEmpty();
    }

    private int getId(String param){
        try{
           return Integer.parseInt(param);
        }catch(NumberFormatException e){
            printParamsError();
            System.exit(-1);
            return -1;
        }
    }
}
