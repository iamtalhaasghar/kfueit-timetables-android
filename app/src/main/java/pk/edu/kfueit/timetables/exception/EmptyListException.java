package pk.edu.kfueit.timetables.exception;

import androidx.annotation.Nullable;

public class EmptyListException extends  Exception{

    private String message;
    public EmptyListException(String message){
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return "No List item was found for : "+message;
    }
}
