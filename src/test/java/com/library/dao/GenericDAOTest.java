package com.library.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GenericDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private TestDAO testDAO;  // A concrete implementation of GenericDAO for testing

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    // Test DAO Implementation for Testing
    private static class TestEntity {
        private int id;
        private String name;

        public TestEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class TestDAO extends GenericDAO<TestEntity> {
        @Override
        protected TestEntity mapResultSetToEntity(ResultSet resultSet) throws SQLException {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            return new TestEntity(id, name);
        }
    }

    @Test
    void testExecuteGetQuery() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("TestName");

        String query = "SELECT * FROM test_table WHERE id = 1";

        // Act
        TestEntity entity = testDAO.executeGetQuery(query);

        // Assert
        assertNotNull(entity);
        assertEquals(1, entity.id);
        assertEquals("TestName", entity.name);

        verify(mockConnection, times(1)).prepareStatement(query);
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getInt("id");
        verify(mockResultSet, times(1)).getString("name");
    }

    
    @Test
    void testExecuteGetAllQuery() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);  // Two rows in the result set
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("name")).thenReturn("TestName1", "TestName2");

        String query = "SELECT * FROM test_table";

        // Act
        List<TestEntity> entities = testDAO.executeGetAllQuery(query);

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals(1, entities.get(0).id);
        assertEquals("TestName1", entities.get(0).name);
        assertEquals(2, entities.get(1).id);
        assertEquals("TestName2", entities.get(1).name);

        verify(mockConnection, times(1)).prepareStatement(query);
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(3)).next(); // 3 times (true, true, false)
    }

    @Test
    void testExecuteUpdateQuery() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);  // Update success (1 row updated)

        String query = "UPDATE test_table SET name = 'UpdatedName' WHERE id = 1";

        // Act
        boolean updateSuccess = testDAO.executeUpdateQuery(query);

        // Assert
        assertTrue(updateSuccess);

        verify(mockConnection, times(1)).prepareStatement(query);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
