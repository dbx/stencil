# Working with tables

It is possible to dynamically modify tables in your documents. At the moment
conditional display of both rows and columns is supported.

It is possible to dynamically hide both table columns and rows at the same time.
But hiding rows has always higher priority, meaning if you put a `HIDE_COLUMN` marker in a row that is hidden,
the marker can not take effect and the column will not be hidden.

> Do not put a `HIDE_COLUMN` marker in a possibly hidden row.

## Dynamic rows

### Repeating rows

Conditionally hiding or repeating rows is similar to what we do with any other kind of content.  Just place a conditional
expression in the _first column_ of the affected row and close it in the _first column of the next row_.

For example, when you want to hide a row, see the following:

| Name  | Price  |
| ----- | ------ |
| `{%for x in rows%}` `{%=x.name%}`   | `{%=x.price%}`  |
| `{%end%}` |   |

### Hiding rows

You can use multiple strategies to hide rows. First is to put the parts of the conditional before/after the row to be hidden.

The second way is to embed a `{%=hideRow()%}` marker to the row you want to hide.

## Dynamic columns

It is a little bit tricky to dynamically manage column but it is certainly possible!

### Hiding columns

It is a little more complicated to dynamically hide a column.

Place a `{%=hideColumn()%}` marker to hide the current column. It makes sense to include it inside a **conditional**  block.

The following example will hide the second column if the `price_hidden` property is true.

| Name  | Price `{%if price_hidden %}` `{%=hideColumn()%}` `{%end%}` |
| ----- | ------ |
|  Tennis ball | $12 |
|  Basket ball | $123 |

When you hide a column the table's dimensions need to be re-aligned. You can specify strategies to control how the columns will be sized after the column is hidden.

- *Cutting out the column:* `{%=hideColumn('cut')%}` will remove a column and decrease the size of the table by the width of the removed column.
- *Resizing the last column* `{%=hideColumn('resize-last')%}` will resize the last column in each rows so that the total width of the table is unchanged.
- *Keeping proportions* `{%=hideColumn('rational')%}` will resize every column in a way that the ratios of their widths and the total width of the table is unchanged.

Default behaviour is `cut`.

### Repeating columns

It is currently not supported to insert repeating columns. Use copies of the column and conditional column hiding to achieve a similar effect.
