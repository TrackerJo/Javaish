package javish;

import javish.Variables.VarType;

public class Argument {
    VarType type;
    String name;

    public Argument(VarType type, String name) {
        this.type = type;
        this.name = name;
    }

    public VarType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
