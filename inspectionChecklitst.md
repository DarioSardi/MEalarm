Java Inspection Checklist
## Variable, Attribute, and Constant Declaration Defects (VC)
- [x] Are descriptive variable and constant names used in accord with naming conventions?
- [x] Are there variables or attributes with confusingly similar names?
- [x] Is every variable and attribute correctly typed?
- [x] Is every variable and attribute properly initialized?
- [x] Could any non-local variables be made local?
- [x] Are there variables or attributes that should be constants?
- [x] Are there attributes that should be local variables?
- [x] Do all attributes have appropriate access modifiers (private, protected, public)?

## Method Definition Defects (FD)
- [x] Are descriptive method names used in accord with naming conventions?
- [x] Is every method parameter value checked before being used?
- [x] For every method: Does it return the correct value at every method return point?
- [x] Do all methods have appropriate access modifiers (private, protected, public)?
- [x] Are there static methods that should be non-static or vice-versa?
## Class Definition Defects (CD)
- [x] Does each class have appropriate constructors
- [x] Do any subclasses have common members that should be in the superclass?
- [x] Can the class inheritance hierarchy be simplified?
## Data Reference Defects (DR)
- [x] For every array reference: Is each subscript value within the defined bounds?
- [x] For every object or array reference: Is the value certain to be non-null?
## Comparison/Relational Defects (CR)
- [x] For every boolean test: Is the correct condition checked?
- [x] Are the comparison operators correct?
- [x] Is each boolean expression correct?
- [x] Are there improper and unnoticed side-effects of a comparison?
- [x] Has an "&" inadvertently been interchanged with a "&&" or a "|" for a "||"?
## Control Flow Defects (CF)
- [x] For each loop: Is the best choice of looping constructs used?
- [x] Will all loops terminate?
- [x] When there are multiple exits from a loop, is each exit necessary and handled properly?
- [x] Are missing switch case break statements correct and marked with a comment?
- [x] Do named break statements send control to the right place?
- [x] Is the nesting of loops and branches too deep, and is it correct?
- [x] Can any nested if statements be converted into a switch statement?
- [x] Are null bodied control structures correct and marked with braces or comments?
- [ ] Are all exceptions handled appropriately?
- [x] Does every method terminate?
## Input-Output Defects (IO)
- [x] Have all files been opened before use?
- [x] Are the attributes of the input object consistent with the use of the file?
- [x] Have all files been closed after use?
- [x] Are there spelling or grammatical errors in any text printed or displayed?
- [x] Are all I/O exceptions handled in a reasonable way?
9. Module Interface Defects (MI)
- [x] Are the number, order, types, and values of parameters in every method call in agreement
with the called method's declaration?
- [x] Do the values in units agree (e.g., inches versus yards)?
- [x] If an object or array is passed, does it get changed, and changed correctly by the called
method?
## Comment Defects (CM)
- [x] Does every method, class, and file have an appropriate header comment?
- [x] Does every attribute, variable, and constant declaration have a comment?
- [x] Is the underlying behavior of each method and class expressed in plain language?
- [x] Is the header comment for each method and class consistent with the behavior of the method
or class?
- [x] Do the comments and code agree?
- [x] Do the comments help in understanding the code?
- [x] Are there enough comments in the code?
- [x] Are there too many comments in the code?
## Layout and Packaging Defects (LP)
- [x] Is a standard indentation and layout format used consistently?
- [x] For each method: Is it no more than about 60 lines long?
- [x] For each compile module: Is no more than about 600 lines long?
## Modularity Defects (MO)
- [x] Is there a low level of coupling between modules (methods and classes)?
- [x] Is there a high level of cohesion within each module (methods or class)?
- [x] Is there repetitive code that could be replaced by a call to a method that provides the behavior of the repetitive code?
- [x] Are the Java class libraries used where and when appropriate?
## Storage Usage Defects (SU)
- [x] Are arrays large enough?
- [x] Are object and array references set to null once the object or array is no longer needed?
