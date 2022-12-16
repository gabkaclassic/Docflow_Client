package client.gui.controller;

import client.response.Response;
import javafx.concurrent.Task;

import java.util.concurrent.Callable;

/**
 * Класс для запуска feature (используется вместе с ProgreessIndicator)
 * @see Callable
 * @see javafx.scene.control.ProgressIndicator
 * */
class Progress <T extends Response> extends Task<T> {
    private T response;
    
    private final Callable<T> task;
    
    public Progress(Callable<T> task) {
    
        this.task = task;
    }
    public T call() throws Exception {
        
        response = task.call();
        
        return response;
    }
    
    public T get() {
        
        return response;
    }
}