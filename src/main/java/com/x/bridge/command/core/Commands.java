package com.x.bridge.command.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc TODO
 * @Date 2020/10/24 12:58
 * @Author AD
 */
public final class Commands {
    
    private static volatile boolean inited = false;
    
    private static Map<Integer, ICommand> commands;
    
    public static ICommand getCommand(int command) {
        if (!inited) {
            init();
        }
        return commands.get(command);
    }
    
    private static void init() {
        synchronized (Commands.class) {
            if (!inited) {
                commands = new ConcurrentHashMap<>();
                for (Command cmd : Command.values()) {
                    commands.put(cmd.getCmd(), cmd.getActor());
                }
                inited = true;
            }
        }
    }
    
}
