package javaish;


import java.util.ArrayList;
import java.util.Map;

import javaish.JavaishVal.JavaishType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class Variables{
    enum VarType {INT, FLOAT, BOOL, STRING}
    Map<String, JavaishType> allVariables = new HashMap<String, JavaishType>();
    ArrayList<intVar> intVariables = new ArrayList<intVar>();
    ArrayList<floatVar> floatVariables = new ArrayList<floatVar>();
    ArrayList<boolVar> boolVariables = new ArrayList<boolVar>();
    ArrayList<stringVar> stringVariables = new ArrayList<stringVar>();
    Map<String, functionVar> functions = new HashMap<String, functionVar>();

    public Variables(Variables variables){
        this.allVariables = variables.allVariables;
        this.intVariables = variables.intVariables;
        this.floatVariables = variables.floatVariables;
        this.boolVariables = variables.boolVariables;
        this.stringVariables = variables.stringVariables;
        this.functions = variables.functions;
    }

    public Variables(){
        
    }

    private JavaishType getType(String type){
        switch (type) {
            case "int":
                return JavaishType.INT;
            case "float":
                return JavaishType.FLOAT;
            case "bool":
                return JavaishType.BOOLEAN;
            case "String":
                return JavaishType.STRING;
            default:
                //System.out .println("Invalid type");
                System.exit(0);
                return null;
        }
    }

    public void addVariable(String name, JavaishType type, JavaishVal value){
        
        if(allVariables.containsKey(name)){
            Error.VariableAlreadyExists(name);
        }
        else{
            allVariables.put(name, type);
            switch (type) {
                case INT:
                    intVariables.add(new intVar((JavaishInt)value, name));
                    
                    break;
                case FLOAT:
                    floatVariables.add(new floatVar((JavaishFloat)value, name));
                    break;
                case BOOLEAN:
                    boolVariables.add(new boolVar((JavaishBoolean)value, name));
                    break;

                case STRING:
                    stringVariables.add(new stringVar((JavaishString)value, name));
                    break;
            
                default:
                    break;
            }
        }
    }

    public boolean functionExists(String name){
        return functions.containsKey(name);
    }

   public void removeVariable(String name){
    JavaishType t = allVariables.get(name);
    switch (t) {
        case INT:
            Iterator<intVar> intIterator = intVariables.iterator();
            while (intIterator.hasNext()) {
                intVar i = intIterator.next();
                if (i.name.equals(name)) {
                    intIterator.remove();
                }
            }
            break;
        case FLOAT:
            Iterator<floatVar> floatIterator = floatVariables.iterator();
            while (floatIterator.hasNext()) {
                floatVar i = floatIterator.next();
                if (i.name.equals(name)) {
                    floatIterator.remove();
                }
            }
            break;
        case BOOLEAN:
            Iterator<boolVar> boolIterator = boolVariables.iterator();
            while (boolIterator.hasNext()) {
                boolVar i = boolIterator.next();
                if (i.name.equals(name)) {
                    boolIterator.remove();
                }
            }
            break;
        case STRING:
            Iterator<stringVar> stringIterator = stringVariables.iterator();
            while (stringIterator.hasNext()) {
                stringVar i = stringIterator.next();
                if (i.name.equals(name)) {
                    stringIterator.remove();
                }
            }
            break;
    }
}
   public boolean isVariable(String name){
       return allVariables.containsKey(name);
   }

   public void addFunction(String name, List<Statements> body, Argument[] args){
        if(functions.containsKey(name)){
            Error.FunctionAlreadyExists(name);
        }
        else{
            functions.put(name, new functionVar(name, body, args));
        }
   }

   public Argument[] getFunctionArgs(String name){
       return functions.get(name).getArgs();
   }

    public List<Statements> getFunctionBody(String name){
         return functions.get(name).getBody();
    }

   public void setVariableValue(String name, JavaishVal value, int lineNumber){
        JavaishType t = allVariables.get(name);
        switch (t) {
             case INT:
               for(intVar i : intVariables){
                    if(i.name.equals(name)){
                         if(value.getType() == JavaishType.INT){
                                i.value = (JavaishInt)value;
                             }
                             else{
                                Error.TypeMismatch("Int", value.typeString(), lineNumber);
                             
                         }
                    }
               }
               break;
               case FLOAT:
                for(floatVar i : floatVariables){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.FLOAT){
                                    i.value = (JavaishFloat)value;
                                 }
                                 else{
                                    if(value.getType() == JavaishType.INT){
                                        i.value = new JavaishFloat(((JavaishInt)value).getValue());
                                    }
                                    else {
                                        Error.TypeMismatch("Float", value.typeString(), lineNumber);
                                    }
                                   
                             }
                     }
                }
                break;
               case BOOLEAN:
                for(boolVar i : boolVariables){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.BOOLEAN){
                                    i.value = (JavaishBoolean)value;
                                 }
                                 else{
                                    Error.TypeMismatch("Boolean", value.typeString(), lineNumber);
                             }
                     }
                }
                break;
               case STRING:
                for(stringVar i : stringVariables){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.STRING){
                                    i.value = (JavaishString)value;
                                 }
                                 else{
                                    Error.TypeMismatch("String", value.typeString(), lineNumber);
                             }
                     }
                }
                break;
             default:
               break;
        }
   }

   public JavaishVal getVariableValue(String name){
         //System.out .println(name);
         JavaishType t = allVariables.get(name);
         switch (t) {
              case INT:
                for(intVar i : intVariables){
                     if(i.name.equals(name)){
                          return i.getValue();
                     }
                }
                break;
                case FLOAT:
                 for(floatVar i : floatVariables){
                      if(i.name.equals(name)){
                            return i.getValue();
                      }
                 }
                 break;
                case BOOLEAN:
                 for(boolVar i : boolVariables){
                      if(i.name.equals(name)){
                            return i.getValue();
                      }
                 }
                 break;
                case STRING:
                 for(stringVar i : stringVariables){
                      if(i.name.equals(name)){
                            return i.getValue();
                      }
                 }
                 break;
              default:
                break;
         }
         return null;
   }
    
}

class intVar {
    JavaishInt value;
    String name;
    intVar(JavaishInt value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishInt getValue() {
        return value;
    }
}

class floatVar {
    JavaishFloat value;
    String name;
    floatVar(JavaishFloat value, String name){
        this.value = value;
        this.name = name;
    }

    public JavaishFloat getValue() {
        return value;
    }
}

class boolVar {
    JavaishBoolean value;
    String name;
    boolVar(JavaishBoolean value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishBoolean getValue() {
        return value;
    }
}

class stringVar {
    JavaishString value;
    String name;
    stringVar(JavaishString value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishString getValue() {
        return value;
    }
}

class functionVar {
    String name;
    List<Statements> body;
    Argument[] args;
    functionVar(String name, List<Statements> body, Argument[] args){
        this.name = name;
        this.body = body;
        this.args = args;
    }
    public Argument[] getArgs() {
        return args;
    }
    public List<Statements> getBody() {
        return body;
    }public String getName() {
        return name;
    }
}

