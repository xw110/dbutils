Utils for JDBC.

DbUtils supply template methods for jdbc operations.

## Add Dependency

DbUtils is in maven central repo.

```xml
<dependency>
    <groupId>net.dongliu</groupId>
    <artifactId>dbutils</artifactId>
    <version>6.0.0</version>
</dependency>

```

## Usage

DbUtils wrap a jdbc data source into a Database instance, for querying or updating, and mapping sql results.
Database can also be create from jdbcUrl too, using a internal simple non-pooled dataSource.

Wrapping dataSource:

```java
DataSource dataSource = ...;
Database database = Database.of(dataSource);
database.query(clause)...

// or create from jdbc url

Database database = Database.of(jdbcUrl, username, password);
database.query(clause)...
```

### Query

The query method execute select queries, and return a resultSet.

```java
Student student = database.query("select id, name, age, is_male, birth_day from student where id=?", 1L)
                .map(Student.class).get();
```

### Update

The update methods execute insert/update/delete/... sqls, and return affected row num.

```java
int deleted = database.update("delete from student");
```

### Insert

The insert methods execute insert sqls, and return auto generated keys as result set.

```java
Long id = database.insert(
        "insert into student(name, age, is_male, birth_day) values(?,?,?,?)", name, age, true, birthDay)
        .map((p, rs) -> rs.getLong(1)).get();
```

### Batch Insert/Update

batchInsert/batchUpdate do the same thing as insert/update, just can accept multi params and commit at once.

### Transaction

With a SQLRunner instance, can start a transaction by:

```java
TransactionContext ctx = database.startTransaction();
try {
    ctx.update(clause, params);
    ctx.update(clause2, params2);
    ctx.commit();
} catch (Throwable e) {
    ctx.rollback();
    throw e;
}
```

or using functional style:

```java
database.withTransaction(ctx -> {
    ctx.update(clause, params);
    ctx.update(clause2, params2);
    return null;
});
```
