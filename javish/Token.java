package javish;


class Token{
    String type;
    String value;
    Token(String type,String value){
        this.type=type;
        this.value=value;
    }
}

class Identifiers {
   String[] identifiers = {"let", "add", "subtract", "multiply", "divide", "print", "if", "while", "for", "function", "return"};
   

    public String isIdentifier(String identifier){
        for (String i : identifiers) {
            if (identifier.startsWith(i)) {
                return i;
            }
        }
        return null;
    }

  


    
}