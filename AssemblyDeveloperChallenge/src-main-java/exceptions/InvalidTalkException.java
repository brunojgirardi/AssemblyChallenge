package exceptions;

public class InvalidTalkException extends Exception{
  
    private static final long serialVersionUID = 1L;

    public InvalidTalkException(String msg) {
        super(msg);
    }

}
