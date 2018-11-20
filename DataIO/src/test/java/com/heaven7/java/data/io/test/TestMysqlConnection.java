/*
package com.heaven7.java.data.io.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.QueryRunner;

public class TestMysqlConnection {

	*/
/**
	 * @param args
	 *//*

	public static void main(String[] args) {
		final String table = "class100";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/" + table + "?" + "user=root&password=mysql123456");
			stmt = conn.createStatement();
			boolean result = stmt.execute("CREATE TABLE IF NOT EXISTS User("
					+ "name char(15) not null check(user_Name !='')," + "password char(15) not null,"
					+ "email varchar(20) not null unique," + "phone varchar(20)," + "primary key(name));");
			System.out.println(" result = " + result);

			int changed = stmt.executeUpdate(
					"insert User(name,password,email,phone) values ('heaven7', '123456', '978136772@qq.com','12345678909')");
			System.out.println("insert count = " + changed);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException sex) {
			sex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException sex) {
			}
		}

	}

}*/
