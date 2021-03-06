package examples;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppApiCriteriaEx2 {

	private static Logger log = LoggerFactory.getLogger( AppApiCriteriaEx2.class );
	
	public static void main(String[] args) {
		System.out.println("Running AppApiCriteriaEx2 ...");
		
		log.debug("Add new jdbc url property for database bank ...");		
		// if your persistence.xml file does not contain all settings already, you
		// can add vendor settings to a map 
		Properties props = new Properties ();
		props.setProperty("javax.persistence.jdbc.url", "jdbc:h2:./bank");

		log.debug("Create persistence manager");		
		// Second API Criteria example: using WHERE clause for the entities of bank.compte
		// JPQL: SELECT c FROM Compte c WHERE solde >= :value
		// APÏ Criteria ...		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("myApp", props);
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<Compte> cq = cb.createQuery(Compte.class);
		Root<Compte> c = cq.from(Compte.class);
		// Déclaration et typage du paramètre
		ParameterExpression<Double> value = cb.parameter(Double.class);
		// Clause WHERE avec ge = greater or equal than
	 	cq.select(c).where( cb.ge(c.get("solde"), value) );
		TypedQuery<Compte> tq = em.createQuery(cq);
	 	tq.setParameter(value , 50.0);
		List<Compte> comptes = tq.getResultList();
		displayResult(comptes);

		
		log.debug("Close Entity Manager");
		em.close();
		emf.close();
	}

	
	private static void displayResult(List<Compte> comptes) {
		System.out.println(
				String.format(
						"%4s	|	%4s" ,
						"NOM",
						"SOLDE")
		);
		
		for (Compte compte : comptes) {
			System.out.println(
					String.format(
							"%4s	|	%4s" ,
							compte.getNom(),
							compte.getSolde())
			);			
		}
		
	}
	
}
