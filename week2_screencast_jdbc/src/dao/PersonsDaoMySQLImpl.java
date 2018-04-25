package dao;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import model.Persons;
import static dao.DAOUtility.*;	// to call utility methods as if they belong to this class 

/**
 * Use with MySQL JDBC Driver
 */
public class PersonsDaoMySQLImpl implements PersonsDao {
	
	private DAOFactory	daoFactory;
	// SQL queries
	private static final String SQL_SELECT_ALL = "SELECT * from persons";
	private static final String SQL_SELECT_BY_EMAIL = "SELECT id, registerDate, email, firstName, lastName, birthday FROM Utilisateur WHERE email = ?";	
	private static final String SQL_INSERT_NEW_PERSON = "INSERT INTO pesons (NOW(), email, password, firstname, lastname, birthday) VALUES (?, ?, ?, ?, ?)";
	
	// ctor
	public PersonsDaoMySQLImpl(DAOFactory daoFactory ) {
		this.daoFactory = daoFactory;
	}
	
	
	/**
	 * Utility method to map a row in users.persons and a Persons bean
	 * @param rs A ResultSet cursor
	 * @return	A Persons bean
	 * @throws SQLException
	 */
	private static Persons map( ResultSet rs ) throws SQLException {
		Persons person = new Persons();
		person.setId( rs.getLong( "id" ) );
		person.setRegisterDate( rs.getTimestamp( "registerDate" ) );
		person.setEmail( rs.getString( "email" ) );
		person.setFirstName( rs.getString( "firstName" ) );
		person.setLastName( rs.getString( "lastName" ) );
		person.setBirthday( LocalDate.parse( rs.getString("birthday") ) );
		
		return person;
	}	
	
	
	/*************************************/
	/* DAO Interface implemented methods */
	/*************************************/
	
	public List<Persons> listAll() throws DAOException {
		// get all users and assigned each to a list
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Persons> listPersons = new ArrayList<>();		
		
		try {
			
			connection = daoFactory.getConnection();
			preparedStatement = initPreparedStatement(connection, SQL_SELECT_ALL, true);
			resultSet = preparedStatement.executeQuery();
			
			if( resultSet.next() ) {
				listPersons.add( map(resultSet) );
			}
			
		} catch (SQLException e) {
			throw new DAOException("Failed to select all persons in database. ",  e );
		} finally {
			quietClosure(resultSet, preparedStatement, connection);
		}
		
		return listPersons;		
	}
	

	@Override
	public void create(Persons person, String password) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		
		try {
			
			connection = daoFactory.getConnection();
			preparedStatement = initPreparedStatement( connection, SQL_INSERT_NEW_PERSON, true,
													  person.getEmail(),
													  password,
													  person.getFirstName(),
													  person.getLastName(),
													  person.getBirthday() );
			
			int status = preparedStatement.executeUpdate();
			// Check returned status			
			System.out.println(status + " insert(s)");
			if( status == 0 ) {
				throw new DAOException("Failed to insert a new person. No row added in databaes.");
			}
			
			// Get generated key 'id'
			generatedKeys = preparedStatement.getGeneratedKeys();
			if( generatedKeys.next() ) {
				// Init Persons bean property 'id' with its value 
				person.setId( generatedKeys.getLong( 1 ) );
			}else {
				throw new DAOException("Failed to insert a new Person in database. No generated ID returned.");
			}
			
		} catch (SQLException e) {
			throw new DAOException("Failed to create a new person. ", e);
		}finally {
			quietClosure(preparedStatement, connection);
		}		
	}

	
	@Override
	public Persons find(String email) throws DAOException {		
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Persons person = null;		
		
		try {
			
			connection = daoFactory.getConnection();
			preparedStatement = initPreparedStatement(connection, SQL_SELECT_BY_EMAIL, false, email);
			resultSet = preparedStatement.executeQuery();
			
			if( resultSet.next() ) {
				person = map(resultSet);
			}
			
		} catch (SQLException e) {
			throw new DAOException("Failed to find a person by email. ",  e );
		} finally {
			quietClosure(resultSet, preparedStatement, connection);
		}
		
		return person;
	}

}
