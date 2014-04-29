

import java.sql.*;

public class Union_useeDB 
{
	public static Connection conn;
	public static String jdbcUrl = "jdbc:mysql://192.168.1.10:3306/union?useUnicode=true&amp;characterEncoding=UTF-8";
	public static String dbName = "root";
	public static String dbPassword = "root";
	public static Connection getConnection() 
	{
		try
		{ 
			if( conn==null)
			{
				//Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
	            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
				conn = (Connection) DriverManager.getConnection(jdbcUrl,dbName,dbPassword);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void closeConnection()
	{
		if( conn!=null)
		{
			try 
			{
				conn.close();
				conn = null;
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	/**  
	  * 1.调用带有输入参数的存储过程  
	  * @param in     stored procedure input parameter value  
	  */  
	public static String executeProcedure(String channelName,String lpdata)
	{
		// 获取连接
		Connection conn = Union_useeDB.getConnection();
		CallableStatement cs = null;
		ResultSet rs = null;
		String result = "";
		try
		{
			// 可以直接传入参数
			// cs = conn.prepareCall("{call sp1(1)}");

			// 也可以用问号代替
			cs = (CallableStatement) conn.prepareCall("{call pro_syncLiveProgram(?,?)}");
			// 设置第一个输入参数的值为110
			cs.setString(1, channelName);
			cs.setString(2, lpdata);
			rs = (ResultSet) cs.executeQuery();
			// 循环输出结果
			// SELECT channelId,liveProgramCount,liveProgramInsertCount, 
			// liveProgramUpdateCount,liveProgramReturnedCount;
			ResultSetMetaData rsmd = rs.getMetaData();   
		    int columnCount = rsmd.getColumnCount(); 
		    result += ("DB sync finnish,");
			while (rs.next()) 
			{
				for (int i=1; i<=columnCount; i++)
		        {   
		            String str = rs.getString(i);
		            switch(i)
					{
					case 1:
						result += ("channelId:" + str + " ");
						break;
					case 2:
						result += ("dataTotalCount:" + str + " ");
						break;
					case 3:
						result += ("insertCount:" + str + " ");
						break;
					case 4:
						result += ("updateCount:" + str + " ");
						break;
					case 5:
						result += ("backUpdateCount:" + str + " ");
						break;
					default:
						break;
					}
		        }
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (cs != null) 
				{
					cs.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}
		return result;
	} 
	
	/**
	 * 2.调用带有输出参数的存储过程
	 * 
	 */
	/*
	public static void callOut()
	{
		Connection conn = Union_useeDB.getConnection();
		CallableStatement cs = null;
		try 
		{
			cs = (CallableStatement) conn.prepareCall("{call test(?)}");
			// 第一个参数的类型为Int
			cs.registerOutParameter(1, Types.INTEGER);
			cs.execute();

			// 得到第一个值
			int i = cs.getInt(1);
			System.out.println(i);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				if (cs != null)
				{
					cs.close();
				}
				if (conn != null) 
				{
					conn.close();
				}
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}
	}
*/
	/**
	 * 3.调用输出结果集的存储过程
	 */
	/*
	public static void callResult() 
	{
		Connection conn = Union_useeDB.getConnection();
		CallableStatement cs = null;
		ResultSet rs = null;
		try 
		{
			cs = (CallableStatement) conn.prepareCall("{call sp6()}");
			rs = (ResultSet) cs.executeQuery();

			// 循环输出结果
			while (rs.next()) 
			{
				System.out.println(rs.getString(1));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if (rs != null)
				{
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}
	}
	*/
}
