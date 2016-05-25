/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.database;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.sql.DataSource;
import org.traccar.Context;
import org.traccar.helper.Log;
import org.traccar.model.MiscFormatter;

public final class QueryBuilder {

    private final Map<String, List<Integer>> indexMap = new HashMap<>();
    private Connection connection;
    private PreparedStatement statement;
    private final String query;
    private final boolean returnGeneratedKeys;

    private QueryBuilder(DataSource dataSource, String query, boolean returnGeneratedKeys) throws SQLException {
        this.query = query;
        this.returnGeneratedKeys = returnGeneratedKeys;
        if (query != null) {
            connection = dataSource.getConnection();
            String parsedQuery = parse(query.trim(), indexMap);
            try {
                if (returnGeneratedKeys) {
                    statement = connection.prepareStatement(parsedQuery, Statement.RETURN_GENERATED_KEYS);
                } else {
                    statement = connection.prepareStatement(parsedQuery);
                }
            } catch (SQLException error) {
                connection.close();
                throw error;
            }
        }
    }

    private static String parse(String query, Map<String, List<Integer>> paramMap) {

        int length = query.length();
        StringBuilder parsedQuery = new StringBuilder(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int index = 1;

        for (int i = 0; i < length; i++) {

            char c = query.charAt(i);

            // String end
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {

                // String begin
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length
                        && Character.isJavaIdentifierStart(query.charAt(i + 1))) {

                    // Identifier name
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        j++;
                    }

                    String name = query.substring(i + 1, j);
                    c = '?';
                    i += name.length();
                    name = name.toLowerCase();

                    // Add to list
                    List<Integer> indexList = paramMap.get(name);
                    if (indexList == null) {
                        indexList = new LinkedList<>();
                        paramMap.put(name, indexList);
                    }
                    indexList.add(index);

                    index++;
                }
            }

            parsedQuery.append(c);
        }

