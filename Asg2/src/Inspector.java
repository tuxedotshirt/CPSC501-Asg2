import java.lang.reflect.Field;
import java.util.ArrayList;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {

    	int tabDepth = depth;
    	Class<?> objClass = c;
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
    	
    }
    
    private void fieldInspector(Object obj, Class<?> objClass, ArrayList<Field> objectList, int depth) {
    	
    }
    

}