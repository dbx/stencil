# Template file syntax

## Substitution

Syntax: `{%=EXPRESSION%}`

## Control structures

You can embed control structures in your templates to implement advanced templating logic. You can use it to repeatedly
display segments or conditionally hide parts of the document.

### Conditional display

This lets you conditionally hide or show parts of your document.

Syntax:

- `{%if CONDITION%}`THEN`{%end%}`
- `{%if CONDITION%}`THEN`{%else%}`ELSE`{%end%}`

Here the `THEN` part is only shown when the `CONDITION` part is evaluated to a true value. Otherwise
the `ELSE` part is shown (when specified).

Example:

- `{%if x.coverData.coverType == "LIFE"%}`*Life insurance*`{%else%}`**Unknown**`{%end%}`

In this example the text `Life insurance` is shown when the value of `x.coverData.coverType` is equal to the `"LIFE"` string.

#### Reverted conditionals

Sometimes it makes sense to swap the THEN and ELSE branches to make the expression more readable.
Therefore you can write `unless` instead of `if !` (if-not) to express the negation of the condition.

For example, `{%unless CONDITION%} Apples {%else%} Oranges {%end%}` is the same as `{%if ! CONDITION%} Oranges {%else%} Apples {%end%}`.

### Iteration

You can iterate over the elements of a list to repeatedly embed content in your document. The body part of the
iteration is inserted for each item of the list.

Syntax:

- `{%for x in elements %}BODY{%end%}`

In this example we iterate over the contents of the `elements` array. The text `BODY` is inserted for every element.

## Finding errors

- Check that every control structure is properly closed!
- Check that the control code does not contain any unexpected whitespaces.
