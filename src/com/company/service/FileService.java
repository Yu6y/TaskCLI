package com.company.service;


import com.company.exception.TaskErrorException;
import com.company.model.Task;
import com.company.model.TaskStatus;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class FileService {
    private Path filePath = Paths.get("./data.json");
    private List<Task> tasksList;

    public FileService() throws TaskErrorException{
        tasksList = new ArrayList<>();
        checkFile();
    }

    private void checkFile() throws TaskErrorException{
        if(!Files.exists(filePath))
            try {
                Files.createFile(filePath);
            }catch (IOException e){
                System.out.println("Cannot create file." + e.getMessage());
            }
        else
            loadTasks();
    }

    private void loadTasks() throws TaskErrorException{
        try(InputStream in = Files.newInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                String line, json = "";
                int lineCounter = 0;
                while((line = reader.readLine()) != null){
                    if(line.contains("[") || line.contains("]")) continue;
                    json += line + '\n';
                    lineCounter++;
                    if(lineCounter == 7){
                        lineCounter = 0;
                        Optional<Task> task = JSONDeserialize(json.split("\n"));
                        if(task.isEmpty())
                            throw new TaskErrorException("Cannot read task from file.");
                        tasksList.add(task.get());
                        json = "";
                    }

                }
        }catch(IOException e){
            throw new TaskErrorException("Cannot open tasks file.");
        }
    }

    public String addTask(Task task) throws TaskErrorException{
        tasksList.add(task);
        saveTasks();

        return "Task added successfully (ID: " + task.getId() + ")";
    }

    private void saveTasks() throws TaskErrorException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.forName("US-ASCII"), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            StringBuilder out = new StringBuilder();
            out.append("[\n");
            for(int i = 0; i < tasksList.size(); i++) {
                out.append(JSONSerialize(tasksList.get(i)));
                if(i != tasksList.size() - 1)
                    out.append(',');
                out.append('\n');
            }
            out.append("]\n");
            writer.write(out.toString(), 0, out.length());
        } catch (IOException e) {
            throw new TaskErrorException("Cannot save data to file.");
        }
    }


    public int getAvailableId() throws TaskErrorException{
        Set<Integer> unavailableIds = new HashSet<>();

        for(Task task: tasksList)
            unavailableIds.add(task.getId());

        for(int i = 0; i != Integer.MAX_VALUE; i++){
            if(!unavailableIds.contains(i))
                return i;
        }

        throw new TaskErrorException("Tasks list is full.");
    }

    public List<Task> getTasksList(){
        return tasksList;
    }

    public Task getTask(int id) throws TaskErrorException{
        Optional<Task> task = tasksList.stream().filter(t -> t.getId() == id).findFirst();

        if(task.isEmpty())
            throw new TaskErrorException("Task cannot be found.");

        return task.get();
    }

    public String updateTask(Task task) throws TaskErrorException{
        int taskId = tasksList.indexOf(tasksList.stream().filter(t -> t.getId() == task.getId()).findFirst().get());

        tasksList.set(taskId, task);

        saveTasks();

        return "Task updated successfully.";
    }

    public String deleteTask(int id) throws TaskErrorException{
        getTask(id);
        tasksList = tasksList.stream().filter(t -> t.getId() != id).collect(Collectors.toList());

        saveTasks();

        return "Task deleted successfully.";
    }

    private Optional<Task> JSONDeserialize(String[] data){
        Task task = new Task();
        for(String line: data) {
            if(line.contains("{") || line.contains("}"))
                continue;
            else if (line.contains("\"id\":"))
                try {
                    task.setId(Integer.parseInt(getJsonVal(line, "\"id\": ")));
                } catch (NumberFormatException e) {
                    System.out.println("JSON file corrupted.");
                    return Optional.empty();
                }
            else if (line.contains("\"description\":"))
                task.setDescription(getJsonVal(line, "\"description\": \""));
            else if (line.contains("\"status\":"))
                task.setStatus(TaskStatus.getStatus(getJsonVal(line, "\"status\": \"")));
            else if (line.contains("\"createdAt\":"))
                try {
                    task.setCreatedAt(LocalDateTime.parse(getJsonVal(line, "\"createdAt\": \"")));
                }catch(DateTimeParseException e){
                    System.out.println("JSON file corrupted");
                }
            else if (line.contains("\"updatedAt\":"))
                try {
                    task.setUpdatedAt(LocalDateTime.parse(getJsonVal(line, "\"updatedAt\": \"")));
                }catch(DateTimeParseException e){
                    System.out.println("JSON file corrupted");
                }
            else
                return Optional.empty();
        }

        return Optional.of(task);
    }

    private String getJsonVal(String line, String param){
        return line.substring(line.lastIndexOf(param) + param.length(), line.length() - 1).replaceAll("\"", "");
    }

    private String JSONSerialize(Task task){
        return String.format("\t{\n" +
                        "\t\t\"id\": %d,\n" +
                        "\t\t\"description\": \"%s\",\n" +
                        "\t\t\"status\": \"%s\",\n" +
                        "\t\t\"createdAt\": \"%s\",\n" +
                        "\t\t\"updatedAt\": \"%s\"\n" +
                        "\t}",
                task.getId(), task.getDescription(), task.getStatus().getValue(), task.getCreatedAt(), task.getUpdatedAt());
    }

}
