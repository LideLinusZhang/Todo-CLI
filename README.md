# Todo CLI
## Available Commands
```
add-item         Add a todo item to a pre-existing category.
delete-category  Delete a todo category and all items under it.
delete-item      Delete a todo item.
modify-item      Modify a todo item.
modify-category  Modify a todo category.
list-categories  Display todo categories.
list-items       List all todo items under a category.
```
## Detailed Explanations
### Add Category
```
Usage: cli add-category [OPTIONS] CATEGORYNAME

  Add a todo category.

Options:
  --favoured  If entered, the added category will be set to be favoured.

Arguments:
  CATEGORYNAME  Name of the category to be added.
```

### Add Item
```
Usage: cli add-item [OPTIONS] CATEGORYIDENTIFIER name [description]

  Add a todo item to a pre-existing category.

Options:
  --importance [CRITICAL|VERY_HIGH|HIGH|NORMAL|BELOW_NORMAL|LOW]
  --deadline VALUE
  --search-category-by [id|name]   Type of identifiers used to determine which
                                   category to add to


```

### Delete Category
```
Usage: cli delete-category [OPTIONS] CATEGORYID

  Delete a todo category and all items under it.

Arguments:
  CATEGORYID  ID of the category to be deleted

```

### Delete Item
```
Usage: cli delete-item [OPTIONS] ITEMID

  Delete a todo item.

Arguments:
  ITEMID  ID of the todo item to be deleted.

```

### List Categories
```
Usage: cli list-categories [OPTIONS]

  Display todo categories.
```
### List Items
```
Usage: cli list-items [OPTIONS] CATEGORYID

  List all todo items under a category.
```

### Modify Category
```
Usage: cli modify-category [OPTIONS] CATEGORYID VALUE

  Modify a todo category.

Options:
  --field [name|favoured]

Arguments:
  CATEGORYID  Unique ID of the todo category.
  VALUE
```
### Modify Item
```
Usage: cli modify-item [OPTIONS] ITEMID VALUE

  Modify a todo item.

Options:
  --field [name|description|importance|deadline]

Arguments:
  ITEMID  Unique ID of the todo item.
  VALUE   To remove deadline, enter "none".
```
