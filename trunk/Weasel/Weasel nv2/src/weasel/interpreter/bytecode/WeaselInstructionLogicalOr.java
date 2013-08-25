package weasel.interpreter.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import weasel.interpreter.WeaselInterpreter;
import weasel.interpreter.WeaselMethodExecutor;
import weasel.interpreter.WeaselPrimitive;
import weasel.interpreter.WeaselThread;

public class WeaselInstructionLogicalOr extends WeaselInstruction {

	private final int primitiveID;
	
	public WeaselInstructionLogicalOr(int primitiveID){
		this.primitiveID = primitiveID;
	}
	
	public WeaselInstructionLogicalOr(DataInputStream dataInputStream) throws IOException{
		primitiveID = dataInputStream.readInt();
	}
	
	@Override
	public void run(WeaselInterpreter interpreter, WeaselThread thread, WeaselMethodExecutor method) {
		Object o1 = thread.popValue();
		Object o2 = thread.popValue();
		
		switch(primitiveID){
		case WeaselPrimitive.BOOLEAN:
			thread.pushValue((Boolean)o1||(Boolean)o2);
		}
		
	}
	
	@Override
	protected void saveToDataStream(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeInt(primitiveID);
	}
	
	@Override
	public String toString() {
		return "logOr "+WeaselPrimitive.primitiveNames[primitiveID];
	}
	
}
