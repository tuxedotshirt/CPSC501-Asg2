import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
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

    }

    private void superclassInspector(Object obj, Class<?> objClass, ArrayList<Field> objectList, int depth) {
    	
    }
    
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
				
				System.out.println("Constructor: " + constructorMethod.getName()
						+ "\n\t-Parameters: " + paramString 
						+ "\n\t-Modifiers: " + Modifier.toString(constructorMethod.getModifiers()));
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
				System.out.println("Method name: '" + method.getName()
						+ "'\n\t-Parameter Types: " + params
						+ "\n\t-Modifiers: "
						+ Modifier.toString(method.getModifiers())
						+ "\n\t-Return Types: " + method.getReturnType()
						+ "\n\t-Exception Types: " + except);
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
				Field field = fields[i];
				formatOutput(tabDepth);
				//get access for private fields
				field.setAccessible(true);
				
				//Unable to access size of array.
				/*
				if(field.getType().isArray()) {
					System.out.println(field.getName());
				    System.out.println("Field: '" + field.getName()
					+ "'\n\t-Type: " + field.getType().getComponentType()
					+ "\n\t-Modifier: "
					+ Modifier.toString(field.getModifiers()));
					System.out.println();
					
					int length = Array.getLength(field);

					for (int j = 0; j < length; j ++) {
				    	formatOutput(tabDepth+1);
				    	Object arrayElement = Array.get(field, j);
				        System.out.println("Element " + j + ": " + arrayElement);
					}

				}
				else *///{
					System.out.println("Field: " + field.getName() 
					+ "\n\t-Type: " + field.getType().getComponentType() 
					+ "\n\t-Modifier: "	+ Modifier.toString(field.getModifiers()));
				//}	
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
	
	private String getMethodParameters(Method method) {
		Class[] parameters = method.getParameterTypes();
		String params = "";
		if (parameters.length == 0)
			params = "nil";
		else
			for (Class<?> parameter : parameters) {
				params += parameter.getSimpleName() + " ";
			}
		return params;
	}

}