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
Usage: todo-cli add-category [OPTIONS] CATEGORYNAME

  Add a todo category.

Options:
  --favoured  If entered, the added category will be set to be favoured.

Arguments:
  CATEGORYNAME  Name of the category to be added.
```

### Add Item
```
Usage: todo-cli add-item [OPTIONS] CATEGORYIDENTIFIER name [description]

  Add a todo item to a pre-existing category.

Options:
  --importance [CRITICAL|VERY_HIGH|HIGH|NORMAL|BELOW_NORMAL|LOW]
  --deadline YYYY-MM-DD            Deadline of the item, in the format of
                                   YYYY-MM-DD.
  --search-category-by [id|name]   Type of identifiers used to determine which
                                   category to add to
  --favoured                       If entered, the added item will be set to
                                   be favoured.

Arguments:
  CATEGORYIDENTIFIER  Value of the identifier used to determine which category
                      to add to
  name
  description



```

### Delete Category
```
Usage: todo-cli delete-category [OPTIONS] CATEGORYID

  Delete a todo category and all items under it.

Arguments:
  CATEGORYID  ID of the category to be deleted

```

### Delete Item
```
Usage: todo-cli delete-item [OPTIONS] ITEMID


  Delete a todo item.

Arguments:
  ITEMID  ID of the todo item to be deleted.

```

### List Categories
```
Usage: todo-cli list-categories [OPTIONS]

  Display todo categories.

Options:
  --sorted-by [Id|Name|Favoured]
  --descending
```
### List Items
```
Usage: todo-cli list-items [OPTIONS] CATEGORYID

  List all todo items under a category.

Options:
  --sorted-by [Id|Name|Description|Favoured|Deadline|Importance]
  --descending

Arguments:
  CATEGORYID  ID of the todo category.

```

### Modify Category
```
Usage: todo-cli modify-category [OPTIONS] CATEGORYID VALUE

  Modify a todo category.

Options:
  --field [Name|Favoured]

Arguments:
  CATEGORYID  ID of the todo category.
  VALUE
```
### Modify Item
```
Usage: todo-cli modify-item [OPTIONS] ITEMID VALUE

  Modify a todo item.

Options:
  --field [Name|Description|Favoured|Deadline|Importance]
                                   Field to modify.

Arguments:
  ITEMID  ID of the todo item.
  VALUE   Value that the field will be modified to be.
          For favoured, it should be either true or false.
          For importance, it should be in [CRITICAL, VERY_HIGH, HIGH, NORMAL,
          BELOW_NORMAL, LOW].
          For deadline, the value should be in the format of YYYY-MM-DD. To
          remove deadline, enter "none".
```

### Synchronize From Server
```
Usage: todo-cli sync-from-server [OPTIONS]

  Synchronize all categories and items from the server to local.
```
