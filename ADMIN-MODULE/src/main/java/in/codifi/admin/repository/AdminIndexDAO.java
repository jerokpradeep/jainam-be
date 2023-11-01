package in.codifi.admin.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.admin.model.response.GenericResponse;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AdminIndexDAO {

	@Inject
	DataSource datasource;

	public void truncateIndexvalue() {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = datasource.getConnection();
			String truncateQuery = "TRUNCATE TABLE TBL_INDEX";
			statement = connection.prepareStatement(truncateQuery);
			int rowsAffected = statement.executeUpdate();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("getCountBySource", e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("getCountBySource -" + e);
			}
		}
		return;
	}

}
