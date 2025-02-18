package com.company;

import com.company.controller.CLIHandler;

public class TaskCli {

    public static void main(String[] args) {

        if(args.length < 1)
            System.out.println("Command not found. Use 'help' parameter to list available params.");
        else
            new CLIHandler().handleCommandLine(args);
    }
}
