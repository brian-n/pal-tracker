package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry any) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO time_entries (project_id, user_id, date, hours)" +
                " VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(conn -> {
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, any.getProjectId());
            statement.setLong(2, any.getUserId());
            statement.setDate(3, Date.valueOf(any.getDate()));
            statement.setInt(4, any.getHours());
            return statement;
        }, keyHolder);

        return find(keyHolder.getKey().longValue());
    }



    @Override
    public TimeEntry find(long timeEntryId) {
        try {
            return this.jdbcTemplate.queryForObject("SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?", new TimeEntryRowMapper(), timeEntryId);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
    }

    @Override
    public TimeEntry update(long eq, TimeEntry any) {
        String sql = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?";

        this.jdbcTemplate.update(conn -> {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, any.getProjectId());
            statement.setLong(2, any.getUserId());
            statement.setDate(3, Date.valueOf(any.getDate()));
            statement.setInt(4, any.getHours());
            statement.setLong(5, eq);
            return statement;
        });

        return find(eq);
    }

    @Override
    public void delete(long timeEntryId) {
        this.jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", timeEntryId);
    }

    @Override
    public List<TimeEntry> list() {
        try {
            return this.jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries", new TimeEntryRowMapper());
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
    }

    private class TimeEntryRowMapper implements RowMapper<TimeEntry> {

        @Override
        public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TimeEntry(
                    rs.getLong("id"),
                    rs.getLong("project_id"),
                    rs.getLong("user_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getInt("hours")
            );
        }
    }
}
