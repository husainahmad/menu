package com.harmoni.pos.menu.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps {@link java.time.DayOfWeek} to and from the {@code tinyint} column used by
 * {@code promotion_schedules.day_of_week}.
 * <p>
 * The wire value follows {@link java.time.DayOfWeek#getValue()}, so {@code MONDAY}
 * is {@code 1} up to {@code SUNDAY} being {@code 7}. This matches MySQL's
 * {@code WEEKDAY() + 1} and therefore {@code DAYOFWEEK()} shifted by one, so
 * an index on {@code (day_of_week, start_time, end_time)} stays usable when
 * resolving the windows of a given day.
 * <p>
 * A custom handler is required because MyBatis' default
 * {@code EnumTypeHandler} would map by {@link Enum#name()} and fail to read the
 * numeric column back into the enum.
 *
 * @author husainahmad
 */
@MappedTypes(java.time.DayOfWeek.class)
public class DayOfWeekTypeHandler extends BaseTypeHandler<java.time.DayOfWeek> {

    private static final int FIRST_DAY = 1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, java.time.DayOfWeek parameter,
                                    JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.time.DayOfWeek getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toDayOfWeek(rs.getInt(columnName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.time.DayOfWeek getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toDayOfWeek(rs.getInt(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.time.DayOfWeek getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toDayOfWeek(cs.getInt(columnIndex));
    }

    /**
     * Converts a stored ordinal back into a {@link java.time.DayOfWeek}, mapping any
     * value outside 1..7 to {@code null} so an out-of-range row degrades to an
     * ignored schedule instead of failing the whole query.
     *
     * @param value the stored ordinal, 1 for Monday up to 7 for Sunday
     * @return the matching day of week, or null when the value is out of range
     */
    private static java.time.DayOfWeek toDayOfWeek(int value) {
        if (value < FIRST_DAY || value > java.time.DayOfWeek.values().length) {
            return null;
        }
        return java.time.DayOfWeek.values()[value - FIRST_DAY];
    }
}
