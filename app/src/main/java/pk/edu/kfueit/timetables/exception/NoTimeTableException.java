package pk.edu.kfueit.timetables.exception;

import androidx.annotation.Nullable;

public class NoTimeTableException extends Exception{
    private String message;
    private String name;
    public NoTimeTableException(String name, String message){
        this.name = name;
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return "There was no TimeTable found for : "+name+message;
    }
}
