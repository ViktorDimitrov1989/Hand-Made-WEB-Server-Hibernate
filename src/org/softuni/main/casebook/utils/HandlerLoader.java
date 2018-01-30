package org.softuni.main.casebook.utils;

import org.softuni.main.casebook.annotations.Get;
import org.softuni.main.casebook.annotations.Post;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class HandlerLoader {

    private static final String DYNAMIC_HANDLERS_FULL_PATH = System.getProperty("user.dir") + "\\src\\org\\softuni\\main\\casebook\\handlers\\dynamic";
    private static final String DYNAMIC_HANDLERS_PACKAGE = "org.softuni.main.casebook.handlers.dynamic";

    private HashMap<String, HashMap<String, Method>> actionsMap;

    public HandlerLoader(){
        this.actionsMap = new HashMap<>();
        this.initializeSupportedMethods();
        this.loadMaps();
    }

    private void initializeSupportedMethods() {
        this.actionsMap.put("GET", new HashMap<>());
        this.actionsMap.put("POST", new HashMap<>());
    }

    private void loadMaps(){
        File directory = new File(DYNAMIC_HANDLERS_FULL_PATH);

        List<Class<?>> handlers = Arrays.asList(directory.listFiles())
                .stream().map(x -> {
                    Class<?> res = null;
                    try {
                        String fileFullName = String.valueOf(x);
                        String className = fileFullName.substring(fileFullName.lastIndexOf("\\") + 1).replace(".java", "");
                        res = Class.forName(DYNAMIC_HANDLERS_PACKAGE + "." + className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return res;
                })
                .collect(Collectors.toList());

        for (Class loadedClass : handlers) {
            for (Method m : loadedClass.getDeclaredMethods()) {
                m.setAccessible(true);

                if(m.isAnnotationPresent(Get.class)){
                    this.actionsMap.get("GET").put(m.getAnnotation(Get.class).route(), m);
                }else if(m.isAnnotationPresent(Post.class)){
                    this.actionsMap.get("POST").put(m.getAnnotation(Post.class).route(), m);
                }
            }
        }
    }

    public Map<String, Method> retrieveActionsMap(String method){

        if(!this.actionsMap.containsKey(method)){
            return null;
        }

        return Collections.unmodifiableMap(this.actionsMap.get(method));
    }


}
