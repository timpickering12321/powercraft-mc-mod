package powercraft.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PC_OreInfo {
	
	public int genOresInChunk();
	
	public int genOresDepositMaxCount();
	
	public int genOresMaxY();
	
	public int genOresMinY();
	
}