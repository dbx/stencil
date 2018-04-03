# Math and Logics

It is possible to embed complex mathematical equations and logical formulae in the document logic.

## Simple math

- Use of integers and floating point decimals are supported.
- Use of parentheses in expressions is supported. For example: `{%=(x.price * (1 + x.tax_pct))%}`
- The following mathematical algebraic operators are supported: `+, -, *, /, %`.

## Logics in conditions

- You can use logical `and`, `or`, `not` operators in the logical expressions.

## Function calls

There are simple functions you can call from within the template documents.

### Coalesce

Accepts any number of arguments, returns the first not-empty value.

Example: `{%=coalesce(x.price, x.premium, 0)%}`

