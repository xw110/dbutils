Utils for JDBC.

DbUtils supply template methods for jdbc operations.

## Add Dependency

DbUtils is in maven central repo.

```xml
<dependency>
    <groupId>net.dongliu</groupId>
    <artifactId>dbutils</artifactId>
    <version>4.0.1</version>
</dependency>

```

## Usage

DbUtils can wrap a jdbc dataSource, into a Database instance, for querying or updating.
Database can also be create from jdbcUrl too, using a simple non-pooled dataSource.

Wrapping dataSource:

```java
// create Database from dataSource
DataSource dataSource = ...;
Database database = Database.create(dataSource);
database.query(clause)...

// or create from jdbc url

Database database = Database.create(jdbcUrl, username, password);
database.query(clause)...
```

DbUtils can wrap one connection too, then you can setting/manager the connection by yourself:

```java
Connection conn = ...;
ConnectionExecutor.create(conn).query(clause)...
conn.close();
```

### Query

The query method execute select queries, and return a resultSet.

```java
Student student = database.query("select id, name, age, is_male, birth_day from student where id=?", 1L)
                .map(Student.class).getOne();
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

### Named Parameters

Methods queryNamed/insertNamed/updateNamed/batchInsertNamed/batchUpdateNamed, use name-parameter for sql clause, get parameter from map using keys or bean using property name:

```java
Student student = ....;
Long id = database.insertNamed(
        "insert into student(name, age, is_male, birth_day) values(:name,:age,:isMale,:birthDay)", student)
        .map((provider, rs) -> rs.getLong(1)).get();

```

### Transaction

If wrap a connection, then you can manage transaction yourself.

With a Database instance, can start a transaction by:
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
