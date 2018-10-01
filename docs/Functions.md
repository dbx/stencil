# Functions

You can call functions from within the template files and embed the call result easily by writing
`{%=functionName(arg1, arg2, arg3, ...)%}` expression in the document template.

This is a short description of the functions implemented in Stenci.

## Basic Functions

### Switch

### Coalesce

Returns the first non-empty value from the parameters.

**Exampe:**

`{%=coalesce(partnerFullName, partnerShortName, partnerName)%}`

### Empty

Decides if a parameter is empty or missing. Useful in conditional statements.

**Example:**

`{%if empty(userName) %}Unknown User{%else%}{%=userName%}{%end%}`

If the value of `userName` is missing then `Unknown User` will be inserted, otherwise the value is used.

## String functions

These functions deal with textual data.

### Format



### Date functions

