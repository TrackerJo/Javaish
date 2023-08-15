package javish;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Variables{
    enum VarType {INT, FLOAT, BOOL, STRING}
    Map<String, VarType> allVariables = new HashMap<String, VarType>();
    ArrayList<intVar> intVariables = new ArrayList<intVar>();
    ArrayList<floatVar> floatVariables = new ArrayList<floatVar>();
    ArrayList<boolVar> boolVariables = new ArrayList<boolVar>();
    ArrayList<stringVar> stringVariables = new ArrayList<stringVar>();

    private VarType getType(String type){
        switch (type) {
            case "Int":
                return VarType.INT;
            case "Float":
                return VarType.FLOAT;
            case "Bool":
                return VarType.BOOL;
            case "String":
                return VarType.STRING;
            default:
                System.out.println("Invalid type");
                System.exit(0);
                return null;
        }
    }

    public void addVariable(String name, String type, String value){
        VarType t = getType(type);
        if(allVariables.containsKey(name)){
            System.out.println("Variable " + name + " already exists");
            System.exit(0);
        }
        else{
            allVariables.put(name, t);
            switch (t) {
                case INT:
                    intVariables.add(new intVar(Integer.parseInt(value), name));
                    
                    break;
                case FLOAT:
                    floatVariables.add(new floatVar(Float.parseFloat(value), name));
                    break;
                case BOOL:
                    boolVariables.add(new boolVar(Boolean.parseBoolean(value), name));
                    break;

                case STRING:
                    stringVariables.add(new stringVar(value, name));
                    break;
            
                default:
                    break;
            }
        }
    }

   public boolean isVariable(String name){
       return allVariables.containsKey(name);
   }

   public String getVariableValue(String name){
         VarType t = allVariables.get(name);
         switch (t) {
              case INT:
                for(intVar i : intVariables){
                     if(i.name.equals(name)){
                          return Integer.toString(i.value);
                     }
                }
                break;
                case FLOAT:
                 for(floatVar i : floatVariables){
                      if(i.name.equals(name)){
                            return Float.toString(i.value);
                      }
                 }
                 break;
                case BOOL:
                 for(boolVar i : boolVariables){
                      if(i.name.equals(name)){
                            return Boolean.toString(i.value);
                      }
                 }
                 break;
                case STRING:
                 for(stringVar i : stringVariables){
                      if(i.name.equals(name)){
                            return i.value;
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
    int value;
    String name;
    intVar(int value, String name){
        this.value = value;
        this.name = name;
    }
}

class floatVar {
    float value;
    String name;
    floatVar(float value, String name){
        this.value = value;
        this.name = name;
    }
}

class boolVar {
    boolean value;
    String name;
    boolVar(boolean value, String name){
        this.value = value;
        this.name = name;
    }
}

class stringVar {
    String value;
    String name;
    stringVar(String value, String name){
        this.value = value;
        this.name = name;
    }
}

