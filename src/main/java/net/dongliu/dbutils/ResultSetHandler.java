/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dongliu.dbutils;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementations of this interface convert ResultSets into other objects.
 *
 * @param <T> the target type the input ResultSet will be converted to.
 */
@FunctionalInterface
public interface ResultSetHandler<T> {

    /**
     * Turn the ResultSet into an Object.
     *
     * @param rs The ResultSet to handle.  It has not been touched
     *           before being passed to this method.
     * @return An Object initialized with ResultSet data. It is
     * legal for implementations to return null if the
     * ResultSet contained 0 rows.
     * @throws SQLException if a database access error occurs
     */
    @Nullable
    T handle(ResultSet rs) throws SQLException;

}