Utils for JDBC.

DbUtils supply template methods for jdbc operations.

## Add Dependency

DbUtils is in maven central repo.

```xml
<dependency>
    <groupId>net.dongliu</groupId>
    <artifactId>dbutils</artifactId>
    <version>5.0.0</version>
</dependency>

```

## Usage

DbUtils can wrap a jdbc dataSource, into a SQLRunner instance, for querying or updating.
SQLRunner can also be create from jdbcUrl too, using a simple non-pooled dataSource.

Wrapping dataSource:

```java
// create SQLRunner from dataSource
DataSource dataSource = ...;
SQLRunner runner = SQLRunner.of(dataSource);
runner.query(clause)...

// or create from jdbc url

SQLRunner runner = SQLRunner.of(jdbcUrl, username, password);
runner.query(clause)...
```

DbUtils can wrap one connection too, then you can setting/manager the connection by yourself:

```java
Connection conn = ...;
SQLRunner runner = SQLRunner.of(conn);
runner.query(clause)...
conn.close();
```

### Query

The query method execute select queries, and return a resultSet.

```java
Student student = runner.query("select id, name, age, is_male, birth_day from student where id=?", 1L)
                .map(Student.class).get();
```

### Update

The update methods execute insert/update/delete/... sqls, and return affected row num.

```java
int deleted = runner.update("delete from student");
```

### Insert

The insert methods execute insert sqls, and return auto generated keys as result set.

```java
Long id = runner.insert(
        "insert into student(name, age, is_male, birth_day) values(?,?,?,?)", name, age, true, birthDay)
        .map((p, rs) -> rs.getLong(1)).get();
```

### Batch Insert/Update

batchInsert/batchUpdate do the same thing as insert/update, just can accept multi params and commit at once.

### Named Parameters

NamedSQLRunner, use name-parameter for sql clause, get parameter from map using keys or bean using property name:

```java
NamedSQLRunner runner = NamedSQLRunner.of(dataSource);
Student student = ....;
Long id = runner.insertBean(
        "insert into student(name, age, is_male, birth_day) values(:name,:age,:isMale,:birthDay)", student)
        .map((provider, rs) -> rs.getLong(1)).get();

```

### Transaction

With a SQLRunner instance, can start a transaction by:

```java
TransactionContext ctx = runner.startTransaction();
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
runner.withTransaction(ctx -> {
    ctx.update(clause, params);
    ctx.update(clause2, params2);
    return null;
});
```
