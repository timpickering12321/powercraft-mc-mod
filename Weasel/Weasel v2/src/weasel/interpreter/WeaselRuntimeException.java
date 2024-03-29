package weasel.interpreter;

public class WeaselRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 5813434606758913978L;

	public WeaselRuntimeException(String message) {
		super(message);
	}

	public WeaselRuntimeException(Throwable e) {
		super(e);
	}
	
	public void setWeaselThread(WeaselThread weaselThread){
		setStackTrace(weaselThread.getStackTrace());
	}
	
}
