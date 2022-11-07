package client.gui.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.AllArgsConstructor;

import java.util.concurrent.Callable;

@AllArgsConstructor
class Progress <T> extends Service<T> {
    
    private Callable<T> run;
    
    protected Task<T> createTask() {
        
        return new Task<>() {
            
            protected T call() throws Exception {
                
                return run.call();
            }
        };
    }
}
