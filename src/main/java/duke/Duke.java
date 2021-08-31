package duke;

import java.io.IOException;

public class Duke {
    private Storage storage;
    private TaskList taskList;
    private Ui ui;
    private Boolean isRunning;
    private Parser parser;

    /**
     * Constructor for Duke program.
     *
     * @throws IOException
     */
    public Duke() {
        this.ui = new Ui();
        this.storage = new Storage("data/dukeTaskList.txt");
        this.taskList = new TaskList();
        this.isRunning = false;
        this.parser = new Parser();
        if (storage.getDoesFileExists()) {
            this.storage.retrieveFile(this.taskList);
        } else {
            this.storage.createFile();
        }
    }

    //String getResponse(String input) {
    //    return "Duke: " + input;
    //}

    String getResponse(String x) {
        String input = x;
        String response;
        if (input.equals("list")) {
            //List task list
            response = this.ui.listTaskList(this.taskList);
        } else if (input.contains("done ")) {
            //Set task as done
            Integer listIndex = parser.doneInputParser(input);
            this.taskList.setTaskDone(listIndex);
            response = this.ui.doneTaskMsg(this.taskList.getTask(listIndex));
        } else if (input.contains("delete ")) {
            //Deletes task
            Integer removeTaskIndex = parser.deleteInputParser(input);
            Task removedTask = taskList.removeTask(removeTaskIndex);
            response = this.ui.deleteTaskMsg(removedTask, this.taskList.getNoOfTask());
        } else if (input.contains("find ")) {
            //Find tasks
            String keyword = parser.findInputParser(input);
            TaskList taskListWithKeyword = this.taskList.findTasks(keyword);
            response = this.ui.findTaskMsg(taskListWithKeyword);
        } else {
            try {
                //Initialise the task if its a valid input.
                Task newTask = null;
                if (input.contains("todo")) {
                    newTask = new Todo(parser.toDoInputParser(input));
                } else if (input.contains("deadline")) {
                    if (!input.contains("/by")) {
                        //for missing dateline
                        throw new MissingDateException();
                    } else {
                        newTask = new Deadline(parser.deadlineInputTaskParser(input),
                                parser.deadlineInputDateParser(input));
                    }
                } else if (input.contains("event")) {
                    if (!input.contains("/at")) {
                        //for missing dateline
                        throw new MissingDateException();
                    } else {
                        newTask = new Event(parser.eventInputTaskParser(input),
                                parser.eventInputDateParser(input));
                    }
                }
                if (newTask != null) {
                    //Add task to the list and print message.
                    taskList.addTask(newTask);
                    this.storage.saveFile(this.taskList);
                    response = this.ui.addTaskMsg(newTask, this.taskList.getNoOfTask());
                } else {
                    //For invalid input message
                    throw new WrongInputException();
                }

            } catch (DukeException e) {
                return (e.toString()
                        + "\n");
            }
        }
        return response;
    }
}



