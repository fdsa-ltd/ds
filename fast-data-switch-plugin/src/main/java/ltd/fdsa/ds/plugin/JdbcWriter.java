package ltd.fdsa.ds.plugin;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.job.model.Result;
import ltd.fdsa.ds.core.model.Record;
import ltd.fdsa.ds.core.pipeline.Writer;
import ltd.fdsa.ds.core.config.Configuration;
import ltd.fdsa.ds.core.pipeline.impl.AbstractPipeline;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JdbcWriter extends AbstractPipeline implements Writer {
    String driver;
    String url;
    String user;
    String password;
    String sql;
    String table;
    Connection conn;
    Set<String> scheme;

    @Override
    public Result<String> init(Configuration configuration) {
        var result = super.init(configuration);
        if (result.getCode() == 200) {
            this.url = configuration.getString("url");
            this.driver = configuration.getString("driver");
            this.user = configuration.getString("username");
            this.password = configuration.getString("password");
            this.sql = configuration.getString("sql");
            this.table = configuration.getString("table");
            try {
                Class.forName(driver);
                this.conn = DriverManager.getConnection(url, user, password);
                return Result.success();
            } catch (SQLException e) {
                return Result.error(e);
            } catch (ClassNotFoundException e) {
                return Result.error(e);
            }

        }
        return result;
    }


    @Override
    public void collect(Record... records) {
        if (!this.isRunning()) {
            return;
        }
        for (var item : this.nextSteps) {
            item.collect(records);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(this.table);
        sb.append("\n(");
        sb.append(String.join(",", this.scheme));
        sb.append(")\nvalues\n");
        var values = Arrays.stream(records)
                .map(record -> {
                    return "(" + record.entrySet().stream()
                            .map(m -> m == null ? "null" : "'" + m.toString() + "'")
                            .collect(Collectors.joining(",")) + ")";
                }).collect(Collectors.joining(","));
        sb.append(values);
        sb.append(";");

        try (var stmt = conn.createStatement()) {
            // STEP 4: Execute a query
            log.info(  "Creating statement...");
            stmt.addBatch(sb.toString());
            stmt.executeBatch();
        } catch (Exception se) {
            log.error("JdbcTargetPipeline.executeBatch", se);
        }
    }
}