        return parsedQuery.toString();
    }

    public static QueryBuilder create(DataSource dataSource, String query) throws SQLException {
        return new QueryBuilder(dataSource, query, false);
    }

    public static QueryBuilder create(
            DataSource dataSource, String query, boolean returnGeneratedKeys) throws SQLException {
        return new QueryBuilder(dataSource, query, returnGeneratedKeys);
    }

    private List<Integer> indexes(String name) {
        name = name.toLowerCase();
        List<Integer> result = indexMap.get(name);
        if (result == null) {
            result = new LinkedList<>();
        }
        return result;
    }

    public QueryBuilder setBoolean(String name, Boolean value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if(value == null)
                    statement.setNull(i, Types.BIT);
                else
                    statement.setBoolean(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setInteger(String name, Integer value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if(value == null)
                    statement.setNull(i, Types.INTEGER);
                else
                    statement.setInt(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setLong(String name, Long value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if(value == null)
                    statement.setNull(i, Types.BIGINT);
                else
                    statement.setLong(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setDouble(String name, Double value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if(value == null)
                    statement.setNull(i, Types.DOUBLE);
                else
                    statement.setDouble(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setString(String name, String value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if (value == null) {
                    statement.setNull(i, Types.VARCHAR);
                } else {
                    statement.setString(i, value);
                }
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setDate(String name, Date value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if (value == null) {
                    statement.setNull(i, Types.TIMESTAMP);
                } else {
                    statement.setTimestamp(i, new Timestamp(value.getTime()));
                }
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setObject(Object object) throws SQLException {

        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
                String name = method.getName().substring(3);
                try {
                    set(method.getReturnType(), name, method.invoke(object));
                } catch (IllegalAccessException | InvocationTargetException error) {
                    Log.warning(error);
                }
            }
        }

        return this;
    }
    
    public QueryBuilder setMap(Map<String, Object> map) throws SQLException{
        for(Entry<String,Object> entry : map.entrySet()) {
            try {
                Object value = entry.getValue();
                if(value == null)
                    set(double.class, entry.getKey(), value);
                else
                    set(value.getClass(), entry.getKey(), value);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
                Logger.getLogger(QueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this;
    }
    
    private void set(Class<?> type, String name, Object value) throws IllegalAccessException, InvocationTargetException, SQLException, IllegalArgumentException {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            setBoolean(name, (Boolean) value);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            setInteger(name, (Integer) value);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            setLong(name, (Long) value);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            setDouble(name, (Double) value);
        } else if (type.equals(String.class)) {
            setString(name, (String) value);
        } else if (type.equals(Date.class)) {
            setDate(name, (Date) value);
        } else if (type.equals(Map.class)) {
            setStringifiedMap(name, (Map)value);
        }
    }

    public QueryBuilder setStringifiedMap(String name, Map value) throws SQLException {
        if (Context.getConfig().getBoolean("database.xml")) {
            setString(name, MiscFormatter.toXmlString(value));
        } else {
            setString(name, MiscFormatter.toJsonString(value));
        }
        return this;
    }
    

    private interface ResultSetProcessor<T> {
        void process(T object, ResultSet resultSet) throws SQLException;
    }

    public <T> T executeQuerySingle(Class<T> clazz) throws SQLException {
        Collection<T> result = executeQuery(clazz);
        if (!result.isEmpty()) {
            return result.iterator().next();
        } else {
            return null;
        }
    }

    private <T> void addProcessors(
            List<ResultSetProcessor<T>> processors, Class<?> parameterType, final Method method, final String name) {

        if (parameterType.equals(boolean.class)) {
            processors.add(new ResultSetProcessor<T>() {
                @Override
                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getBoolean(name));
                    } catch (IllegalAccessException | InvocationTargetException error) {
                        Log.warning(error);
                    }
                }
            });
        } else if (parameterType.equals(int.class)) {
            processors.add(new ResultSetProcessor<T>() {
                @Override
                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getInt(name));
                    } catch (IllegalAccessException | InvocationTargetException error) {
                        Log.warning(error);
                    }
                }
            });
        } else if (parameterType.equals(long.class)) {
            processors.add(new ResultSetProcessor<T>() {
                @Override
                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getLong(name));
                    } catch (IllegalAccessException | InvocationTargetException error) {
                        Log.warning(error);
                    }
                }
            });
        } else if (parameterType.equals(double.class)) {
            processors.add(new ResultSetProcessor<T>() {
                @Override
                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getDouble(name));
                    } catch (IllegalAccessException | InvocationTargetException error) {
                        Log.warning(error);
                    }
                }
            });
        } else if (parameterType.equals(String.class)) {
            processors.add(new ResultSetProcessor<T>() {
                @Override
                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getString(name));
                    } catch (IllegalAccessException | InvocationTargetException error) {
                        Log.warning(error);
                    }
                }
            });
        } else if (parameterType.equals(Date.class)) {
            processors.add(new ResultSetProcessor<T>() {
                @Override
                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        Timestamp timestamp = resultSet.getTimestamp(name);
                        if (timestamp != null) {
                            method.invoke(object, new Date(timestamp.getTime()));
                        }
                    } catch (IllegalAccessException | InvocationTargetException error) {
                        Log.warning(error);
                    }
                }
            });
        } else if (parameterType.equals(Map.class)) {
            processors.add(new ResultSetProcessor<T>() {
                @Override
                public void process(T object, ResultSet resultSet) throws SQLException {
                    try (JsonReader reader = Json.createReader(new StringReader(resultSet.getString(name)))) {
                        method.invoke(object, MiscFormatter.fromJson(reader.readObject()));
                    } catch (IllegalAccessException | InvocationTargetException | JsonParsingException error) {
                        Log.warning(error);
                    }
                }
            });
        }
    }

    public <T> Collection<T> executeQuery(Class<T> clazz) throws SQLException {
        List<T> result = new LinkedList<>();

        if (query != null) {

            try {

                try (ResultSet resultSet = statement.executeQuery()) {

                    ResultSetMetaData resultMetaData = resultSet.getMetaData();

                    List<ResultSetProcessor<T>> processors = new LinkedList<>();

                    Method[] methods = clazz.getMethods();

                    for (final Method method : methods) {
                        if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {

                            final String name = method.getName().substring(3);

                            // Check if column exists
                            boolean column = false;
                            for (int i = 1; i <= resultMetaData.getColumnCount(); i++) {
                                if (name.equalsIgnoreCase(resultMetaData.getColumnLabel(i))) {
                                    column = true;
                                    break;
                                }
                            }
                            if (!column) {
                                continue;
                            }

                            addProcessors(processors, method.getParameterTypes()[0], method, name);
                        }
                    }

                    while (resultSet.next()) {
                        try {
                            T object = clazz.newInstance();
                            for (ResultSetProcessor<T> processor : processors) {
                                processor.process(object, resultSet);
                            }
                            result.add(object);
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new IllegalArgumentException();
                        }
                    }
                }

            } finally {
                statement.close();
                connection.close();
            }
        }

        return result;
    }

    public long executeUpdate() throws SQLException {

        if (query != null) {
            try {
                statement.execute();
                if (returnGeneratedKeys) {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if (resultSet.next()) {
                        return resultSet.getLong(1);
                    }
                }
            } finally {
                statement.close();
                connection.close();
            }
        }
        return 0;
    }

}
