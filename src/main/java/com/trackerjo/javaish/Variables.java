package com.trackerjo.javaish;

import java.util.ArrayList;
import java.util.Map;

import com.trackerjo.javaish.JavaishVal.JavaishType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Variables{
    enum VarType {INT, FLOAT, BOOL, STRING, INTLIST,
        FLOATLIST,
        STRINGLIST,
        BOOLEANLIST,}
    Map<String, JavaishType> allVariables = new HashMap<String, JavaishType>();
    ArrayList<IntVar> intVariables = new ArrayList<IntVar>();
    ArrayList<FloatVar> floatVariables = new ArrayList<FloatVar>();
    ArrayList<BoolVar> boolVariables = new ArrayList<BoolVar>();
    ArrayList<StringVar> stringVariables = new ArrayList<StringVar>();
    ArrayList<IntList> intLists = new ArrayList<IntList>();
    ArrayList<FloatList> floatLists = new ArrayList<FloatList>();
    ArrayList<BoolList> boolLists = new ArrayList<BoolList>();
    ArrayList<StringList> stringLists = new ArrayList<StringList>();

    Map<String, FunctionVar> functions = new HashMap<String, FunctionVar>();
    


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

    public void removeVariable(String varName, JavaishType varType){
        allVariables.remove(varName);
        switch (varType.toString()) {
            case "INT":
                for (Iterator<IntVar> iterator = intVariables.iterator(); iterator.hasNext(); ) {
                    IntVar value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            case "FLOAT":
                for (Iterator<FloatVar> iterator = floatVariables.iterator(); iterator.hasNext(); ) {
                    FloatVar value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            case "BOOLEAN":
                for (Iterator<BoolVar> iterator = boolVariables.iterator(); iterator.hasNext(); ) {
                    BoolVar value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            case "STRING":
                for (Iterator<StringVar> iterator = stringVariables.iterator(); iterator.hasNext(); ) {
                    StringVar value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            case "INTLIST":
                for (Iterator<IntList> iterator = intLists.iterator(); iterator.hasNext(); ) {
                    IntList value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            case "FLOATLIST":
                for (Iterator<FloatList> iterator = floatLists.iterator(); iterator.hasNext(); ) {
                    FloatList value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            case "BOOLEANLIST":
                for (Iterator<BoolList> iterator = boolLists.iterator(); iterator.hasNext(); ) {
                    BoolList value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            case "STRINGLIST":
                for (Iterator<StringList> iterator = stringLists.iterator(); iterator.hasNext(); ) {
                    StringList value = iterator.next();
                    if (value.name.equals(varName)) {
                        iterator.remove();
                    }
                }
                break;
            default:
                break;
        }

        
    }

    public Variables clone(){
        Map<String, JavaishType> allVariables = new HashMap<String, JavaishType>();
        ArrayList<IntVar> intVariables = new ArrayList<IntVar>();
        ArrayList<FloatVar> floatVariables = new ArrayList<FloatVar>();
        ArrayList<BoolVar> boolVariables = new ArrayList<BoolVar>();
        ArrayList<StringVar> stringVariables = new ArrayList<StringVar>();
        ArrayList<IntList> intLists = new ArrayList<IntList>();
        ArrayList<FloatList> floatLists = new ArrayList<FloatList>();
        ArrayList<BoolList> boolLists = new ArrayList<BoolList>();
        ArrayList<StringList> stringLists = new ArrayList<StringList>();

        Map<String, FunctionVar> functions = new HashMap<String, FunctionVar>();

        for (Map.Entry<String, JavaishType> entry : this.allVariables.entrySet()) {
            allVariables.put(entry.getKey(), entry.getValue());
        }

        for (IntVar intVar : this.intVariables) {
            intVariables.add(intVar);
        }

        for (FloatVar floatVar : this.floatVariables) {
            floatVariables.add(floatVar);
        }

        for (BoolVar boolVar : this.boolVariables) {
            boolVariables.add(boolVar);
        }

        for (StringVar stringVar : this.stringVariables) {
            stringVariables.add(stringVar);
        }

        for (IntList intList : this.intLists) {
            intLists.add(intList);
        }

        for (FloatList floatList : this.floatLists) {
            floatLists.add(floatList);
        }

        for (BoolList boolList : this.boolLists) {
            boolLists.add(boolList);
        }

        for (StringList stringList : this.stringLists) {
            stringLists.add(stringList);
        }

        for (Map.Entry<String, FunctionVar> entry : this.functions.entrySet()) {
            functions.put(entry.getKey(), entry.getValue());
        }

        Variables newVariables = new Variables();
        newVariables.allVariables = allVariables;
        newVariables.intVariables = intVariables;
        newVariables.floatVariables = floatVariables;
        newVariables.boolVariables = boolVariables;
        newVariables.stringVariables = stringVariables;
        newVariables.intLists = intLists;
        newVariables.floatLists = floatLists;
        newVariables.boolLists = boolLists;
        newVariables.stringLists = stringLists;
        newVariables.functions = functions;

        return newVariables;

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

    public Map<String, JavaishType> getAllVariables() {
        return allVariables;
    }

    public Map<String, FunctionVar> getFunctions() {
        return functions;
    }

    public void addVariable(String name, JavaishType type, JavaishVal value, int lineNumber){
        
        if(allVariables.containsKey(name)){
            Error.VariableAlreadyExists(name, lineNumber);
        }
        else{
            allVariables.put(name, type);
            switch (type.toString()) {
                case "INT":
                    intVariables.add(new IntVar((JavaishInt)value, name));
                    
                    break;
                case "FLOAT":
                    floatVariables.add(new FloatVar((JavaishFloat)value, name));
                    break;
                case "BOOLEAN":
                    boolVariables.add(new BoolVar((JavaishBoolean)value, name));
                    break;

                case "STRING":
                    stringVariables.add(new StringVar((JavaishString)value, name));
                    break;
            
                default:
                    break;
            }
        }
    }

    public boolean functionExists(String name){
        return functions.containsKey(name);
    }

    public boolean isVariable(String name){
       return allVariables.containsKey(name);
   }

   public void addFunction(String name, List<Statements> body, Argument[] args, int lineNumber){
        if(functions.containsKey(name)){
            Error.FunctionAlreadyExists(name, lineNumber);
        }
        else{
            functions.put(name, new FunctionVar(name, body, args, lineNumber));
        }
   }

   public Argument[] getFunctionArgs(String name){
       return functions.get(name).getArgs();
   }

    public List<Statements> getFunctionBody(String name){
         return functions.get(name).getBody();
    }

    public int getFunctionLineNumber(String name){
        return functions.get(name).getLineNumber();
    }

   public void setVariableValue(String name, JavaishVal value, int lineNumber){
        JavaishType t = allVariables.get(name);
        switch (t.toString()) {
             case "INT":
               for(IntVar i : intVariables){
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
               case "FLOAT":
                for(FloatVar i : floatVariables){
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
               case "BOOLEAN":
                for(BoolVar i : boolVariables){
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
               case "STRING":
                for(StringVar i : stringVariables){
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

   public void setListValue(String name, JavaishInt index, JavaishVal value, int lineNumber){
        JavaishType t = allVariables.get(name);
        switch (t.toString()) {
             case "INTLIST":
               for(IntList i : intLists){
                    if(i.name.equals(name)){
                         if(value.getType() == JavaishType.INT){
                                List<JavaishInt> list = i.getValue().getList();
                                list.set(index.getValue(), (JavaishInt)value);
                                i.setValue(new JavaishIntList(list));
                             }
                             else{
                                Error.TypeMismatch("Int", value.typeString(), lineNumber);
                             
                         }
                    }
               }
               break;
               case "FLOATLIST":
                for(FloatList i : floatLists){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.FLOAT){
                                    List<JavaishFloat> list = i.getValue().getList();
                                    list.set(index.getValue(), (JavaishFloat)value);
                                    i.setValue(new JavaishFloatList(list));
                                 }
                                 else{
                                    if(value.getType() == JavaishType.INT){
                                        List<JavaishFloat> list = i.getValue().getList();
                                        list.set(index.getValue(), new JavaishFloat(((JavaishInt)value).getValue()));
                                        i.setValue(new JavaishFloatList(list));
                                    }
                                    else {
                                        Error.TypeMismatch("Float", value.typeString(), lineNumber);
                                    }
                                   
                             }
                     }
                }
                break;
               case "BOOLEANLIST":
                for(BoolList i : boolLists){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.BOOLEAN){
                                    List<JavaishBoolean> list = i.getValue().getList();
                                    list.set(index.getValue(), (JavaishBoolean)value);
                                    i.setValue(new JavaishBooleanList(list));
                                 }
                                 else{
                                    Error.TypeMismatch("Boolean", value.typeString(), lineNumber);
                             }
                     }
                }
                break;
               case "STRINGLIST":
                for(StringList i : stringLists){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.STRING){
                                    List<JavaishString> list = i.getValue().getList();
                                    list.set(index.getValue(), (JavaishString)value);
                                    i.setValue(new JavaishStringList(list));
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
         switch (t.toString()) {
              case "INT":
                for(IntVar i : intVariables){
                     if(i.name.equals(name)){
                          return i.getValue();
                     }
                }
                break;
                case "FLOAT":
                 for(FloatVar i : floatVariables){
                      if(i.name.equals(name)){
                            return i.getValue();
                      }
                 }
                 break;
                case "BOOLEAN":
                 for(BoolVar i : boolVariables){
                      if(i.name.equals(name)){
                            return i.getValue();
                      }
                 }
                 break;
                case "STRING":
                 for(StringVar i : stringVariables){
                      if(i.name.equals(name)){
                            return i.getValue();
                      }
                 }
                 break;
              default:
                 if(t == JavaishType.BOOLEANLIST || t == JavaishType.FLOATLIST || t == JavaishType.INTLIST || t == JavaishType.STRINGLIST){
                    return getList(name);
                 }
                break;
         }
         return null;
   }
    
   public JavaishListVal getList(String name){
        //System.out .println(name);
        JavaishType t = allVariables.get(name);
        switch (t.toString()) {
             case "INTLIST":
               for(IntList i : intLists){
                    if(i.name.equals(name)){
                         return new JavaishListVal(new JavaishIntList(i.getValue().getList()));
                    }
               }
               break;
               case "FLOATLIST":
                for(FloatList i : floatLists){
                     if(i.name.equals(name)){
                           return new JavaishListVal(new JavaishFloatList(i.getValue().getList()));
                     }
                }
                break;
               case "BOOLEANLIST":
                for(BoolList i : boolLists){
                     if(i.name.equals(name)){
                           return new JavaishListVal(new JavaishBooleanList(i.getValue().getList()));
                     }
                }
                break;
               case "STRINGLIST":
                for(StringList i : stringLists){
                     if(i.name.equals(name)){
                           return new JavaishListVal(new JavaishStringList(i.getValue().getList()));
                     }
                }
                break;
             default:
               break;
        }
        return null;
   }

   public JavaishType getVariableType(String name){
        return allVariables.get(name);
   }

   public void addList(String name, JavaishType type, JavaishList value, int lineNumber){
        if(allVariables.containsKey(name)){
            Error.VariableAlreadyExists(name, lineNumber);
        }
        else{
            allVariables.put(name, type);
            switch (type.toString()) {
                case "INTLIST":
                    if(value instanceof JavaishIntList){
                        JavaishIntList v = (JavaishIntList)value;
                        intLists.add(new IntList(v, name));
                    } else {
                        Error.TypeMismatch("IntList", value.typeString(), 0);
                    }   
                    
                    break;
                case "FLOATLIST":
                    if(value instanceof JavaishFloatList){
                        JavaishFloatList v = (JavaishFloatList)value;
                        floatLists.add(new FloatList(v, name));
                    } else {
                        Error.TypeMismatch("FloatList", value.typeString(), 0);
                    }
                    break;
                case "BOOLEANLIST":
                        
                        if(value instanceof JavaishBooleanList){
                            JavaishBooleanList v = (JavaishBooleanList)value;
                            boolLists.add(new BoolList(v, name));
                        } else {
                            Error.TypeMismatch("BooleanList", value.typeString(), 0);
                        }

                  
                    break;

                case "STRINGLIST":
                    if(value instanceof JavaishStringList){
                        JavaishStringList v = (JavaishStringList)value;
                        stringLists.add(new StringList(v, name));
                    } else {
                        Error.TypeMismatch("StringList", value.typeString(), 0);
                    }
                    break;
            
                default:
                    break;
            }
        }
   }

   public void setListIndexValue(String name, int index, JavaishVal value){
        JavaishType t = allVariables.get(name);
        switch (t.toString()) {
             case "INTLIST":
               for(IntList i : intLists){
                    if(i.name.equals(name)){
                         if(value.getType() == JavaishType.INT){
                                List<JavaishInt> list = i.getValue().getList();
                                list.set(index, (JavaishInt)value);
                                i.setValue(new JavaishIntList(list));
                             }
                             else{
                                Error.TypeMismatch("Int", value.typeString(), 0);
                             
                         }
                    }
               }
               break;
               case "FLOATLIST":
                for(FloatList i : floatLists){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.FLOAT){
                                    List<JavaishFloat> list = i.getValue().getList();
                                    list.set(index, (JavaishFloat)value);
                                    i.setValue(new JavaishFloatList(list));
                                 }
                                 else{
                                    Error.TypeMismatch("Float", value.typeString(), 0);
                             }
                     }
                }
                break;
               case "BOOLEANLIST":
                for(BoolList i : boolLists){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.BOOLEAN){
                                    List<JavaishBoolean> list = i.getValue().getList();
                                    list.set(index, (JavaishBoolean)value);
                                    i.setValue(new JavaishBooleanList(list));
                                 }
                                 else{
                                    Error.TypeMismatch("Boolean", value.typeString(), 0);
                             }
                     }
                }
                break;
               case "STRINGLIST":
                for(StringList i : stringLists){
                     if(i.name.equals(name)){
                            if(value.getType() == JavaishType.STRING){
                                    List<JavaishString> list = i.getValue().getList();
                                    list.set(index, ((JavaishString)value));
                                    i.setValue(new JavaishStringList(list));
                                 }
                                 else{
                                    Error.TypeMismatch("String", value.typeString(), 0);
                             }
                     }
                }
                break;
             default:
               break;
        }
   }
}

class IntVar {
    JavaishInt value;
    String name;
    IntVar(JavaishInt value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishInt getValue() {
        return value;
    }
    
}

class FloatVar {
    JavaishFloat value;
    String name;
    FloatVar(JavaishFloat value, String name){
        this.value = value;
        this.name = name;
    }

    public JavaishFloat getValue() {
        return value;
    }
}

class BoolVar {
    JavaishBoolean value;
    String name;
    BoolVar(JavaishBoolean value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishBoolean getValue() {
        return value;
    }
}

class StringVar {
    JavaishString value;
    String name;
    StringVar(JavaishString value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishString getValue() {
        return value;
    }
}

class FunctionVar {
    String name;
    List<Statements> body;
    Argument[] args;
    int lineNumber;
    FunctionVar(String name, List<Statements> body, Argument[] args, int lineNumber){
        this.name = name;
        this.body = body;
        this.args = args;
        this.lineNumber = lineNumber;
    }
    public Argument[] getArgs() {
        return args;
    }
    public List<Statements> getBody() {
        return body;
    }
    public String getName() {
        return name;
    }
    public int getLineNumber() {
        return lineNumber;
    }
}

class StringList {
   JavaishStringList value;
    String name;
    StringList(JavaishStringList value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishStringList getValue() {
        return value;
    }
    public void setValue(JavaishStringList value) {
        this.value = value;
    }
}

class IntList {
    JavaishIntList value;
    String name;
    IntList(JavaishIntList value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishIntList getValue() {
        return value;
    }
    public void setValue(JavaishIntList value) {
        this.value = value;
    }
}

class FloatList {
    JavaishFloatList value;
    String name;
    FloatList(JavaishFloatList value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishFloatList getValue() {
        return value;
    }

    public void setValue(JavaishFloatList value) {
        this.value = value;
    }
}

class BoolList {
    JavaishBooleanList value;
    String name;
    BoolList(JavaishBooleanList value, String name){
        this.value = value;
        this.name = name;
    }
    public JavaishBooleanList getValue() {
        return value;
    }
    public void setValue(JavaishBooleanList value) {
        this.value = value;
    }
}

