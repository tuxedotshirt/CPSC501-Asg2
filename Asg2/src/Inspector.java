import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import sun.misc.Unsafe;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class<? extends Object> c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class<?> c, Object obj, boolean recursive, int depth) {
    	disableWarning();
    	//tabDepth for formatting output
    	int tabDepth = depth;
    	
    	//Get class for object to be inspected
    	Class<?> objClass = c;
    	
    	//Need a list of objects to iterate through
    	ArrayList<Field> objectList = new ArrayList<Field>();
    	
    	//1
    	System.out.println();
    	System.out.println("Declaring class: " + objClass.getSimpleName());
   
    	
    	//2
		if (objClass.getSuperclass() != null) {
			superclassInspector(obj, objClass, objectList, tabDepth+1);
		}
		
		//3
		interfaceInspector(obj, objClass, tabDepth+1);
		//4
		constructorInspector(obj, objClass, tabDepth+1);
		//5
		methodInspector(obj, objClass, tabDepth+1);
		
		//6
		fieldInspector(obj, objClass, objectList, tabDepth+1);

		//6
		fieldClassInspector(obj, objClass, objectList, recursive, depth+1);
    }

    private void superclassInspector(Object obj, Class<?> objClass, ArrayList<Field> objectList, int depth) {
    	System.out.println();
		int tabDepth = depth;
		formatOutput(tabDepth);
		System.out.println("Superclass: " + objClass.getSuperclass().getSimpleName());
		tabDepth++;
		Class<?> superclass = objClass.getSuperclass();
		methodInspector(obj, superclass, tabDepth);
		constructorInspector(obj, superclass, tabDepth);
		fieldInspector(obj, superclass, new ArrayList<Field>(), tabDepth);    	
    }
    
    @SuppressWarnings("rawtypes")
	private void interfaceInspector(Object obj, Class<?> objClass, int depth) {
    	int tabDepth = depth;
		System.out.println();
		formatOutput(tabDepth);
		System.out.println("Interfaces: ");
		tabDepth++;
		Class[] interfaces = objClass.getInterfaces();
		if (interfaces.length > 0) {
			for (int i = 0; i < interfaces.length; i++) {
				System.out.println();
				formatOutput(tabDepth+1);
				System.out.println("Interface: " + interfaces[i].getName());
				methodInspector(obj, interfaces[i], tabDepth+2);
				constructorInspector(obj, interfaces[i], tabDepth+2);
			}
		}   	
    }

    @SuppressWarnings("rawtypes")
	private void constructorInspector(Object obj, Class<?> objClass, int depth) {
    	int tabDepth = depth;
		System.out.println();
		formatOutput(tabDepth);
		System.out.println(objClass.getSimpleName() + " constructor methods: ");
		Constructor[] constructors = objClass.getConstructors();
		if (constructors.length > 0) {
			for (int i = 0; i < constructors.length; i++) {
				Constructor<?> constructorMethod = constructors[i];
				Class[] parameters = constructors[i].getParameterTypes();
				String paramString = "";
				if (parameters.length == 0)
					paramString = "nil";
				else
					for(int j = 0; j < parameters.length; j++) {
						paramString += parameters[j].getSimpleName() + " ";
					}
				System.out.println();
				formatOutput(tabDepth+1);
				
				//Print name, parameters and modifiers for constructor
				System.out.println("Constructor: " + constructorMethod.getName()
						+ "\n\t Parameters: " + paramString 
						+ "\n\t Modifiers: " + Modifier.toString(constructorMethod.getModifiers()));
			}
		}     	
    }
    
    private void methodInspector(Object obj, Class<?> objClass, int depth) {
    	int tabDepth = depth++;
		System.out.println();
		formatOutput(tabDepth++);
		System.out.println(objClass.getSimpleName() + " methods:");
		System.out.println();
		Method[] methods = objClass.getDeclaredMethods();
		if (methods.length >= 1) {
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				String params = getMethodParameters(method);
				String except = getMethodExceptions(method);
				formatOutput(tabDepth);
				
				//print name, exceptions, parameters, return type and modifiers
				System.out.println("Method name: '" + method.getName()
						+ "\n\t Exception Types: " + except		
						+ "'\n\t Parameter Types: " + params
						+ "\n\t Return Type: " + method.getReturnType()
						+ "\n\t Modifiers: " + Modifier.toString(method.getModifiers()));
			}
		}   	
    }
    
    private void fieldInspector(Object obj, Class<?> objClass, ArrayList<Field> objectList, int depth) {
    	int tabDepth = depth;
		System.out.println();
		formatOutput(tabDepth);
		System.out.println(objClass.getSimpleName() + " fields:");
		System.out.println();
		tabDepth++;
		
		if (objClass.getDeclaredFields().length >= 1) {
			Field[] fields = objClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				try{
				Field field = fields[i];
				formatOutput(tabDepth);
				//get access for private fields
				field.setAccessible(true);
				
				//Array handler
				Object fieldObj = field.get(obj);
				if(field.getType().isArray()){
					//print name, type, modifier, current value
					System.out.println("Field: '" + field.getName()
					+ "'\n\t Type: " + field.getType().getComponentType()
					+ "\n\t Modifier: "
					+ Modifier.toString(field.getModifiers()));	

					Object[] objArray;

					if(fieldObj instanceof Object[])
						objArray =  (Object[]) fieldObj;

					else{
						int arrayLength = Array.getLength(fieldObj);
						objArray = new Object[arrayLength];
						for(int a = 0; a < arrayLength; a++){
							objArray[a] = Array.get(fieldObj, a);
						}
					}

					for(int j =0; j < objArray.length; j++){
			            Object index = objArray[j];

			            String indexString = null;
			            if(index != null)
			                if(index instanceof Class) {
			                	Class<? extends Object> indexClass = index.getClass();
			                    indexString = indexClass.getName() + " " + index.hashCode();
			                }
			                else
			                	indexString =  index.toString();
			            formatOutput(tabDepth+1);
			            System.out.println("[" + j + "]: " + indexString);
					}
				}	
				//Everything else handler
				else {
					Object value = field.get(obj);
					//print name, type, modifier, current value
					System.out.print("Field: " + field.getName() 
					+ "\n\t Type: " + field.getType().getComponentType() 
					+ "\n\t Modifier: "	+ Modifier.toString(field.getModifiers()));
					if(value != null) {
					System.out.println("\n\t Value: " + value.toString());
				}	
					else {
						System.out.println("\n\t Value: null");
					}
			}}
				catch(Exception e){
	                e.printStackTrace();
	            }
			}
		}
		
		System.out.println();
		if (objClass.getSuperclass() != null)
			fieldInspector(obj, objClass.getSuperclass(), objectList, depth);    	
    }
    
	private void formatOutput(int depth) {
		for(int i = 0; i < depth; i++) {
			System.out.print("\t");
		}
	}
	
	@SuppressWarnings("rawtypes")
	private String getMethodExceptions(Method method) {
		Class[] exceptions = method.getExceptionTypes();
		String exceptionString = "";
		if (exceptions.length == 0)
			exceptionString = "nil";
		else
			for (Class<?> exception : exceptions) {
				exceptionString += exception.getSimpleName() + " ";
			}
		return exceptionString;
	}
	
	@SuppressWarnings("rawtypes")
	private String getMethodParameters(Method method) {
		Class[] parameters = method.getParameterTypes();
		String paramString = "";
		if (parameters.length == 0)
			paramString = "nil";
		else
			for (Class<?> parameter : parameters) {
				paramString += parameter.getSimpleName() + " ";
			}
		return paramString;
	}
	
	public static void disableWarning() {
	    try {
	        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
	        theUnsafe.setAccessible(true);
	        Unsafe u = (Unsafe) theUnsafe.get(null);

	        Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
	        Field logger = cls.getDeclaredField("logger");
	        u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
	    } catch (Exception e) {
	        // ignore
	    }
	}

	private void fieldClassInspector(Object obj, Class<?> objClass, ArrayList<Field> objectList, Boolean recurse, int depth) {
    	int tabDepth = depth;
		System.out.println();
		formatOutput(tabDepth);
		System.out.println(objClass.getSimpleName() + " fields:");
		System.out.println();
		tabDepth++;
		
		if (objClass.getDeclaredFields().length >= 1) {
			Field[] fields = objClass.getDeclaredFields();
			
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				Class<?> fieldType = fields[i].getType();

				//if not primitive, then it must be a class
				if(!fieldType.isPrimitive()){
                    System.out.println("Field: " + fields[i].getName() + " Object");

                    try {
						if(fields[i].get(obj) != null){
						    	inspect(fields[i].get(obj), recurse);
						}
						else { 
							System.out.println(" object is null or uninstantiated.");
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
			}
		}	
	}
}