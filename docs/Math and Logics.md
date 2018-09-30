# Math and Logics

It is possible to embed complex mathematical equations and logical formulae in the document logic.

## Simple math

- Use of integers and floating point decimals are supported.
- Use of parentheses in expressions is supported. For example: `{%=(x.price * (1 + x.tax_pct))%}`
- The following mathematical algebraic operators are supported: `+, -, *, /, %`.

## Logics in conditions

- You can use logical operators in the expressions. See:

| operator | symbol | example | meaning |
|-----|----|-------------|---|
| conjuction (and) | `&` | `a & b` | both `a` and `b` are not null and not false values |
| disjunction (or) | `\|` | `a\|b` | either `a` or `b` or both are not null or false |
| negation (not) | `!` | `!a` | value `a` is false or null |

## Function calls

There are simple functions you can call from within the template documents.

### Coalesce

Accepts any number of arguments, returns the first not-empty value.

Example: `{%=coalesce(x.price, x.premium, 0)%}`

