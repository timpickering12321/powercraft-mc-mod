package weasel.interpreter;

import java.util.HashMap;

import weasel.interpreter.io.WeaselClassFile;

public class WeaselClassBuildPlan extends WeaselPackage {
	
	private final WeaselInterpreter interpreter;
	private final HashMap<WeaselClass[], WeaselClass> loadedGenericClasses = new HashMap<WeaselClass[], WeaselClass>();
	private WeaselClassFile wcf;
	private int modifier;
	private WeaselGenericInfo[] genericInfos;
	private WeaselClassGenericBuildPlan[] superClasses;
	private WeaselField fields[];
	private WeaselMethod methods[];
	private WeaselClassBuildPlan[] innerClasses;
	
	protected WeaselClassBuildPlan(WeaselInterpreter interpreter, String name, WeaselClassFile wcf) {
		super(name);
		this.interpreter = interpreter;
		this.wcf = wcf;
		if(!name.equals(wcf.wClass.name)){
			throw new WeaselRuntimeException("Names of class %s in file %s are not equal", wcf.wClass.name, getName());
		}
		modifier = wcf.wClass.modifier;
		genericInfos = new WeaselGenericInfo[wcf.genericInfos.length];
		for(int i=0; i<genericInfos.length; i++){
			genericInfos[i] = new WeaselGenericInfo(wcf.genericInfos[i].name, new WeaselClassGenericBuildPlan[wcf.genericInfos[i].classes.length]);
		}
		if(!getName().equals("Object")){
			if(wcf.superClasses.length==0){
				superClasses = new WeaselClassGenericBuildPlan[1];
				superClasses[0] = new WeaselClassGenericBuildPlan(interpreter.getWeaselClassBuildPlan("Object"));
			}else{
				superClasses = new WeaselClassGenericBuildPlan[wcf.superClasses.length];
				for(int i=0; i<superClasses.length; i++){
					superClasses[i] = getGenericBuildPlan(wcf.superClasses[i]);
					WeaselChecks.checkSuperClass(superClasses[i].getClassBuildPlan());
				}
			}
		}
		innerClasses = new WeaselClassBuildPlan[wcf.innerClasses.length];
		for(int i=0; i<superClasses.length; i++){
			innerClasses[i] = new WeaselClassBuildPlan(interpreter, wcf.innerClasses[i].wClass.name, wcf.innerClasses[i]);
			childPackages.put(wcf.innerClasses[i].wClass.name, innerClasses[i]);
		}
	}
	
	protected WeaselClassGenericBuildPlan getGenericBuildPlan(weasel.interpreter.io.WeaselClassFile.WeaselClass wClass){
		WeaselClassBuildPlan wcbp = interpreter.getWeaselClassBuildPlan(wClass.name);
		WeaselClassGenericBuildPlan[] obj = new WeaselClassGenericBuildPlan[wClass.typeParams.length];
		for(int i=0; i<obj.length; i++){
			obj[i] = interpreter.getGenericBuildPlan(wClass.typeParams[i], this);
		}
		if(wcbp.getGenericInfos().length != obj.length){
			throw new WeaselRuntimeException("Wrong argument of generic types for %s", name);
		}
		return new WeaselClassGenericBuildPlan(wcbp, obj);
	}
	
	public WeaselClass getGenericClass(WeaselClass[] generics) {
		WeaselClass genericClass = loadedGenericClasses.get(generics);
		if(genericClass==null){
			genericClass = new WeaselClass(this, generics);
			loadedGenericClasses.put(generics, genericClass);
		}
		return genericClass;
	}

	@Override
	public void addPackage(WeaselPackage p) {
		throw new WeaselRuntimeException("Can't add package %s to class %s", p.getSimpleName(), getName());
	}
	
	protected void resolve() {
		if(wcf!=null){
			for(int i=0; i<genericInfos.length; i++){
				WeaselClassGenericBuildPlan[] genericBuildPlans = genericInfos[i].getBuildPlans();
				for(int j=0; j<genericBuildPlans.length; j++){
					genericBuildPlans[i] = getGenericBuildPlan(wcf.genericInfos[i].classes[j]);
				}
			}
			fields = new WeaselField[wcf.fields.length];
			for(int i=0; i<fields.length; i++){
				fields[i] = new WeaselField(this, wcf.fields[i]);
			}
			methods = new WeaselMethod[wcf.methods.length];
			for(int i=0; i<methods.length; i++){
				methods[i] = new WeaselMethod(this, wcf.methods[i]);
			}
			wcf  = null;
		}
	}

	public int getModifier() {
		return modifier;
	}

	public int getGenericIndex(String name) {
		for(int i=0; i<genericInfos.length; i++){
			if(genericInfos[i].getName().equals(name)){
				return i;
			}
		}
		return -1;
	}

	public WeaselGenericInfo[] getGenericInfos() {
		return genericInfos;
	}

	public WeaselInterpreter getInterpreter() {
		return interpreter;
	}
	
}
