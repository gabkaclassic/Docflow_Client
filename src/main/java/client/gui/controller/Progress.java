package client.gui.controller;

import client.response.Response;
import javafx.concurrent.Task;

import java.util.concurrent.Callable;

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