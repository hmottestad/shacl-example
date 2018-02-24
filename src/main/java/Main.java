import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.NotifyingSail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;

import java.io.IOException;

public class Main {

	private final static String ex = "http://example.com/ns#";

	public static void main(String[] args) {

		System.out.println("Loading shacl rules");

		SailRepository shaclRules = getSailRepository("shacl.ttl");
		NotifyingSail underlyingStore = new MemoryStore();
		SailRepository shaclSail = new SailRepository(new ShaclSail(underlyingStore, shaclRules));

		System.out.println("Initializing shacl sail");
		shaclSail.initialize();

		try (SailRepositoryConnection connection = shaclSail.getConnection()) {

			ValueFactory vf = connection.getValueFactory();

			System.out.println("Begin transaction");
			connection.begin();

			System.out.println("Add data");

			IRI person1 = vf.createIRI(ex + "person1");
			connection.add(person1, RDF.TYPE, FOAF.PERSON);
			connection.add(person1, FOAF.AGE, vf.createLiteral("22"));
//			connection.add(person1, FOAF.AGE, vf.createLiteral("73")); // comment this line back in for SHACL violation

			connection.add(person1, FOAF.KNOWS, vf.createIRI(ex+"Samantha"));
			connection.add(person1, FOAF.KNOWS, vf.createIRI(ex+"Bart")); // comment this line out for SHACL violation


			System.out.println("Commit");
			// experimental information about the violation is logged as a warn message
			// .commit() will throw an exception if there is a violation
			connection.commit();
		}

		System.out.println("Done");

	}


	 private static SailRepository getSailRepository(String resourceName) {
		SailRepository sailRepository = new SailRepository(new MemoryStore());
		sailRepository.initialize();
		try (SailRepositoryConnection connection = sailRepository.getConnection()) {
			connection.add(Main.class.getClassLoader().getResourceAsStream(resourceName), "", RDFFormat.TURTLE);
		} catch (IOException | NullPointerException e) {
			System.out.println("Error reading: " + resourceName);
			throw new RuntimeException(e);
		}
		return sailRepository;
	}

}
