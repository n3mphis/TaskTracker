package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static List<String> pendingTasks = new ArrayList<>();
    private static List<String> inProgressTasks = new ArrayList<>();
    private static List<String> completedTasks = new ArrayList<>();
    private static int ID = 1;

    public static void main(String[] args) {
        int option = -1;
        while (option != 0) {
            System.out.println("""
                ----- WELCOME TO THE TASK TRACKER -----
                
                Choose your option:
                1) Add task
                2) Update task
                3) Delete task
                4) Mark task
                5) List tasks
                6) Save to JSON File
                
                0) Quit
                """);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    addTask(pendingTasks);
                    break;
                case 2:
                    updateTask();
                    break;
                case 3:
                    deleteTask();
                    break;
                case 4:
                    markTask();
                    break;
                case 5:
                    showTasks();
                    break;
                case 6:
                    saveTasksToJson();
                    break;
                case 0:
                    System.out.println("Exiting the program. Goodbye!");
                    break;
            }
        }
    }

    private static void addTask(List<String> taskList) {
        System.out.print("Write the task to be added: ");
        String task = scanner.nextLine();
        taskList.add(ID + ". " + task);
        System.out.println("Task added succesfully (ID: " + ID + ")");
        ID++;

        System.out.println();
    }

    private static void updateTask() {
        System.out.print("Select the ID task to update: ");
        int taskIdToUpdate;
        try {
            taskIdToUpdate = Integer.parseInt(scanner.nextLine());
            String taskIdPrefix = taskIdToUpdate + ". ";
            boolean found = false;

            found = updateTaskInList(pendingTasks, taskIdPrefix);
            if (!found) {
                found = updateTaskInList(inProgressTasks, taskIdPrefix);
            }
            if (!found) {
                found = updateTaskInList(completedTasks, taskIdPrefix);
            }
            if (!found) {
                System.out.println("Task with ID " + taskIdPrefix + " not found in any list.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number");
            return;
        }
        System.out.println();
    }

    private static boolean updateTaskInList(List<String> taskList, String taskIdPrefix) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).startsWith(taskIdPrefix)) {
                String currentTask = taskList.get(i).substring(taskIdPrefix.length());
                System.out.println("Enter the text to add to task " + taskIdPrefix.substring(0, taskIdPrefix.length() - 2) + ": ");
                String textToAdd = scanner.nextLine();

                String updatedTask = taskIdPrefix + currentTask + " " + textToAdd;

                taskList.set(i, updatedTask);
                System.out.println("Task with ID " + taskIdPrefix.substring(0, taskIdPrefix.length() - 2) + " updated succesfully.");
                return true;
            }
        }
        return false;
    }

    private static void deleteTask() {
        System.out.print("Enter the ID of the task you want to delete: ");
        int taskToBeDeleted;
        try {
            taskToBeDeleted = Integer.parseInt(scanner.nextLine());
            String taskToRemove = taskToBeDeleted + ". ";
            boolean found = false;

            if (removeTaskById(pendingTasks, taskToRemove)) {
                System.out.println("Task with ID " + taskToBeDeleted + " delete from Pending Tasks.");
                found = true;
            } else if (removeTaskById(inProgressTasks, taskToRemove)) {
                System.out.println("Task with ID " + taskToBeDeleted + " deleted from In-Progress Tasks.");
                found = true;
            } else if (removeTaskById(completedTasks, taskToRemove)) {
                System.out.println("Task with ID " + taskToBeDeleted + " deleted from the Completed Tasks.");
                found = true;
            } else {
                System.out.println("Task with ID " + taskToBeDeleted + " was not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
        System.out.println();
    }

    private static boolean removeTaskById(List<String> taskList, String taskId) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).startsWith(taskId)) {
                taskList.remove(i);
                return true;
            }
        }
        return false;
    }

    private static void markTask() {
        System.out.print("Enter the ID of the task to mark: ");
        int taskToBeMarked;

        try {
            taskToBeMarked = Integer.parseInt(scanner.nextLine());
            String taskToMove = null;
            List<String> originalList = null;
            String taskIdPrefix = taskToBeMarked + ". ";

            if ((taskToMove = findAndRemoveTask(pendingTasks, taskIdPrefix)) != null) {
                originalList = pendingTasks;
            } else if ((taskToMove = findAndRemoveTask(inProgressTasks, taskIdPrefix)) != null) {
                originalList = inProgressTasks;
            } else if ((taskToMove = findAndRemoveTask(completedTasks, taskIdPrefix)) != null) {
                originalList = completedTasks;
            }

            if (taskToMove != null) {
                System.out.println("Task found: " + taskToMove);
                System.out.print("Move to (P)ending, (I)n Progress, or (C)ompleted? ");
                String newStatus = scanner.nextLine().trim().toUpperCase();

                switch (newStatus) {
                    case "P":
                        pendingTasks.add(taskToMove);
                        System.out.println("Task moved to Pending.");
                        break;
                    case "I":
                        inProgressTasks.add(taskToMove);
                        System.out.println("Task moved to In-Progress.");
                        break;
                    case "C":
                        completedTasks.add(taskToMove);
                        System.out.println("Task moved to Completed.");
                        break;
                    default:
                        if (originalList != null) {
                            originalList.add(taskToMove);
                            System.out.println("Invalid status. Task remains in its original list");
                        }
                        break;
                }
            } else {
                System.out.println("Task with ID " + taskToBeMarked + " not found in any list.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }

    private static String findAndRemoveTask(List<String> taskList, String taskIdPrefix) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).startsWith(taskIdPrefix)) {
                return taskList.remove(i);
            }
        }
        return null;
    }

    private static void showTasks() {
        System.out.println("--- Pending Tasks ---");
        showTasks(pendingTasks);
        System.out.println("\n--- In Progress Tasks ---");
        showTasks(inProgressTasks);
        System.out.println("\n--- Completed Tasks ---");
        showTasks(completedTasks);
        System.out.println("-------------");
        System.out.println();
    }

    private static void showTasks(List<String> taskList) {
        if (taskList.isEmpty()) {
            System.out.println("No tasks in this list.");
        } else {
            for (String task : taskList) {
                System.out.println(task);
            }
        }
    }

    private static void saveTasksToJson() {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{\n");

        jsonBuilder.append(" \"pendingTasks\": [\n");
        appendTaskListToJson(jsonBuilder, pendingTasks);
        jsonBuilder.append(" ],\n");

        jsonBuilder.append(" \"inProgressTasks\": [\n");
        appendTaskListToJson(jsonBuilder, inProgressTasks);
        jsonBuilder.append("],\n");

        jsonBuilder.append("\"CompletedTasks\": [\n");
        appendTaskListToJson(jsonBuilder, completedTasks);
        jsonBuilder.append("],\n");

        jsonBuilder.append("}\n");

        try (FileWriter writer = new FileWriter("tasks.json")) {
            writer.write(jsonBuilder.toString());
            System.out.println("Tasks saved to tasks.json");
        } catch (IOException e) {
            System.out.println("Error saving tasks to file: " + e.getMessage());
        }
    }

    private static void appendTaskListToJson(StringBuilder sb, List<String> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            sb.append("  \"");
            sb.append(taskList.get(i));
            sb.append("\"");
            if (i < taskList.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
    }
}
