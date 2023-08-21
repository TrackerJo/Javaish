package javaish;
public sealed interface JavaishVal {
    public Object getValue();
   
}

final class JavaishInt implements JavaishVal {
    int value;
    public Integer getValue() {
        return value;
    }
    JavaishInt(int value){
        this.value = value;
    }
    
}

final class JavaishString implements JavaishVal {
    String value;
    public String getValue() {
        return "";
    }
   
    JavaishString(String value){
        this.value = value;
    }
}

final class JavaishBoolean implements JavaishVal {
    Boolean value;
    public Boolean getValue() {
        return true;
    }
    public void setValue(Object value){
        this.value = (Boolean) value;
    }
    JavaishBoolean(Boolean value){
        this.value = value;
    }
}