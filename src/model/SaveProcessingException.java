package model;

public class SaveProcessingException extends Exception{
    public SaveProcessingException(){
        super();
    }

    public SaveProcessingException(Object message){
        super(message.toString());
    }

}
