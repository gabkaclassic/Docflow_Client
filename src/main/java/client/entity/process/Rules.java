package client.entity.process;

import lombok.Getter;

/**
 * Права участника для работы с документом
 * READ - только просмотр документов, возможность добавлять ресурсы и комментарии
 * CHANGE - изменение документов, добавление новых, а также все возможности READ
 * CONTROL - переход на новый шаг, возврат на предыдущий, завершение процесса, а также все возможности CHANGE
 * @see client.entity.process.step.Step
 * */
@Getter
public enum Rules {
    
    CHANGE("Edit and saving documents", 2),
    READ("Reading and check documents", 1),
    CONTROL("Edit and saving documents, confirmation of the transition to the next step", 3);
    
    
    private final String view;
    private final int level;
    
    Rules(String view, int level) {
    
        this.view = view;
        this.level = level;
    }
}
