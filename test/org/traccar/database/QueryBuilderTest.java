/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.traccar.database;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.traccar.model.ObdInfo;

/**
 *
 * @author admin
 */
public class QueryBuilderTest { 
    HashMap<Integer, Object> Values;
    PreparedStatement mockStatement;
    @Test
    public void testObject() throws Exception {
        Boolean ecmErr = true;
        Integer gears = 4;
        Double remainOilPerc = 25.43;
        ObdInfo obdInfo = new ObdInfo();
        obdInfo.addProp(0x17, ecmErr ? 1 : 0); //ECMErr
        obdInfo.addProp(0x27, gears); //Gears
        obdInfo.addProp(0x2b, (int)(remainOilPerc*100)); //RemainOilPerc
        java.util.Date timestamp = new java.util.Date();
        Values = new HashMap<>();
        mockStatement = new PreparedStatement() {
            @Override
            public ResultSet executeQuery() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int executeUpdate() throws SQLException {
                throw new UnsupportedOperationException("");
            }

            @Override
            public void setNull(int parameterIndex, int sqlType) throws SQLException {
                Values.put(parameterIndex, null);
            }

            @Override
            public void setBoolean(int parameterIndex, boolean x) throws SQLException {
                Values.put(parameterIndex, x);
            }

            @Override
            public void setByte(int parameterIndex, byte x) throws SQLException {
               Values.put(parameterIndex, x);
            }

            @Override
            public void setShort(int parameterIndex, short x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setInt(int parameterIndex, int x) throws SQLException {
                Values.put(parameterIndex, x);
            }

            @Override
            public void setLong(int parameterIndex, long x) throws SQLException {
                Values.put(parameterIndex, x);
            }

            @Override
            public void setFloat(int parameterIndex, float x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setDouble(int parameterIndex, double x) throws SQLException {
                Values.put(parameterIndex, x);
            }

            @Override
            public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setString(int parameterIndex, String x) throws SQLException {
                Values.put(parameterIndex, x);
            }

            @Override
            public void setBytes(int parameterIndex, byte[] x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setDate(int parameterIndex, Date x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setTime(int parameterIndex, Time x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
                Values.put(parameterIndex, x);
            }

            @Override
            public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void clearParameters() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setObject(int parameterIndex, Object x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean execute() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void addBatch() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setRef(int parameterIndex, Ref x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setBlob(int parameterIndex, Blob x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setClob(int parameterIndex, Clob x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setArray(int parameterIndex, Array x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ResultSetMetaData getMetaData() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setURL(int parameterIndex, URL x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ParameterMetaData getParameterMetaData() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setRowId(int parameterIndex, RowId x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setNString(int parameterIndex, String value) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setNClob(int parameterIndex, NClob value) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setClob(int parameterIndex, Reader reader) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setNClob(int parameterIndex, Reader reader) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ResultSet executeQuery(String sql) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int executeUpdate(String sql) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void close() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getMaxFieldSize() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setMaxFieldSize(int max) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getMaxRows() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setMaxRows(int max) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setEscapeProcessing(boolean enable) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getQueryTimeout() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setQueryTimeout(int seconds) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void cancel() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public SQLWarning getWarnings() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void clearWarnings() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setCursorName(String name) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean execute(String sql) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ResultSet getResultSet() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getUpdateCount() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean getMoreResults() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setFetchDirection(int direction) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getFetchDirection() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setFetchSize(int rows) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getFetchSize() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getResultSetConcurrency() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getResultSetType() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void addBatch(String sql) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void clearBatch() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int[] executeBatch() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Connection getConnection() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean getMoreResults(int current) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ResultSet getGeneratedKeys() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int executeUpdate(String sql, String[] columnNames) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean execute(String sql, int[] columnIndexes) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean execute(String sql, String[] columnNames) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getResultSetHoldability() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isClosed() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setPoolable(boolean poolable) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isPoolable() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void closeOnCompletion() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isCloseOnCompletion() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        };
        DataSource dataSource = new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return new Connection() {
                    @Override
                    public Statement createStatement() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public PreparedStatement prepareStatement(String sql) throws SQLException {
                        return mockStatement;
                    }

                    @Override
                    public CallableStatement prepareCall(String sql) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public String nativeSQL(String sql) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setAutoCommit(boolean autoCommit) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public boolean getAutoCommit() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void commit() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void rollback() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void close() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public boolean isClosed() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public DatabaseMetaData getMetaData() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setReadOnly(boolean readOnly) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public boolean isReadOnly() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setCatalog(String catalog) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public String getCatalog() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setTransactionIsolation(int level) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int getTransactionIsolation() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public SQLWarning getWarnings() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void clearWarnings() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Map<String, Class<?>> getTypeMap() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setHoldability(int holdability) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int getHoldability() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Savepoint setSavepoint() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Savepoint setSavepoint(String name) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void rollback(Savepoint savepoint) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
                        return mockStatement;
                    }

                    @Override
                    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Clob createClob() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Blob createBlob() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public NClob createNClob() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public SQLXML createSQLXML() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public boolean isValid(int timeout) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setClientInfo(String name, String value) throws SQLClientInfoException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setClientInfo(Properties properties) throws SQLClientInfoException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public String getClientInfo(String name) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Properties getClientInfo() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setSchema(String schema) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public String getSchema() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void abort(Executor executor) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int getNetworkTimeout() throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public <T> T unwrap(Class<T> iface) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public boolean isWrapperFor(Class<?> iface) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                    
                };
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getLoginTimeout() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        };
        QueryBuilder qb;
        qb = QueryBuilder.create(dataSource, "insert into OBDInfo(\n" +
                "server_time,\n" +
                "/ecmErr, RemainOilPercent, Gears)"
                +"values(\n"+
                ":now,\n"+
                ":ecmErr,:remainOilPercent, :gears");
        qb.setDate("now", timestamp);
        qb.setMap(obdInfo);
        Assert.assertTrue(Values.containsKey(1));
        Assert.assertEquals(timestamp,Values.get(1));
        Assert.assertTrue(Values.containsKey(2));
        Assert.assertEquals(ecmErr, Values.get(2));
        Assert.assertTrue(Values.containsKey(3));
        Assert.assertEquals(remainOilPerc, Values.get(3));
        Assert.assertTrue(Values.containsKey(4));
        Assert.assertEquals(gears, Values.get(4));
    }
}
