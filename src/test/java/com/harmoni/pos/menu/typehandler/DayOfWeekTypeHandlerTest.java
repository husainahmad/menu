package com.harmoni.pos.menu.typehandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DayOfWeekTypeHandlerTest {

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private DayOfWeekTypeHandler typeHandler;

    @BeforeEach
    void setUp() {
        typeHandler = new DayOfWeekTypeHandler();
    }

    @Test
    void setNonNullParameter_shouldWriteOneBasedOrdinal() throws Exception {
        for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
            typeHandler.setNonNullParameter(preparedStatement, 1, day, null);
            verify(preparedStatement).setInt(1, day.getValue());
        }
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).setInt(1, 7);
    }

    @Test
    void getNullableResult_shouldReadOneBasedOrdinalByColumnName() throws Exception {
        for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
            when(resultSet.getInt("day_of_week")).thenReturn(day.getValue());
            assertEquals(day, typeHandler.getNullableResult(resultSet, "day_of_week"));
        }
    }

    @Test
    void getNullableResult_shouldMapMondayToOneAndSundayToSeven() throws Exception {
        when(resultSet.getInt("day_of_week")).thenReturn(1);
        assertEquals(java.time.DayOfWeek.MONDAY, typeHandler.getNullableResult(resultSet, "day_of_week"));

        when(resultSet.getInt("day_of_week")).thenReturn(7);
        assertEquals(java.time.DayOfWeek.SUNDAY, typeHandler.getNullableResult(resultSet, "day_of_week"));
    }

    @Test
    void getNullableResult_shouldReturnNull_whenValueBelowRange() throws Exception {
        when(resultSet.getInt("day_of_week")).thenReturn(0);
        assertNull(typeHandler.getNullableResult(resultSet, "day_of_week"));
    }

    @Test
    void getNullableResult_shouldReturnNull_whenValueAboveRange() throws Exception {
        when(resultSet.getInt("day_of_week")).thenReturn(8);
        assertNull(typeHandler.getNullableResult(resultSet, "day_of_week"));
    }

    @Test
    void getNullableResult_shouldReadByColumnIndex() throws Exception {
        when(resultSet.getInt(3)).thenReturn(4);
        assertEquals(java.time.DayOfWeek.THURSDAY, typeHandler.getNullableResult(resultSet, 3));
    }
}
