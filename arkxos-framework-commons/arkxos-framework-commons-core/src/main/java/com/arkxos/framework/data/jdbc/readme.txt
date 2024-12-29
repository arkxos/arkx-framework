public DataTable executeDataTable() {
		JdbcTemplate dataAccess = new JdbcTemplate();
		DataTable dataTable = dataAccess.executeDataTable(getSQL(), paramList);
		try {
//			dataAccess.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
		return dataTable;
	}

	
	new JdbcTemplate().executeDataTable(qb.getSQL(), qb.getParams())