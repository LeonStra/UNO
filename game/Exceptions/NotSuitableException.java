package Exceptions;

public class NotSuitableException extends Exception{
    public NotSuitableException(String message){
        super(message);
    }

    public NotSuitableException(Throwable cause){
        super(cause);
    }
}
