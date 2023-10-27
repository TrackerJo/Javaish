

# Javaish

Programming language made to resemble java, javascript, and english. Made for beginners to get into coding and learn how to think like a programmer. Not meant for professional use.
## Features

#### Variables
##### Types
- Integers (int)
- Floats (float)
- Strings (String)
- Booleans (bool)
- Lists
    - Integer Lists (int[])
    - Float Lists (float[])
    - String Lists (String[])
    - Boolean Lists (bool[])

##### Usage
- Declare - `let [type] [var] = [expression].`
- Assign - `[var] = [expression].`
- Mutate(lists can only do add mutations) - `add [expression] to [var]` or `subtract [expression] from [var]` or `divide/multiply [var] by [expression]`
- List Only Usages
    - RemoveAt = `removeAt [listVar] sub [index]`
    - Remove = `remove [expression] from [listVar]`
    - RemoveAll = `removeAll [expression] from [listVar]`


#### Functions
##### Built-in Functions
- Print(Any Value) - `print([expression]).`
- Show Message Box(String) - `showMessageDialog([expression]).`
- Show Input Box(String) - `showInputDialog([expression]).`
- To String(Any Value but string) - `toString([expression]).`
- To Integer(Any Value but int) - `toInt([expression]).`
- To Float(Any Value but float) - `toFloat([expression]).`

##### Declare and Call
- Declare - `function [name]([arguments]){}`
- Call - `[name]([parameters]).`

#### Operators
- Equals - `=` or `equals`
- Not Equal - `!=` or `not equals`
- Greater than - `>` or `greater than`
- Less than - `<` or `less than`
- Greater than or equal to - `>=` or `Greater than or equal to`
- Less than or equal to - `<=` or `Less than or equal to`
- Plus - `+` or `plus`
- Minus - `-` or `minus`
- Multiply - `*` or `times`
- Divide - `/` or `divide`
- And - `&&` or `and`
- Or - `||` or `or`

#### List Expressions
- When using a list in an expression, the list can be the only element in the expression, you cant do any Operators to lists in Expressions
- When creating a list, surround with brackets and enter each value followed by a comma and a space - ex. `[2, 2, 3, 3]`
