package pk.edu.kfueit.timetables.exception;

import androidx.annotation.Nullable;

public class LoginException extends Exception{
    private String message;
    public LoginException(String message){
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }
}
