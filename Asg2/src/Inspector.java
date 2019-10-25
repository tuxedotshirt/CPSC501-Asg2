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
    	
    }

    private void constructorInspector(Object obj, Class<?> objClass, int depth) {
    	
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